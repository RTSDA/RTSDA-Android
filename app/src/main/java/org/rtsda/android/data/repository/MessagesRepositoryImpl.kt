package org.rtsda.android.data.repository

import org.rtsda.android.domain.model.Message
import org.rtsda.android.domain.model.MediaType
import org.rtsda.android.domain.repository.MessagesRepository
import org.rtsda.android.data.service.JellyfinService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagesRepositoryImpl @Inject constructor(
    private val jellyfinService: JellyfinService
) : MessagesRepository {
    
    override suspend fun getMessages(mediaType: MediaType): List<Message> {
        return when (mediaType) {
            MediaType.SERMONS -> jellyfinService.getSermons().map { it.toDomain() }
            MediaType.LIVESTREAMS -> jellyfinService.getLiveStreams().map { it.toDomain() }
        }
    }

    override suspend fun getVideoUrl(messageId: String): String {
        return jellyfinService.getVideoUrl(messageId)
    }

    override suspend fun getLiveStreamViewerCount(streamId: String): Int {
        return jellyfinService.getLiveStreamViewerCount(streamId)
    }
} 