package org.rtsda.android.data.service

import org.rtsda.android.data.model.EventResponse
import retrofit2.Response
import retrofit2.http.GET

interface EventService {
    @GET("api/collections/events/records")
    suspend fun getEvents(): Response<EventResponse>
} 