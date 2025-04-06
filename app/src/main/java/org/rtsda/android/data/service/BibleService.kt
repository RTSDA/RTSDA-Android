package org.rtsda.android.data.service

import org.rtsda.android.data.model.VersesRecord
import org.rtsda.android.data.remote.PocketBaseResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface BibleService {
    @Headers("Cache-Control: max-age=3600") // Cache for 1 hour
    @GET("api/collections/bible_verses/records")
    suspend fun getVerses(@Query("filter") filter: String? = null): Response<PocketBaseResponse<VersesRecord>>

    @Headers("Cache-Control: max-age=3600") // Cache for 1 hour
    @GET("api/collections/bible_verses/records/{id}")
    suspend fun getVerse(@Path("id") id: String): Response<VersesRecord>
} 