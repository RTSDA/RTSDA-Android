package com.rtsda.appr.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.Date
import java.util.UUID
import javax.inject.Inject

data class PrayerRequestState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class PrayerRequestViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _state = MutableStateFlow(PrayerRequestState())
    val state: StateFlow<PrayerRequestState> = _state.asStateFlow()

    fun submitPrayerRequest(
        name: String,
        email: String,
        request: String,
        isPrivate: Boolean,
        requestType: String
    ) {
        viewModelScope.launch {
            try {
                _state.value = PrayerRequestState(isLoading = true)
                
                val docId = UUID.randomUUID().toString()
                val prayerRequest = hashMapOf(
                    "name" to name,
                    "email" to email,
                    "details" to request,
                    "isConfidential" to isPrivate,
                    "requestType" to requestType,
                    "timestamp" to Timestamp(Date()),
                    "prayedFor" to false
                )

                // Use the same collection name and field names as iOS
                firestore.collection("prayerRequests")
                    .document(docId)
                    .set(prayerRequest)
                    .await()

                _state.value = PrayerRequestState(isSuccess = true)
            } catch (e: Exception) {
                Timber.e(e, "Error submitting prayer request")
                val errorMessage = when {
                    e.message?.contains("permission") == true -> "Permission denied. Please try again later."
                    e.message?.contains("network") == true -> "Network error. Please check your connection."
                    else -> "Error submitting prayer request. Please try again."
                }
                _state.value = PrayerRequestState(error = errorMessage)
            }
        }
    }

    fun resetState() {
        _state.value = PrayerRequestState()
    }
}
