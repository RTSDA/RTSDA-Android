package org.rtsda.android.domain.model

data class Message(
    val id: String,
    val title: String,
    val speaker: String,
    val videoUrl: String,
    val thumbnailUrl: String?,
    val date: String,
    val description: String,
    val isLiveStream: Boolean
) 