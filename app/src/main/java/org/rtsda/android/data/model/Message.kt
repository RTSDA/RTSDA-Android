package org.rtsda.android.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
data class Message(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("speaker")
    val speaker: String,
    @SerializedName("videoUrl")
    val videoUrl: String,
    @SerializedName("thumbnailUrl")
    val thumbnailUrl: String?,
    @SerializedName("date")
    val date: String, // ISO8601 formatted date string
    @SerializedName("description")
    val description: String,
    @SerializedName("isLiveStream")
    val isLiveStream: Boolean = false
) : Parcelable {
    private val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("America/New_York")
    }
    
    private val displayDateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("America/New_York")
    }
    
    val formattedDate: String
        get() = try {
            val parsedDate = inputDateFormat.parse(date)
            displayDateFormat.format(parsedDate)
        } catch (e: Exception) {
            date // Return original string if parsing fails
        }

    fun toDomain(): org.rtsda.android.domain.model.Message {
        return org.rtsda.android.domain.model.Message(
            id = id,
            title = title,
            speaker = speaker,
            videoUrl = videoUrl,
            thumbnailUrl = thumbnailUrl,
            date = date,
            description = description,
            isLiveStream = isLiveStream
        )
    }
} 