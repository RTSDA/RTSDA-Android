package org.rtsda.android.data.api

import retrofit2.http.GET
import retrofit2.http.Path

interface PocketBaseApi {
    @GET("api/collections/jellyfin_credentials/records")
    suspend fun getJellyfinCredentials(): List<JellyfinCredentials>
}

data class JellyfinCredentials(
    val id: String,
    val url: String,
    val username: String,
    val password: String
) 