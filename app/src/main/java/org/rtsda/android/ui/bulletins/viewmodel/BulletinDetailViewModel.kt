package org.rtsda.android.ui.bulletins.viewmodel

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.rtsda.android.domain.model.Bulletin
import org.rtsda.android.domain.repository.BulletinRepository
import javax.inject.Inject

@HiltViewModel
class BulletinDetailViewModel @Inject constructor(
    private val bulletinRepository: BulletinRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow<BulletinDetailState>(BulletinDetailState.Loading)
    val state: StateFlow<BulletinDetailState> = _state.asStateFlow()

    private val _events = MutableStateFlow<BulletinDetailEvent>(BulletinDetailEvent.None)
    val events: StateFlow<BulletinDetailEvent> = _events.asStateFlow()

    private var bulletinId: String? = null

    fun setBulletinId(id: String) {
        bulletinId = id
        loadBulletin()
    }

    private fun loadBulletin() {
        viewModelScope.launch {
            try {
                _state.value = BulletinDetailState.Loading
                val bulletin = bulletinRepository.getBulletinById(bulletinId ?: return@launch)
                _state.value = BulletinDetailState.Success(bulletin)
            } catch (e: Exception) {
                _state.value = BulletinDetailState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun onDownloadPdfClicked() {
        val currentState = state.value
        if (currentState is BulletinDetailState.Success) {
            currentState.bulletin.pdfUrl?.let { pdfUrl ->
                try {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(pdfUrl)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                } catch (e: Exception) {
                    _events.value = BulletinDetailEvent.ShowError("Could not open PDF")
                }
            }
        }
    }

    fun clearEvent() {
        _events.value = BulletinDetailEvent.None
    }

    sealed class BulletinDetailState {
        object Loading : BulletinDetailState()
        data class Error(val message: String) : BulletinDetailState()
        data class Success(val bulletin: Bulletin) : BulletinDetailState()
    }

    sealed class BulletinDetailEvent {
        object None : BulletinDetailEvent()
        object NavigateBack : BulletinDetailEvent()
        data class ShowError(val message: String) : BulletinDetailEvent()
    }
} 