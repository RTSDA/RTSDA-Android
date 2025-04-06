package org.rtsda.android.viewmodels

sealed class PlaybackState {
    object Idle : PlaybackState()
    data class Playing(val streamUrl: String) : PlaybackState()
    data class Error(val message: String) : PlaybackState()
} 