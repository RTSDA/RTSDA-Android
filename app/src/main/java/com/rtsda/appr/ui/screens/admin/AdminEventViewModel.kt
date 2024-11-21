package com.rtsda.appr.ui.screens.admin

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.rtsda.appr.data.model.CalendarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

sealed class EventUiState {
    object Loading : EventUiState()
    data class Success(val events: List<CalendarEvent>) : EventUiState()
    data class Error(val message: String) : EventUiState()
}

@HiltViewModel
class AdminEventViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    @ApplicationContext private val context: Context
) : ViewModel() {

    var uiState by mutableStateOf<EventUiState>(EventUiState.Loading)
        private set

    private val _isOnline = MutableStateFlow(false)
    val isOnline: StateFlow<Boolean> = _isOnline

    private val _eventToEdit = mutableStateOf<CalendarEvent?>(null)
    val eventToEdit: CalendarEvent? get() = _eventToEdit.value

    private var eventsListener: ListenerRegistration? = null
    private lateinit var connectivityManager: ConnectivityManager
    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    init {
        setupNetworkCallback()
        // Ensure we start in Loading state
        uiState = EventUiState.Loading
        setupEventsListener()
    }

    override fun onCleared() {
        super.onCleared()
        eventsListener?.remove()
        networkCallback?.let { callback ->
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }

    private fun setupNetworkCallback() {
        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                viewModelScope.launch {
                    _isOnline.emit(true)
                }
            }

            override fun onLost(network: Network) {
                viewModelScope.launch {
                    _isOnline.emit(false)
                }
            }
        }

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback!!)
        
        // Initial network state
        _isOnline.value = connectivityManager.activeNetwork != null
    }

    fun updateEventToEdit(event: CalendarEvent?) {
        _eventToEdit.value = event
    }

    fun addEvent(event: CalendarEvent) {
        viewModelScope.launch {
            try {
                uiState = EventUiState.Loading
                
                val eventWithId = if (event.id.isEmpty()) {
                    event.copy(id = firestore.collection("events").document().id)
                } else {
                    event
                }
                
                firestore.collection("events")
                    .document(eventWithId.id)
                    .set(eventWithId.toMap())
                    .await()
                
                // Success state will be updated by the listener
            } catch (e: Exception) {
                uiState = EventUiState.Error(e.localizedMessage ?: "Error adding event")
            }
        }
    }

    fun updateEvent(event: CalendarEvent) {
        viewModelScope.launch {
            try {
                uiState = EventUiState.Loading
                
                firestore.collection("events")
                    .document(event.id)
                    .set(event.toMap())
                    .await()
                
                // If it's a recurring event, update future instances
                if (event.recurrenceType != com.rtsda.appr.data.model.RecurrenceType.NONE) {
                    val futureInstances = firestore.collection("events")
                        .whereEqualTo("parentEventId", event.id)
                        .whereGreaterThanOrEqualTo("startDate", event.startDate)
                        .get()
                        .await()
                    
                    futureInstances.documents.forEach { doc ->
                        val instance = CalendarEvent.fromDocument(doc)
                        if (instance != null) {
                            val timeDiff = instance.startDate - event.startDate
                            val updatedInstance = instance.copy(
                                title = event.title,
                                description = event.description,
                                location = event.location,
                                endDate = event.endDate + timeDiff
                            )
                            firestore.collection("events")
                                .document(doc.id)
                                .set(updatedInstance.toMap())
                                .await()
                        }
                    }
                }
                
                // Success state will be updated by the listener
            } catch (e: Exception) {
                uiState = EventUiState.Error(e.localizedMessage ?: "Error updating event")
            }
        }
    }

    fun deleteEvent(event: CalendarEvent) {
        viewModelScope.launch {
            try {
                uiState = EventUiState.Loading
                
                firestore.collection("events")
                    .document(event.id)
                    .delete()
                    .await()
                
                // Success state will be updated by the listener
            } catch (e: Exception) {
                uiState = EventUiState.Error(e.localizedMessage ?: "Error deleting event")
            }
        }
    }

    fun setupEventsListener() {
        // Don't set to Loading if we're already loading
        if (uiState !is EventUiState.Loading) {
            uiState = EventUiState.Loading
        }
        
        eventsListener?.remove()
        
        eventsListener = firestore.collection("events")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    uiState = EventUiState.Error(error.localizedMessage ?: "Error loading events")
                    return@addSnapshotListener
                }

                val events = snapshot?.documents?.mapNotNull { doc ->
                    CalendarEvent.fromDocument(doc)
                } ?: emptyList()

                uiState = EventUiState.Success(events)
            }
    }
}
