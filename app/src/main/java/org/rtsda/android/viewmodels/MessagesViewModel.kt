package org.rtsda.android.viewmodels

import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.isActive
import org.rtsda.android.domain.model.MediaType
import org.rtsda.android.domain.model.Message
import org.rtsda.android.domain.repository.MessagesRepository
import org.rtsda.android.presentation.messages.LiveStreamStatus
import org.rtsda.android.services.OwnCastService
import org.rtsda.android.services.Result
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val repository: MessagesRepository,
    private val ownCastService: OwnCastService
) : ViewModel() {

    private val _originalMessages = MutableStateFlow<List<Message>>(emptyList())
    private val _filteredMessages = MutableStateFlow<List<Message>>(emptyList())
    val filteredMessages: StateFlow<List<Message>> = _filteredMessages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _playbackState = MutableStateFlow<PlaybackState>(PlaybackState.Idle)
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private val _currentMediaType = MutableStateFlow(MediaType.SERMONS)
    val currentMediaType: StateFlow<MediaType> = _currentMediaType.asStateFlow()

    private val _selectedYear = MutableStateFlow<String?>(null)
    val selectedYear: StateFlow<String?> = _selectedYear.asStateFlow()

    private val _selectedMonth = MutableStateFlow<String?>(null)
    val selectedMonth: StateFlow<String?> = _selectedMonth.asStateFlow()

    private val _availableYears = MutableStateFlow<List<String>>(emptyList())
    val availableYears: StateFlow<List<String>> = _availableYears.asStateFlow()

    private val _availableMonths = MutableStateFlow<List<String>>(emptyList())
    val availableMonths: StateFlow<List<String>> = _availableMonths.asStateFlow()

    private val _liveStreamStatus = MutableStateFlow<LiveStreamStatus?>(null)
    val liveStreamStatus: StateFlow<LiveStreamStatus?> = _liveStreamStatus.asStateFlow()

    private val monthNames = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    private var currentVideoUrl: String? = null
    private var _isPlayerActive = false
    val isPlayerActive: Boolean
        get() = _isPlayerActive

    private var _shouldLaunchPlayer = false
    val shouldLaunchPlayer: Boolean
        get() = _shouldLaunchPlayer

    val isPlayerInPiP: Boolean
        get() = _playbackState.value is PlaybackState.Playing && 
                (_playbackState.value as PlaybackState.Playing).isInPiP

    private var liveStreamUpdateJob: Job? = null

    init {
        Log.d("MessagesViewModel", "Initializing with media type: ${_currentMediaType.value}")
        loadContent()
        startLiveStreamStatusUpdates()
    }

    private fun startLiveStreamStatusUpdates() {
        // Cancel any existing update job
        liveStreamUpdateJob?.cancel()
        
        liveStreamUpdateJob = viewModelScope.launch {
            while (true) {
                if (!isActive) break
                
                try {
                    Log.d("MessagesViewModel", "Checking live stream status...")
                    when (val result = ownCastService.getStreamStatus()) {
                        is Result.Success -> {
                            val status = result.data
                            Log.d("MessagesViewModel", "OwnCast status - online: ${status.online}")
                            if (status.online) {
                                Log.d("MessagesViewModel", "Stream is live! Title: ${status.streamTitle}")
                                _liveStreamStatus.value = LiveStreamStatus(
                                    title = status.streamTitle,
                                    description = "Join us for our live stream",
                                    thumbnailUrl = null
                                )
                            } else {
                                Log.d("MessagesViewModel", "Stream is not live")
                                _liveStreamStatus.value = null
                            }
                        }
                        is Result.Error -> {
                            Log.e("MessagesViewModel", "Error checking stream status: ${result.exception.message}")
                            // Don't update the status on error, keep the existing one
                        }
                    }
                } catch (e: Exception) {
                    Log.e("MessagesViewModel", "Error updating live stream status", e)
                    // Don't update the status on error, keep the existing one
                }
                delay(5000) // Update every 5 seconds
            }
        }
    }

    fun loadContent(mediaType: MediaType = currentMediaType.value) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                Log.d("MessagesViewModel", "Loading content for media type: $mediaType")
                
                val messages = repository.getMessages(mediaType)
                Log.d("MessagesViewModel", "Loaded ${messages.size} messages")
                _originalMessages.value = messages
                
                // Only apply filters if we have a year or month selected
                if (selectedYear.value != null || selectedMonth.value != null) {
                    applyFilters()
                } else {
                    // If no filters are selected, show all messages
                    _filteredMessages.value = messages
                }
                
                updateAvailableFilters(messages)
            } catch (e: Exception) {
                Log.e("MessagesViewModel", "Error loading content", e)
                _error.value = e.message ?: "Error loading content"
                // Keep the existing messages if there was an error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshContent() {
        // Don't refresh if we're already loading
        if (_isLoading.value) {
            return
        }
        loadContent()
    }

    fun selectMessage(message: Message) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                // If there's an active player, close it before starting a new one
                if (isPlayerActive) {
                    // If we're in PiP mode, we need to close the PiP window
                    if (isPlayerInPiP) {
                        closeExistingPiP()
                        // Add a longer delay to ensure PiP is fully closed
                        kotlinx.coroutines.delay(500)
                    }
                    onPlayerClosed()
                    // Add a small delay to ensure the previous player is fully closed
                    kotlinx.coroutines.delay(100)
                }

                if (message.isLiveStream) {
                    // For live streams, use the OwnCast service
                    when (val result = ownCastService.getStreamStatus()) {
                        is Result.Success -> {
                            if (result.data.online) {
                                val streamUrl = ownCastService.getStreamUrl()
                                if (streamUrl != null) {
                                    currentVideoUrl = streamUrl
                                    _isPlayerActive = true
                                    _shouldLaunchPlayer = true
                                    _playbackState.value = PlaybackState.Playing(streamUrl, false)
                                } else {
                                    _error.value = "Failed to get stream URL"
                                }
                            } else {
                                _error.value = "Stream is not currently live"
                            }
                        }
                        is Result.Error -> {
                            _error.value = "Failed to get stream status: ${result.exception.message}"
                        }
                    }
                } else {
                    // For archived content, use the repository
                    val videoUrl = repository.getVideoUrl(message.id)
                    currentVideoUrl = videoUrl
                    _isPlayerActive = true
                    _shouldLaunchPlayer = true
                    _playbackState.value = PlaybackState.Playing(videoUrl, false)
                }
            } catch (e: Exception) {
                Log.e("MessagesViewModel", "Error selecting message", e)
                _error.value = "Failed to play video: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun closeExistingPiP() {
        if (isPlayerActive && isPlayerInPiP) {
            // Send a broadcast to close the PiP window
            val intent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS).apply {
                putExtra("reason", "close_pip")
            }
            android.app.Activity.RESULT_CANCELED.let { resultCode ->
                android.content.Intent().also { data ->
                    android.content.Intent.FLAG_ACTIVITY_NEW_TASK.let { flags ->
                        android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP.let { clearFlags ->
                            android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP.let { singleTop ->
                                android.content.Intent.FLAG_ACTIVITY_NEW_TASK or
                                        android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                        android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
                            }
                        }
                    }
                }
            }
        }
    }

    fun onPlayerClosed() {
        _isPlayerActive = false
        _playbackState.value = PlaybackState.Idle
        currentVideoUrl = null
    }

    fun onPlayerLaunched() {
        _shouldLaunchPlayer = false
    }

    fun setMediaType(mediaType: MediaType) {
        Log.d("MessagesViewModel", "Setting media type to: $mediaType")
        _currentMediaType.value = mediaType
        _selectedYear.value = null
        _selectedMonth.value = null
        _availableMonths.value = emptyList()
        _availableYears.value = emptyList()
        _filteredMessages.value = emptyList()
        loadContent(mediaType)
    }

    fun setYear(year: String?) {
        Log.d("MessagesViewModel", "Setting year to: $year")
        
        // If the year is already selected, deselect it
        if (year == _selectedYear.value) {
            Log.d("MessagesViewModel", "Deselecting year: $year")
            _selectedYear.value = null
            _selectedMonth.value = null
            _availableMonths.value = emptyList()
        } else {
            Log.d("MessagesViewModel", "Selecting year: $year")
            // Store the current month selection
            val currentMonth = _selectedMonth.value
            _selectedYear.value = year
            
            if (year != null) {
                updateMonthsForYear(year)
                // If the current month is available in the new year, keep it selected
                if (currentMonth != null && _availableMonths.value.contains(currentMonth)) {
                    _selectedMonth.value = currentMonth
                } else {
                    _selectedMonth.value = null
                }
            } else {
                _availableMonths.value = emptyList()
                _selectedMonth.value = null
            }
        }
        
        applyFilters()
    }

    fun setMonth(month: String?) {
        Log.d("MessagesViewModel", "Setting month to: $month")
        // Only allow month selection if we have a year selected
        if (month != null && selectedYear.value == null) {
            Log.d("MessagesViewModel", "Cannot set month without a year selected")
            return
        }
        
        // If the month is already selected, deselect it
        if (month == _selectedMonth.value) {
            Log.d("MessagesViewModel", "Deselecting month: $month")
            _selectedMonth.value = null
        } else {
            Log.d("MessagesViewModel", "Selecting month: $month")
            _selectedMonth.value = month
        }
        
        applyFilters()
    }

    fun resetFilters() {
        Log.d("MessagesViewModel", "Resetting all filters")
        _selectedYear.value = null
        _selectedMonth.value = null
        _availableMonths.value = emptyList()
        _availableYears.value = emptyList()
        
        // Force a reload of the content to ensure clean state
        loadContent()
    }

    fun applyFilters() {
        val year = _selectedYear.value
        val month = _selectedMonth.value
        val allMessages = _originalMessages.value
        val currentType = _currentMediaType.value

        Log.d("MessagesViewModel", "Applying filters - Media Type: $currentType, Year: $year, Month: $month")
        Log.d("MessagesViewModel", "Total messages before filtering: ${allMessages.size}")

        // First filter by media type
        val typeFiltered = allMessages.filter { message ->
            when (currentType) {
                MediaType.LIVESTREAMS -> message.isLiveStream
                MediaType.SERMONS -> !message.isLiveStream
                else -> true
            }.also { matches ->
                Log.d("MessagesViewModel", "Message: ${message.title}, isLiveStream: ${message.isLiveStream}, matchesType: $matches")
            }
        }

        Log.d("MessagesViewModel", "Messages after media type filtering: ${typeFiltered.size}")

        // Then filter by date components
        val newFilteredMessages = typeFiltered.filter { message ->
            // Extract year and month from the date string (e.g., "March 22nd 2025")
            val dateParts = message.date.split(" ")
            if (dateParts.size < 3) {
                Log.d("MessagesViewModel", "Invalid date format: ${message.date}")
                return@filter true // Don't filter out messages with invalid dates
            }
            
            val messageMonth = dateParts[0]
            val messageYear = dateParts[2]

            val matches = when {
                year != null && month != null -> {
                    // If both year and month are selected, only show messages that match both
                    val yearMatch = messageYear == year
                    val monthMatch = messageMonth == month
                    Log.d("MessagesViewModel", "Checking message - Title: ${message.title}, Year match: $yearMatch, Month match: $monthMatch")
                    yearMatch && monthMatch
                }
                year != null -> {
                    // If only year is selected, show all messages for that year
                    val yearMatch = messageYear == year
                    Log.d("MessagesViewModel", "Checking message - Title: ${message.title}, Year match: $yearMatch")
                    yearMatch
                }
                month != null -> {
                    // If only month is selected, show all messages for that month across all years
                    val monthMatch = messageMonth == month
                    Log.d("MessagesViewModel", "Checking message - Title: ${message.title}, Month match: $monthMatch")
                    monthMatch
                }
                else -> true
            }
            
            if (matches) {
                Log.d("MessagesViewModel", "Message matches filter - Title: ${message.title}, Date: ${message.date}")
            }
            matches
        }

        Log.d("MessagesViewModel", "Final filtered messages count: ${newFilteredMessages.size}")
        _filteredMessages.value = newFilteredMessages
    }

    private fun updateMonthsForYear(year: String) {
        // Get all messages for the selected year
        val messagesForYear = _originalMessages.value
            .filter { message -> 
                val dateParts = message.date.split(" ")
                dateParts.size >= 3 && dateParts.last() == year
            }
        
        Log.d("MessagesViewModel", "Messages for year $year:")
        messagesForYear.forEach { message ->
            Log.d("MessagesViewModel", "Title: ${message.title}, Date: ${message.date}")
        }
        
        // Get all months that have messages for the selected year
        val monthsWithMessages = messagesForYear
            .map { it.date.split(" ").first() }
            .distinct()
        
        Log.d("MessagesViewModel", "Raw months found: $monthsWithMessages")
        
        // Sort months according to the predefined order
        val sortedMonths = monthNames.filter { it in monthsWithMessages }
        
        Log.d("MessagesViewModel", "Available months for year $year: $sortedMonths")
        _availableMonths.value = sortedMonths
    }

    private fun updateAvailableFilters(messages: List<Message>) {
        val years = messages
            .filter { message -> 
                val dateParts = message.date.split(" ")
                dateParts.size >= 3
            }
            .map { it.date.split(" ").last() } // Get the year from the date string
            .distinct()
            .sortedDescending()
        
        Log.d("MessagesViewModel", "Available years: $years")
        _availableYears.value = years
    }

    fun updatePlaybackState(state: PlaybackState) {
        _playbackState.value = state
        _isPlayerActive = state is PlaybackState.Playing
    }

    sealed class PlaybackState {
        object Idle : PlaybackState()
        data class Playing(val videoUrl: String, val isInPiP: Boolean = false) : PlaybackState()
        data class Error(val message: String) : PlaybackState()
    }

    override fun onCleared() {
        super.onCleared()
        liveStreamUpdateJob?.cancel()
    }
} 