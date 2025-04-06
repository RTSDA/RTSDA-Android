package org.rtsda.android.api

import org.rtsda.android.models.Bulletin
import org.rtsda.android.models.Event
import org.rtsda.android.models.Message
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PocketBaseApi {
    @GET("api/collections/events/records")
    suspend fun getEvents(
        @Query("sort") sort: String = "-startDate",
        @Query("filter") filter: String? = null
    ): Response<PocketBaseResponse<Event>>

    @GET("api/collections/bulletins/records")
    suspend fun getBulletins(
        @Query("sort") sort: String = "-date",
        @Query("filter") filter: String? = null
    ): Response<PocketBaseResponse<Bulletin>>

    @GET("api/collections/messages/records")
    suspend fun getMessages(
        @Query("sort") sort: String = "-date",
        @Query("filter") filter: String? = null
    ): Response<PocketBaseResponse<Message>>

    @GET("api/collections/bulletins/records/{id}/download")
    suspend fun downloadBulletin(
        @Path("id") id: String
    ): Response<Unit>
}

data class PocketBaseResponse<T>(
    val page: Int,
    val perPage: Int,
    val totalItems: Int,
    val totalPages: Int,
    val items: List<T>
) 