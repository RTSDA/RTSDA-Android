package org.rtsda.android.data.model

import com.google.gson.annotations.SerializedName

data class ContactForm(
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("message")
    val message: String
) 