package org.rtsda.android.data.repository

import org.rtsda.android.data.model.Event
import org.rtsda.android.data.remote.PocketBaseApiImpl
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EventRepositoryImpl @Inject constructor(
    private val api: PocketBaseApiImpl
) : EventRepository {

    override suspend fun getEvents(): List<Event> {
        val response = api.getEvents()
        return if (response.isSuccessful && response.body() != null) {
            val currentDate = Date()
            response.body()!!.items.filter { event ->
                event.endDate.after(currentDate)
            }
        } else {
            emptyList()
        }
    }

    override suspend fun getEvent(id: String): Event {
        val response = api.getEvents("id='$id'")
        return if (response.isSuccessful && response.body() != null && response.body()!!.items.isNotEmpty()) {
            response.body()!!.items[0]
        } else {
            throw Exception("Failed to get event")
        }
    }
} 