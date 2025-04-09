package org.rtsda.android.domain.repository

import org.rtsda.android.domain.model.Message
import org.rtsda.android.domain.model.MediaType

interface MessagesRepository {
    suspend fun getMessages(mediaType: MediaType): List<Message>
    suspend fun getVideoUrl(messageId: String): String
    suspend fun getLiveStreamViewerCount(streamId: String): Int
} 