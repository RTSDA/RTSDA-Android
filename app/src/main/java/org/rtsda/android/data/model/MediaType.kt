package org.rtsda.android.data.model

enum class MediaType {
    SERMONS,
    LIVESTREAMS;

    fun toDomain(): org.rtsda.android.domain.model.MediaType {
        return when (this) {
            SERMONS -> org.rtsda.android.domain.model.MediaType.SERMONS
            LIVESTREAMS -> org.rtsda.android.domain.model.MediaType.LIVESTREAMS
        }
    }

    companion object {
        fun fromDomain(mediaType: org.rtsda.android.domain.model.MediaType): MediaType {
            return when (mediaType) {
                org.rtsda.android.domain.model.MediaType.SERMONS -> SERMONS
                org.rtsda.android.domain.model.MediaType.LIVESTREAMS -> LIVESTREAMS
            }
        }
    }
} 