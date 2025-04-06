package org.rtsda.android.data

import org.rtsda.android.data.model.Message

interface MessagesRepository {
    suspend fun getMessages(mediaType: MediaType): List<Message>
    suspend fun getVideoUrl(messageId: String): String
    suspend fun getLiveStreamViewerCount(streamId: String): Int
} 