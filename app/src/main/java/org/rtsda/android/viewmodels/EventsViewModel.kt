package org.rtsda.android.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.rtsda.android.data.model.Event
import org.rtsda.android.data.remote.PocketBaseApi
import javax.inject.Inject

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val api: PocketBaseApi
) : ViewModel() {

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val response = api.getEvents()
                if (response.isSuccessful && response.body() != null) {
                    _events.value = response.body()!!.items
                } else {
                    _error.value = "Failed to load events"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
} 