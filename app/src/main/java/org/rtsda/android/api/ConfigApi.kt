package org.rtsda.android.api

import org.rtsda.android.models.Config
import retrofit2.Response
import retrofit2.http.GET

interface ConfigApi {
    @GET("api/collections/config/records/nn753t8o2t1iupd")
    suspend fun getConfig(): Response<Config>
} 