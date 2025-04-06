package org.rtsda.android.services

import android.content.Context
import org.jellyfin.sdk.Jellyfin
import org.jellyfin.sdk.api.client.ApiClient
import org.jellyfin.sdk.api.client.exception.ApiClientException
import org.jellyfin.sdk.model.ClientInfo
import org.jellyfin.sdk.model.DeviceInfo
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.MediaSourceInfo
import org.jellyfin.sdk.model.api.MediaStream
import org.jellyfin.sdk.model.api.BaseItemKind
import org.jellyfin.sdk.model.api.MediaProtocol
import org.jellyfin.sdk.model.api.MediaStreamType
import org.jellyfin.sdk.createJellyfin
import org.jellyfin.sdk.api.client.extensions.itemsApi
import org.jellyfin.sdk.api.client.extensions.mediaInfoApi
import org.jellyfin.sdk.model.UUID
import org.rtsda.android.api.JellyfinItem
import org.rtsda.android.api.JellyfinMediaSource
import org.rtsda.android.api.JellyfinMediaStream
import org.rtsda.android.api.JellyfinPlaybackInfo
import org.rtsda.android.api.ConfigApi
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JellyfinService @Inject constructor(
    private val configApi: ConfigApi,
    private val context: Context
) {
    private var apiClient: ApiClient? = null

    suspend fun authenticate(): Result<Unit> {
        return try {
            // Get the API key from PocketBase
            val configResponse = configApi.getConfig()
            if (!configResponse.isSuccessful || configResponse.body() == null) {
                return Result.Error(Exception("Failed to get config from PocketBase"))
            }

            val config = configResponse.body()!!
            if (config.api_key.jellyfin_api_key.isBlank()) {
                return Result.Error(Exception("No Jellyfin API key found in config"))
            }

            // Create Jellyfin client
            val client = createJellyfin {
                clientInfo = ClientInfo("RTSDA", "1.0.0")
                deviceInfo = DeviceInfo("RTSDA-Android", "RTSDA-Android-Device")
                context = this@JellyfinService.context
            }

            // Create API client
            apiClient = client.createApi(
                baseUrl = "https://jellyfin.rockvilletollandsda.church",
                accessToken = config.api_key.jellyfin_api_key
            )

            Result.Success(Unit)
        } catch (e: IOException) {
            Result.Error(e)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getVideos(): Result<List<JellyfinItem>> {
        if (apiClient == null) {
            return Result.Error(Exception("Not authenticated"))
        }

        return try {
            val response = apiClient!!.itemsApi.getItems()

            Result.Success(response.content.items.map { item: BaseItemDto ->
                JellyfinItem(
                    Id = item.id.toString(),
                    Name = item.name ?: "",
                    Overview = item.overview,
                    Path = item.path,
                    Type = item.type?.name ?: "",
                    MediaSources = item.mediaSources?.map { source: MediaSourceInfo ->
                        JellyfinMediaSource(
                            Id = source.id.toString(),
                            Path = source.path ?: "",
                            Protocol = source.protocol?.name ?: "",
                            MediaStreams = source.mediaStreams?.map { stream: MediaStream ->
                                JellyfinMediaStream(
                                    Type = stream.type?.name ?: "",
                                    Index = stream.index ?: 0,
                                    Codec = stream.codec ?: "",
                                    Language = stream.language
                                )
                            } ?: emptyList()
                        )
                    } ?: emptyList()
                )
            })
        } catch (e: ApiClientException) {
            Result.Error(e)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getPlaybackInfo(itemId: String): Result<JellyfinPlaybackInfo> {
        if (apiClient == null) {
            return Result.Error(Exception("Not authenticated"))
        }

        return try {
            val response = apiClient!!.mediaInfoApi.getPlaybackInfo(
                itemId = UUID.fromString(itemId)
            )

            Result.Success(JellyfinPlaybackInfo(
                MediaSources = response.content.mediaSources.map { source: MediaSourceInfo ->
                    JellyfinMediaSource(
                        Id = source.id.toString(),
                        Path = source.path ?: "",
                        Protocol = source.protocol?.name ?: "",
                        MediaStreams = source.mediaStreams?.map { stream: MediaStream ->
                            JellyfinMediaStream(
                                Type = stream.type?.name ?: "",
                                Index = stream.index ?: 0,
                                Codec = stream.codec ?: "",
                                Language = stream.language
                            )
                        } ?: emptyList()
                    )
                }
            ))
        } catch (e: ApiClientException) {
            Result.Error(e)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    fun getStreamUrl(itemId: String): String? {
        if (apiClient == null) return null
        return "https://jellyfin.rockvilletollandsda.church/Items/$itemId/stream?static=true&api_key=${apiClient!!.accessToken}"
    }
} 