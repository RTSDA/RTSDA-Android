package org.rtsda.android.ui.bulletins

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.rtsda.android.domain.model.Bulletin
import org.rtsda.android.domain.repository.BulletinRepository
import javax.inject.Inject

data class BulletinDetailUiState(
    val bulletin: Bulletin? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class BulletinDetailViewModel @Inject constructor(
    private val repository: BulletinRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BulletinDetailUiState())
    val uiState: StateFlow<BulletinDetailUiState> = _uiState.asStateFlow()

    fun loadBulletin(bulletinId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val bulletin = repository.getBulletinById(bulletinId)
                _uiState.value = _uiState.value.copy(
                    bulletin = bulletin,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Error loading bulletin",
                    isLoading = false
                )
            }
        }
    }

    fun getBaseUrl(): String {
        return repository.getBaseUrl()
    }
} 