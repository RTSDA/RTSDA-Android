package org.rtsda.android.data.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.rtsda.android.data.model.JellyfinCredentials
import org.rtsda.android.data.service.PocketBaseService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PocketBaseRepository @Inject constructor(
    private val pocketBaseService: PocketBaseService
) {
    suspend fun getJellyfinCredentials(): JellyfinCredentials = withContext(Dispatchers.IO) {
        val response = pocketBaseService.getConfig()
        if (!response.isSuccessful) {
            throw IllegalStateException("Failed to get config from PocketBase: ${response.code()}")
        }
        
        // Debug logging
        val rawResponse = response.body()?.toString() ?: "null"
        Log.d("PocketBaseRepository", "Raw response: $rawResponse")
        
        val config = response.body() ?: throw IllegalStateException("No config found in PocketBase. Please add a config record with Jellyfin API key.")
            
        val apiKey = config.apiKey?.jellyfinApiKey 
            ?: throw IllegalStateException("Jellyfin API key not found in config. Please add the API key to the config record.")
        
        JellyfinCredentials(
            apiKey = apiKey,
            serverUrl = "https://jellyfin.rockvilletollandsda.church"
        )
    }
} 