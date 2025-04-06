package org.rtsda.android.api

import retrofit2.Response
import retrofit2.http.GET

interface OwnCastApi {
    @GET("api/status")
    suspend fun getStatus(): Response<OwnCastStatus>

    @GET("api/config")
    suspend fun getConfig(): Response<OwnCastConfig>
}

data class OwnCastStatus(
    val serverTime: String,
    val lastConnectTime: String?,
    val lastDisconnectTime: String?,
    val versionNumber: String,
    val streamTitle: String,
    val online: Boolean
)

data class OwnCastConfig(
    val instanceDetails: OwnCastInstanceDetails,
    val ffmpegSettings: OwnCastFfmpegSettings,
    val streamOutputSettings: List<OwnCastStreamOutput>
)

data class OwnCastInstanceDetails(
    val name: String,
    val title: String,
    val summary: String,
    val logo: String
)

data class OwnCastFfmpegSettings(
    val videoSettings: OwnCastVideoSettings,
    val audioSettings: OwnCastAudioSettings
)

data class OwnCastVideoSettings(
    val framerate: Int,
    val videoBitrate: Int
)

data class OwnCastAudioSettings(
    val audioBitrate: Int
)

data class OwnCastStreamOutput(
    val url: String,
    val streamKey: String?,
    val serverUrl: String
) 