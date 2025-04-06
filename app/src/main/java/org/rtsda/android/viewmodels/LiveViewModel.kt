package org.rtsda.android.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.rtsda.android.api.OwnCastStatus
import org.rtsda.android.services.OwnCastService
import org.rtsda.android.services.Result
import javax.inject.Inject

@HiltViewModel
class LiveViewModel @Inject constructor(
    private val ownCastService: OwnCastService
) : ViewModel() {

    private val _uiState = MutableStateFlow<LiveUiState>(LiveUiState.Loading)
    val uiState: StateFlow<LiveUiState> = _uiState.asStateFlow()

    private val _streamUrl = MutableStateFlow<String?>(null)
    val streamUrl: StateFlow<String?> = _streamUrl.asStateFlow()

    init {
        startStatusPolling()
    }

    private fun startStatusPolling() {
        viewModelScope.launch {
            while (true) {
                checkStreamStatus()
                delay(10000) // Poll every 10 seconds
            }
        }
    }

    private suspend fun checkStreamStatus() {
        when (val result = ownCastService.getStreamStatus()) {
            is Result.Success -> {
                val status = result.data
                if (status.online) {
                    _streamUrl.value = ownCastService.getStreamUrl()
                    _uiState.value = LiveUiState.Streaming(status)
                } else {
                    _streamUrl.value = null
                    _uiState.value = LiveUiState.Offline(status)
                }
            }
            is Result.Error -> {
                _streamUrl.value = null
                _uiState.value = LiveUiState.Error(
                    result.exception.localizedMessage ?: "Failed to check stream status"
                )
            }
        }
    }

    fun retryLoading() {
        _uiState.value = LiveUiState.Loading
        viewModelScope.launch {
            checkStreamStatus()
        }
    }
}

sealed class LiveUiState {
    data object Loading : LiveUiState()
    data class Streaming(val status: OwnCastStatus) : LiveUiState()
    data class Offline(val status: OwnCastStatus) : LiveUiState()
    data class Error(val message: String) : LiveUiState()
} 