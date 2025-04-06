package org.rtsda.android.data.repository

import org.rtsda.android.data.model.Message
import org.rtsda.android.data.remote.PocketBaseApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepositoryImpl @Inject constructor(
    private val api: PocketBaseApi
) : MessageRepository {

    override suspend fun getMessages(): List<Message> {
        val response = api.getMessages()
        return if (response.isSuccessful && response.body() != null) {
            response.body()!!.items
        } else {
            emptyList()
        }
    }
} 