package org.rtsda.android.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Body

interface JellyfinApi {
    @POST("Users/AuthenticateByName")
    suspend fun authenticate(
        @Header("X-Emby-Authorization") authorization: String,
        @Body body: JellyfinAuthRequest
    ): Response<JellyfinAuthResponse>

    @GET("Library/MediaFolders")
    suspend fun getLibraries(
        @Header("X-Emby-Token") token: String
    ): Response<JellyfinLibraryResponse>

    @GET("Items")
    suspend fun getItems(
        @Header("X-Emby-Token") token: String,
        @Query("ParentId") parentId: String? = null,
        @Query("Fields") fields: String = "Path,PremiereDate,ProductionYear,Overview,DateCreated",
        @Query("Recursive") recursive: Boolean = true,
        @Query("IncludeItemTypes") includeItemTypes: String = "Movie,Video,Episode",
        @Query("SortBy") sortBy: String = "DateCreated",
        @Query("SortOrder") sortOrder: String = "Descending"
    ): Response<JellyfinItemsResponse>

    @GET("Items/{itemId}/PlaybackInfo")
    suspend fun getPlaybackInfo(
        @Path("itemId") itemId: String,
        @Header("X-Emby-Token") token: String
    ): Response<JellyfinPlaybackInfo>
}

data class JellyfinAuthRequest(
    val Username: String,
    val Pw: String
)

data class JellyfinAuthResponse(
    val User: JellyfinUser,
    val AccessToken: String,
    val ServerId: String
)

data class JellyfinUser(
    val Id: String,
    val Name: String,
    val ServerId: String
)

data class JellyfinLibraryResponse(
    val Items: List<JellyfinLibrary>
)

data class JellyfinLibrary(
    val Id: String,
    val Name: String,
    val Path: String
)

data class JellyfinItemsResponse(
    val Items: List<JellyfinItem>,
    val TotalRecordCount: Int
)

data class JellyfinItem(
    val Id: String,
    val Name: String,
    val Overview: String?,
    val Path: String?,
    val Type: String,
    val MediaSources: List<JellyfinMediaSource>?
)

data class JellyfinMediaSource(
    val Id: String,
    val Path: String,
    val Protocol: String,
    val MediaStreams: List<JellyfinMediaStream>
)

data class JellyfinMediaStream(
    val Type: String,
    val Index: Int,
    val Codec: String,
    val Language: String?
)

data class JellyfinPlaybackInfo(
    val MediaSources: List<JellyfinMediaSource>
) 