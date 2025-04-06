package org.rtsda.android.domain.model

import java.util.Date

data class Bulletin(
    val id: String,
    val title: String,
    val date: Date,
    val pdfUrl: String?,
    val sections: List<BulletinSection>
)

data class BulletinSection(
    val title: String,
    val content: String
) 