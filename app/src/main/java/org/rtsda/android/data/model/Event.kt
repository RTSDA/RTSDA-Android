package org.rtsda.android.data.model

import android.os.Parcelable
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import org.rtsda.android.data.adapter.DateAdapter
import java.util.Date

@Parcelize
data class Event(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("start_time")
    @JsonAdapter(DateAdapter::class)
    val startDate: Date,
    @SerializedName("end_time")
    @JsonAdapter(DateAdapter::class)
    val endDate: Date,
    @SerializedName("location")
    val location: String,
    @SerializedName("location_url")
    val locationUrl: String,
    @SerializedName("image")
    val imageUrl: String,
    @SerializedName("thumbnail")
    val thumbnailUrl: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("reoccuring")
    val reoccuring: String,
    @SerializedName("is_featured")
    val isFeatured: Boolean
) : Parcelable

enum class EventCategory {
    @SerializedName("Ministry")
    MINISTRY,
    @SerializedName("Service")
    SERVICE,
    @SerializedName("Social")
    SOCIAL,
    @SerializedName("Other")
    OTHER
}

enum class EventRecurring {
    @SerializedName("")
    NONE,
    @SerializedName("DAILY")
    DAILY,
    @SerializedName("WEEKLY")
    WEEKLY,
    @SerializedName("BIWEEKLY")
    BIWEEKLY,
    @SerializedName("FIRST_TUESDAY")
    FIRST_TUESDAY
} 