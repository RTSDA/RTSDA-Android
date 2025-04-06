package org.rtsda.android.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Bulletin(
    val id: String,
    val title: String,
    val description: String,
    val date: Date,
    val pdfUrl: String,
    val created: String,
    val updated: String
) : Parcelable 