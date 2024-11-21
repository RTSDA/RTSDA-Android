package com.rtsda.appr.ui.screens.admin

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.rtsda.appr.data.model.PrayerRequest
import com.rtsda.appr.data.model.RequestType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminPrayerRequestsUiState(
    val prayerRequests: List<PrayerRequest> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AdminPrayerRequestsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val connectivityManager: ConnectivityManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminPrayerRequestsUiState(isLoading = true))
    val uiState: StateFlow<AdminPrayerRequestsUiState> = _uiState.asStateFlow()

    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    init {
        setupNetworkMonitoring()
        loadPrayerRequests()
    }

    private fun setupNetworkMonitoring() {
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                loadPrayerRequests()
            }

            override fun onLost(network: Network) {
                _uiState.value = _uiState.value.copy(
                    error = "Network connection lost"
                )
            }
        }

        connectivityManager.registerDefaultNetworkCallback(networkCallback!!)
    }

    private fun loadPrayerRequests() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            firestore.collection("prayerRequests")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = e.message
                        )
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val updatedRequests = mutableListOf<PrayerRequest>()
                        
                        for (document in snapshot.documents) {
                            val data = document.data
                            if (data != null) {
                                try {
                                    val requestType = (data["requestType"] as? String)?.let {
                                        RequestType.fromString(it)
                                    } ?: RequestType.OTHER

                                    val prayerRequest = PrayerRequest(
                                        id = document.id,
                                        name = data["name"] as? String ?: "",
                                        email = data["email"] as? String ?: "",
                                        requestText = data["details"] as? String ?: "", 
                                        details = data["details"] as? String ?: "", 
                                        isConfidential = data["isConfidential"] as? Boolean ?: false,
                                        requestType = requestType,
                                        timestamp = data["timestamp"] as? Timestamp ?: Timestamp.now(),
                                        prayedFor = data["prayedFor"] as? Boolean ?: false,
                                        prayedForDate = data["prayedForDate"] as? Timestamp
                                    )
                                    updatedRequests.add(prayerRequest)
                                } catch (e: Exception) {
                                    println("Error processing document ${document.id}: ${e.message}")
                                }
                            }
                        }

                        _uiState.value = _uiState.value.copy(
                            prayerRequests = updatedRequests.sortedByDescending { it.timestampMillis },
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    fun togglePrayedFor(request: PrayerRequest) {
        viewModelScope.launch {
            try {
                firestore.collection("prayerRequests")
                    .document(request.id)
                    .update(
                        mapOf(
                            "prayedFor" to !request.prayedFor,
                            "prayedForDate" to if (!request.prayedFor) Timestamp.now() else null
                        )
                    )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to update prayer request: ${e.message}"
                )
            }
        }
    }

    fun deletePrayerRequest(request: PrayerRequest) {
        viewModelScope.launch {
            try {
                firestore.collection("prayerRequests")
                    .document(request.id)
                    .delete()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to delete prayer request: ${e.message}"
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        networkCallback?.let {
            connectivityManager.unregisterNetworkCallback(it)
        }
    }
}
