package org.rtsda.android.data.model

import com.google.gson.annotations.SerializedName

data class EventResponse(
    @SerializedName("items")
    val items: List<Event>,
    @SerializedName("page")
    val page: Int,
    @SerializedName("perPage")
    val perPage: Int,
    @SerializedName("totalItems")
    val totalItems: Int,
    @SerializedName("totalPages")
    val totalPages: Int
) 