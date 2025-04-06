package org.rtsda.android.data

import org.rtsda.android.data.model.Message
import org.rtsda.android.data.repository.MessageRepository
import org.rtsda.android.data.service.JellyfinService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagesRepositoryImpl @Inject constructor(
    private val jellyfinService: JellyfinService
) : MessagesRepository {
    
    override suspend fun getMessages(mediaType: MediaType): List<Message> {
        return when (mediaType) {
            MediaType.SERMONS -> jellyfinService.getSermons()
            MediaType.LIVESTREAMS -> jellyfinService.getLiveStreams()
        }
    }

    override suspend fun getVideoUrl(messageId: String): String {
        return jellyfinService.getVideoUrl(messageId)
    }

    override suspend fun getLiveStreamViewerCount(streamId: String): Int {
        return jellyfinService.getLiveStreamViewerCount(streamId)
    }
} 