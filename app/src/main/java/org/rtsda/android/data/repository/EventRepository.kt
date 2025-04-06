package org.rtsda.android.data.repository

import org.rtsda.android.data.model.Event

interface EventRepository {
    suspend fun getEvents(): List<Event>
    suspend fun getEvent(id: String): Event
} 