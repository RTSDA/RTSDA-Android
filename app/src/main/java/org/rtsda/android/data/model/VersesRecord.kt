package org.rtsda.android.data.model

import com.google.gson.annotations.SerializedName

data class BibleVerse(
    val id: String,
    val reference: String,
    val text: String,
    @SerializedName("is_active")
    val isActive: Boolean
)

data class VersesData(
    val id: String,
    val verses: List<BibleVerse>
)

data class VersesRecord(
    @SerializedName("collectionId")
    val collectionId: String,
    @SerializedName("collectionName")
    val collectionName: String,
    val created: String,
    val id: String,
    val updated: String,
    val verses: VersesData
) 