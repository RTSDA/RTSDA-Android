package org.rtsda.android.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Message(
    val id: String,
    val title: String,
    val speaker: String,
    val videoUrl: String?,
    val audioUrl: String?,
    val thumbnailUrl: String?,
    val date: Date,
    val description: String,
    val created: String,
    val updated: String,
    val isLiveStream: Boolean
) : Parcelable {
    val formattedDate: String
        get() = android.text.format.DateFormat.format("MMM d, yyyy", date).toString()
} 