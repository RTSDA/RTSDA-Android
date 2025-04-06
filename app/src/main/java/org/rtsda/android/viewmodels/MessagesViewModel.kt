package org.rtsda.android.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.rtsda.android.data.MediaType
import org.rtsda.android.data.model.Message
import org.rtsda.android.data.MessagesRepository
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
    private var isPlayerActive = false
    val shouldLaunchPlayer: Boolean
        get() = _shouldLaunchPlayer
    private var _shouldLaunchPlayer = false

    init {
        loadContent()
        startLiveStreamStatusUpdates()
    }

    private fun startLiveStreamStatusUpdates() {
        viewModelScope.launch {
            while (true) {
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
                            _liveStreamStatus.value = null
                        }
                    }
                } catch (e: Exception) {
                    Log.e("MessagesViewModel", "Error updating live stream status", e)
                }
                kotlinx.coroutines.delay(5000) // Update every 5 seconds
            }
        }
    }

    fun loadContent(mediaType: MediaType = currentMediaType.value) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val messages = repository.getMessages(mediaType)
                _originalMessages.value = messages
                _filteredMessages.value = messages
                updateAvailableFilters(messages)
                
                // Only apply filters if we have a year or month selected
                if (selectedYear.value != null || selectedMonth.value != null) {
                    applyFilters()
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error loading content"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshContent() {
        loadContent()
    }

    fun selectMessage(message: Message) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                // Force a new player launch by first closing any existing player
                onPlayerClosed()

                if (message.isLiveStream) {
                    // For live streams, use the OwnCast service
                    when (val result = ownCastService.getStreamStatus()) {
                        is Result.Success -> {
                            if (result.data.online) {
                                val streamUrl = ownCastService.getStreamUrl()
                                currentVideoUrl = streamUrl
                                isPlayerActive = true
                                _shouldLaunchPlayer = true
                                _playbackState.value = PlaybackState.Playing(streamUrl)
                            } else {
                                _error.value = "Stream is not currently live"
                            }
                        }
                        is Result.Error -> {
                            _error.value = "Failed to get stream status: ${result.exception.message}"
                        }
                    }
                } else {
                    // For regular messages, use the repository
                    val videoUrl = repository.getVideoUrl(message.id)
                    currentVideoUrl = videoUrl
                    isPlayerActive = true
                    _shouldLaunchPlayer = true
                    _playbackState.value = PlaybackState.Playing(videoUrl)
                }
            } catch (e: Exception) {
                _error.value = "Failed to get playback info: ${e.message}"
                _playbackState.value = PlaybackState.Error(e.message ?: "Unknown error")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onPlayerClosed() {
        android.util.Log.d("MessagesViewModel", "onPlayerClosed: isPlayerActive=$isPlayerActive, shouldLaunchPlayer=$_shouldLaunchPlayer")
        isPlayerActive = false
        _shouldLaunchPlayer = false
        _playbackState.value = PlaybackState.Idle
        currentVideoUrl = null  // Reset the current video URL
    }

    fun onPlayerLaunched() {
        android.util.Log.d("MessagesViewModel", "onPlayerLaunched: isPlayerActive=$isPlayerActive, shouldLaunchPlayer=$_shouldLaunchPlayer")
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
        _selectedYear.value = null
        _selectedMonth.value = null
        _availableMonths.value = emptyList()
        applyFilters()
    }

    fun applyFilters() {
        val year = _selectedYear.value
        val month = _selectedMonth.value
        val allMessages = _originalMessages.value

        Log.d("MessagesViewModel", "Applying filters - Year: $year, Month: $month")
        Log.d("MessagesViewModel", "Total messages before filtering: ${allMessages.size}")
        Log.d("MessagesViewModel", "All messages:")
        allMessages.forEach { message ->
            Log.d("MessagesViewModel", "Title: ${message.title}, Date: ${message.date}")
        }

        // First filter by media type
        val typeFiltered = allMessages.filter { message ->
            val isLiveStream = _currentMediaType.value == MediaType.LIVESTREAMS
            message.isLiveStream == isLiveStream
        }

        Log.d("MessagesViewModel", "Messages after media type filtering: ${typeFiltered.size}")
        Log.d("MessagesViewModel", "Messages after media type filtering:")
        typeFiltered.forEach { message ->
            Log.d("MessagesViewModel", "Title: ${message.title}, Date: ${message.date}")
        }

        // Then filter by date components
        val newFilteredMessages = typeFiltered.filter { message ->
            // Extract year and month from the date string (e.g., "March 22nd 2025")
            val dateParts = message.date.split(" ")
            if (dateParts.size < 3) {
                Log.d("MessagesViewModel", "Invalid date format: ${message.date}")
                return@filter false
            }
            
            val messageMonth = dateParts[0]
            val messageYear = dateParts[2]

            Log.d("MessagesViewModel", "Checking message - Title: ${message.title}, Date: ${message.date}, Year: $messageYear, Month: $messageMonth")

            val matches = when {
                year != null && month != null -> {
                    // If both year and month are selected, only show messages that match both
                    val yearMatch = messageYear == year
                    val monthMatch = messageMonth == month
                    Log.d("MessagesViewModel", "Year match: $yearMatch, Month match: $monthMatch")
                    yearMatch && monthMatch
                }
                year != null -> {
                    // If only year is selected, show all messages for that year
                    val yearMatch = messageYear == year
                    Log.d("MessagesViewModel", "Year match: $yearMatch")
                    yearMatch
                }
                month != null -> {
                    // If only month is selected, show all messages for that month across all years
                    val monthMatch = messageMonth == month
                    Log.d("MessagesViewModel", "Month match: $monthMatch")
                    monthMatch
                }
                else -> true
            }
            
            if (matches) {
                Log.d("MessagesViewModel", "Message matches filter - Title: ${message.title}, Date: ${message.date}")
            }
            matches
        }

        Log.d("MessagesViewModel", "Messages after filtering: ${newFilteredMessages.size}")
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

    sealed class PlaybackState {
        object Idle : PlaybackState()
        data class Playing(val videoUrl: String) : PlaybackState()
        data class Error(val message: String) : PlaybackState()
    }
} 