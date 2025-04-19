package org.rtsda.android.data.remote

import org.rtsda.android.data.model.Bulletin
import org.rtsda.android.data.model.Event
import org.rtsda.android.data.model.Message
import org.rtsda.android.data.model.VersesRecord
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class PocketBaseResponse<T>(
    val items: List<T>,
    val page: Int,
    val perPage: Int,
    val totalItems: Int,
    val totalPages: Int
)

interface PocketBaseApi {
    @GET("api/collections/events/records")
    suspend fun getEvents(@Query("filter") filter: String? = null): Response<PocketBaseResponse<Event>>

    @GET("api/collections/bulletins/records")
    suspend fun getBulletins(
        @Query("filter") filter: String? = null,
        @Query("sort") sort: String? = null
    ): Response<PocketBaseResponse<Bulletin>>

    @GET("api/collections/messages/records")
    suspend fun getMessages(@Query("filter") filter: String? = null): Response<PocketBaseResponse<Message>>

    @GET("api/collections/bulletins/records/{id}")
    suspend fun getBulletin(@Path("id") id: String): Response<Bulletin>

    @GET("api/collections/bulletins/records/{id}/download")
    suspend fun downloadBulletin(@Path("id") id: String): Response<Unit>

    @GET("api/collections/verses/records")
    suspend fun getVerses(@Query("filter") filter: String? = null): Response<PocketBaseResponse<VersesRecord>>

    @GET("api/collections/verses/records/{id}")
    suspend fun getVerse(@Path("id") id: String): Response<VersesRecord>
} 