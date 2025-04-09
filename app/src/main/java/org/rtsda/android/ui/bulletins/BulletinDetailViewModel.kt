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

sealed class BulletinDetailState {
    object Loading : BulletinDetailState()
    data class Error(val message: String) : BulletinDetailState()
    data class Success(val bulletin: Bulletin) : BulletinDetailState()
}

@HiltViewModel
class BulletinDetailViewModel @Inject constructor(
    private val repository: BulletinRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BulletinDetailState>(BulletinDetailState.Loading)
    val uiState: StateFlow<BulletinDetailState> = _uiState.asStateFlow()

    fun loadBulletin(bulletinId: String) {
        viewModelScope.launch {
            _uiState.value = BulletinDetailState.Loading
            try {
                val bulletin = repository.getBulletinById(bulletinId)
                _uiState.value = BulletinDetailState.Success(bulletin)
            } catch (e: Exception) {
                _uiState.value = BulletinDetailState.Error(e.message ?: "Error loading bulletin")
            }
        }
    }

    fun getBaseUrl(): String {
        return repository.getBaseUrl()
    }
} 