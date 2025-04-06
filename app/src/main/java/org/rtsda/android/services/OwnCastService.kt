package org.rtsda.android.services

import org.rtsda.android.api.OwnCastApi
import org.rtsda.android.api.OwnCastStatus
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OwnCastService @Inject constructor(
    private val api: OwnCastApi
) {
    suspend fun getStreamStatus(): Result<OwnCastStatus> {
        return try {
            val response = api.getStatus()
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(HttpException(response))
            }
        } catch (e: IOException) {
            Result.Error(e)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getStreamUrl(): String {
        return try {
            val response = api.getConfig()
            if (response.isSuccessful && response.body() != null) {
                val config = response.body()!!
                // Get the first HLS stream URL
                config.streamOutputSettings.firstOrNull { it.url.endsWith(".m3u8") }?.url
                    ?: "https://stream.rockvilletollandsda.church/hls/stream.m3u8"
            } else {
                "https://stream.rockvilletollandsda.church/hls/stream.m3u8"
            }
        } catch (e: Exception) {
            "https://stream.rockvilletollandsda.church/hls/stream.m3u8"
        }
    }

    fun getWebSocketUrl(): String {
        return "wss://stream.rockvilletollandsda.church/ws"
    }
} 