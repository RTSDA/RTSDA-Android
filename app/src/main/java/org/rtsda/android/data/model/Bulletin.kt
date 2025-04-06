package org.rtsda.android.data.model

import android.os.Parcelable
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import org.rtsda.android.data.adapter.DateAdapter
import java.util.Date

@Parcelize
data class Bulletin(
    @SerializedName("id")
    val id: String,
    @SerializedName("collectionId")
    val collectionId: String,
    @SerializedName("collectionName")
    val collectionName: String,
    @SerializedName("created")
    val created: String,
    @SerializedName("date")
    @JsonAdapter(DateAdapter::class)
    val date: Date,
    @SerializedName("divine_worship")
    val divineWorship: String,
    @SerializedName("is_active")
    val isActive: Boolean,
    @SerializedName("pdf")
    val pdf: String,
    @SerializedName("pdf_url")
    val pdfUrl: String,
    @SerializedName("sabbath_school")
    val sabbathSchool: String,
    @SerializedName("scripture_reading")
    val scriptureReading: String,
    @SerializedName("sunset")
    val sunset: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("updated")
    val updated: String,
    @SerializedName("url")
    val url: String
) : Parcelable

@Parcelize
data class BulletinSection(
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content: String
) : Parcelable 