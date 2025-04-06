package org.rtsda.android.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Event(
    val id: String,
    val title: String,
    val description: String,
    val startDate: Date,
    val endDate: Date,
    val location: String,
    val imageUrl: String? = null,
    val locationLink: String?,
    val created: String,
    val updated: String
) : Parcelable

enum class EventCategory {
    SERVICE,
    SOCIAL,
    MINISTRY,
    OTHER
}

enum class ReoccurringType {
    NONE,
    DAILY,
    WEEKLY,
    BIWEEKLY,
    FIRST_TUESDAY
} 