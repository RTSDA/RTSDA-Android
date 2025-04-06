package org.rtsda.android.presentation.bulletins

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

data class BulletinsUiState(
    val bulletins: List<Bulletin> = emptyList(),
    val selectedBulletin: Bulletin? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class BulletinsViewModel @Inject constructor(
    private val repository: BulletinRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BulletinsUiState())
    val uiState: StateFlow<BulletinsUiState> = _uiState.asStateFlow()

    init {
        loadBulletins()
    }

    fun loadBulletins() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val bulletins = repository.getBulletins()
                _uiState.value = _uiState.value.copy(
                    bulletins = bulletins,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Error loading bulletins",
                    isLoading = false
                )
            }
        }
    }

    fun selectBulletin(bulletin: Bulletin) {
        _uiState.value = _uiState.value.copy(selectedBulletin = bulletin)
    }
} 