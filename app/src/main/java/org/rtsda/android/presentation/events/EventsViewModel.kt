package org.rtsda.android.presentation.events

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.rtsda.android.data.model.Event
import org.rtsda.android.data.service.EventService
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val eventService: EventService
) : ViewModel() {

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadEvents()
    }

    fun loadEvents() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                Log.d("EventsViewModel", "Loading events...")
                val response = eventService.getEvents()
                Log.d("EventsViewModel", "Response code: ${response.code()}")
                
                if (response.isSuccessful) {
                    response.body()?.let { eventResponse ->
                        Log.d("EventsViewModel", "Received ${eventResponse.items.size} events")
                        
                        // Get current time
                        val now = Date()
                        Log.d("EventsViewModel", "Current time: $now")
                        
                        // Filter events that haven't ended yet and sort by start date
                        val filteredEvents = eventResponse.items
                            .filter { event -> 
                                val isAfterNow = event.endDate.after(now)
                                Log.d("EventsViewModel", "Event ${event.title} end date: ${event.endDate}, isAfterNow: $isAfterNow")
                                isAfterNow
                            }
                            .sortedBy { it.startDate.time }
                        
                        Log.d("EventsViewModel", "Filtered to ${filteredEvents.size} events")
                        _events.value = filteredEvents
                    } ?: run {
                        Log.e("EventsViewModel", "Response body is null")
                        _error.value = "No events found"
                    }
                } else {
                    Log.e("EventsViewModel", "Failed to load events: ${response.code()}")
                    _error.value = "Failed to load events: ${response.code()}"
                }
            } catch (e: Exception) {
                Log.e("EventsViewModel", "Error loading events", e)
                _error.value = "Error loading events: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun filterEvents(category: String? = null) {
        val currentEvents = _events.value
        if (category == null) {
            _events.value = currentEvents
            return
        }

        _events.value = currentEvents.filter { event ->
            event.category.equals(category, ignoreCase = true)
        }
    }
} 