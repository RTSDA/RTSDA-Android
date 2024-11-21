package com.rtsda.appr.ui.screens.admin

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.rtsda.appr.data.model.CalendarEvent
import com.rtsda.appr.data.model.RecurrenceType
import com.rtsda.appr.repositories.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

data class AdminEventUiState(
    val events: List<CalendarEvent> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class EventValidationState(
    val hasStartDate: Boolean = false,
    val hasEndDate: Boolean = false,
    val hasTitle: Boolean = false,
    val hasLocation: Boolean = false,
    val isEndDateAfterStart: Boolean = false
) {
    val isValid: Boolean
        get() = hasStartDate && hasEndDate && hasTitle && hasLocation && isEndDateAfterStart
}

@HiltViewModel
class AdminEventViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val eventRepository: EventRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminEventUiState(isLoading = true))
    val uiState: StateFlow<AdminEventUiState> = _uiState.asStateFlow()

    private val _eventToEdit = mutableStateOf<CalendarEvent?>(null)
    val eventToEdit: CalendarEvent? get() = _eventToEdit.value

    private val _validationState = mutableStateOf(EventValidationState())
    val validationState: EventValidationState get() = _validationState.value

    private val _error = mutableStateOf<String?>(null)
    val error: String? get() = _error.value

    init {
        setupEventsListener()
    }

    fun validateEvent(event: CalendarEvent): Boolean {
        _validationState.value = EventValidationState(
            hasStartDate = true, // Assuming Timestamp is non-null in the model
            hasEndDate = event.endDate != null,
            hasTitle = event.title.isNotBlank(),
            hasLocation = event.location.isNotBlank(),
            isEndDateAfterStart = event.endDate?.let { end ->
                end.seconds > event.startDate.seconds
            } ?: false
        )
        return _validationState.value.isValid
    }

    fun updateEventToEdit(event: CalendarEvent?) {
        _eventToEdit.value = event
        if (event != null) {
            validateEvent(event)
        } else {
            _validationState.value = EventValidationState()
        }
    }

    fun loadEvent(eventId: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                
                val doc = firestore.collection("events")
                    .document(eventId)
                    .get()
                    .await()
                
                val event = CalendarEvent.fromDocument(doc)
                if (event != null) {
                    _eventToEdit.value = event
                    _uiState.update { it.copy(isLoading = false, error = null) }
                } else {
                    _error.value = "Event not found"
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load event"
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error loading event"
                    )
                }
            }
        }
    }

    fun addEvent(event: CalendarEvent) {
        if (!validateEvent(event)) {
            _uiState.update { 
                it.copy(error = "Please fill all required fields and ensure end date is after start date")
            }
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                
                val eventWithId = if (event.id.isEmpty()) {
                    event.copy(id = firestore.collection("events").document().id)
                } else {
                    event
                }
                
                firestore.collection("events")
                    .document(eventWithId.id)
                    .set(eventWithId.toMap())
                    .await()
                
                _uiState.update { it.copy(isLoading = false, error = null) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error adding event"
                    )
                }
            }
        }
    }

    fun updateEvent(event: CalendarEvent) {
        if (!validateEvent(event)) {
            _uiState.update { 
                it.copy(error = "Please fill all required fields and ensure end date is after start date")
            }
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                
                firestore.collection("events")
                    .document(event.id)
                    .set(event.toMap())
                    .await()
                
                _uiState.update { it.copy(isLoading = false, error = null) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error updating event"
                    )
                }
            }
        }
    }

    fun setupEventsListener() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                
                val snapshot = firestore.collection("events")
                    .get()
                    .await()
                
                val events = snapshot.documents.mapNotNull { doc ->
                    CalendarEvent.fromDocument(doc)
                }.sortedBy { it.startDate.seconds }
                
                _uiState.update { 
                    it.copy(
                        events = events,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error loading events"
                    )
                }
            }
        }
    }

    fun deleteEvent(event: CalendarEvent) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                
                firestore.collection("events")
                    .document(event.id)
                    .delete()
                    .await()
                
                _uiState.update { currentState ->
                    currentState.copy(
                        events = currentState.events.filter { it.id != event.id },
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error deleting event"
                    )
                }
            }
        }
    }

    fun clearCache() {
        _eventToEdit.value = null
        _error.value = null
    }
}
