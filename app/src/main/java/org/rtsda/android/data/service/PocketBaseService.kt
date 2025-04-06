package org.rtsda.android.data.service

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET

interface PocketBaseService {
    @GET("api/collections/config/records/nn753t8o2t1iupd")
    suspend fun getConfig(): Response<ConfigItem>
}

data class ConfigResponse(
    val items: List<ConfigItem>
)

data class ConfigItem(
    val id: String,
    @SerializedName("api_key")
    val apiKey: ApiKey?
)

data class ApiKey(
    @SerializedName("jellyfin_api_key")
    val jellyfinApiKey: String
) 