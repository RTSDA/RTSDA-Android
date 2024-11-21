package com.rtsda.appr.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.rtsda.appr.data.model.CalendarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val db: FirebaseFirestore
) : ViewModel() {

    private val _events = MutableStateFlow<List<CalendarEvent>>(emptyList())
    val events: StateFlow<List<CalendarEvent>> = _events.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var eventsListener: ListenerRegistration? = null

    init {
        setupEventsListener()
    }

    private fun setupEventsListener() {
        eventsListener = db.collection("events")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                snapshot?.let { querySnapshot ->
                    _events.value = querySnapshot.documents.mapNotNull { doc ->
                        doc.toObject(CalendarEvent::class.java)?.copy(id = doc.id)
                    }.sortedBy { it.startDate }
                }
            }
    }

    fun fetchEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                // Add a minimum refresh time of 1 second
                val startTime = System.currentTimeMillis()
                
                val snapshot = db.collection("events")
                    .get()
                    .await()
                
                val fetchedEvents = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(CalendarEvent::class.java)?.copy(id = doc.id)
                }.sortedBy { it.startDate }
                
                // Ensure minimum refresh time of 1 second
                val elapsedTime = System.currentTimeMillis() - startTime
                if (elapsedTime < 1000) {
                    delay(1000 - elapsedTime)
                }
                
                _events.value = fetchedEvents
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        eventsListener?.remove()
    }
}
