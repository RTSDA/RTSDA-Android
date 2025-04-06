package org.rtsda.android.data.repository

import org.rtsda.android.data.model.Message

interface MessageRepository {
    suspend fun getMessages(): List<Message>
} 