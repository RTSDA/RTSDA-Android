package org.rtsda.android.data.service

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import org.json.JSONArray
import org.rtsda.android.data.model.Message
import org.rtsda.android.data.repository.PocketBaseRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JellyfinService @Inject constructor(
    private val pocketBaseRepository: PocketBaseRepository,
    private val okHttpClient: OkHttpClient
) {
    private var jellyfinApiKey: String? = null
    private var jellyfinServerUrl: String? = null
    private var sermonsLibraryId: String? = null
    private var livestreamsLibraryId: String? = null

    suspend fun initialize() {
        withContext(Dispatchers.IO) {
            // Get Jellyfin credentials from PocketBase
            val credentials = pocketBaseRepository.getJellyfinCredentials()
            jellyfinApiKey = credentials.apiKey
            jellyfinServerUrl = credentials.serverUrl
            Log.d("JellyfinService", "Initialized with server URL: $jellyfinServerUrl")
        }
    }

    private fun buildRequest(endpoint: String): Request {
        val apiKey = requireNotNull(jellyfinApiKey) { "Jellyfin API key not initialized" }
        val serverUrl = requireNotNull(jellyfinServerUrl) { "Jellyfin server URL not initialized" }

        val url = "$serverUrl/$endpoint"
        Log.d("JellyfinService", "Building request to: $url")
        
        return Request.Builder()
            .url(url)
            .addHeader("X-MediaBrowser-Token", apiKey)
            .addHeader("X-Emby-Authorization", "MediaBrowser Client=\"RTSDA Android\", Device=\"Android\", DeviceId=\"rtsda-android\", Version=\"1.0.0\", Token=\"$apiKey\"")
            .build()
    }

    private suspend fun getLibraryId(folderName: String): String = withContext(Dispatchers.IO) {
        if (jellyfinApiKey == null) {
            initialize()
        }

        try {
            // Use the correct endpoint for getting library folders
            val request = buildRequest("Library/VirtualFolders")
            val response = okHttpClient.newCall(request).execute()
            
            if (!response.isSuccessful) {
                Log.e("JellyfinService", "Failed to get library folders: ${response.code}")
                throw IllegalStateException("Failed to get library folders: ${response.code}")
            }
            
            val responseBody = response.body?.string() ?: throw IllegalStateException("Empty response")
            Log.d("JellyfinService", "Library folders response: $responseBody")
            
            val items = JSONArray(responseBody)
            Log.d("JellyfinService", "Found ${items.length()} library folders")

            for (i in 0 until items.length()) {
                val item = items.getJSONObject(i)
                val name = item.getString("Name")
                Log.d("JellyfinService", "Checking library folder: $name")
                
                if (name == folderName) {
                    val libraryId = item.getString("ItemId")
                    Log.d("JellyfinService", "Found library ID for $folderName: $libraryId")
                    return@withContext libraryId
                }
            }

            Log.e("JellyfinService", "Library folder not found: $folderName")
            throw IllegalStateException("Library folder not found: $folderName")
        } catch (e: Exception) {
            Log.e("JellyfinService", "Error getting library ID for $folderName", e)
            throw e
        }
    }

    private fun addSuffixToDate(dateString: String): String {
        if (dateString.isEmpty()) return ""
        
        // Check if the date already has a suffix
        if (dateString.contains(Regex("(st|nd|rd|th)"))) {
            return dateString
        }
        
        // Split the date into parts
        val parts = dateString.split(" ")
        if (parts.size != 3) return dateString
        
        val month = parts[0]
        val day = parts[1].toIntOrNull() ?: return dateString
        val year = parts[2]
        
        // Add the appropriate suffix to the day
        val suffix = when (day) {
            1, 21, 31 -> "st"
            2, 22 -> "nd"
            3, 23 -> "rd"
            else -> "th"
        }
        
        // Combine the parts with the suffix
        return "$month ${day}$suffix $year"
    }

    private fun extractTitleAndSpeaker(title: String): Pair<String, String> {
        // Remove file extension if present
        var cleanTitle = title.replace(Regex("\\.(mp4|mov)$"), "")
        
        // Try to split into title and speaker
        val parts = cleanTitle.split(" - ")
        if (parts.size > 1) {
            val titlePart = parts[0].trim()
            var speakerPart = parts[1].trim()
            
            // Extract date from speaker part (after |)
            val dateMatch = Regex("\\|\\s*(.*)$").find(speakerPart)
            val date = dateMatch?.groupValues?.get(1)?.trim() ?: ""
            
            // Remove date from speaker part
            speakerPart = speakerPart.replace(Regex("\\|.*$"), "").trim()
            
            return Pair(titlePart, speakerPart)
        }
        
        // If no date is found, try to get it from PremiereDate or DateCreated
        val dateMatch = Regex("\\|\\s*(.*)$").find(cleanTitle)
        val date = dateMatch?.groupValues?.get(1)?.trim() ?: ""
        
        return Pair(cleanTitle, "Unknown Speaker")
    }

    suspend fun getSermons(): List<Message> = withContext(Dispatchers.IO) {
        if (jellyfinApiKey == null) {
            initialize()
        }

        try {
            if (sermonsLibraryId == null) {
                sermonsLibraryId = getLibraryId("Movies")
            }

            val request = buildRequest("Items?ParentId=${sermonsLibraryId}&Fields=Path,PremiereDate,ProductionYear,Overview,DateCreated&Recursive=true&IncludeItemTypes=Movie,Video,Episode&SortBy=DateCreated&SortOrder=Descending")
            val response = okHttpClient.newCall(request).execute()
            
            if (!response.isSuccessful) {
                Log.e("JellyfinService", "Failed to get sermons: ${response.code}")
                throw IllegalStateException("Failed to get sermons: ${response.code}")
            }
            
            val jsonResponse = JSONObject(response.body?.string() ?: throw IllegalStateException("Empty response"))
            val items = jsonResponse.getJSONArray("Items")
            val messages = mutableListOf<Message>()

            for (i in 0 until items.length()) {
                val item = items.getJSONObject(i)
                val (title, speaker) = extractTitleAndSpeaker(item.getString("Name"))
                
                // First try to get the date from Jellyfin's metadata
                val formattedDate = when {
                    item.has("PremiereDate") && !item.isNull("PremiereDate") -> {
                        val premiereDate = item.getString("PremiereDate")
                        val date = java.time.LocalDate.parse(premiereDate.substring(0, 10))
                        val month = date.month.toString().lowercase().replaceFirstChar { it.uppercase() }
                        val day = date.dayOfMonth
                        val year = date.year
                        addSuffixToDate("$month $day $year")
                    }
                    item.has("ProductionYear") && !item.isNull("ProductionYear") -> {
                        val year = item.getInt("ProductionYear")
                        // Get the date from the title after the | character
                        val dateMatch = Regex("\\|\\s*(.*)$").find(item.getString("Name"))
                        val dateFromTitle = dateMatch?.groupValues?.get(1)?.trim() ?: ""
                        if (dateFromTitle.isNotEmpty()) {
                            // Extract month and day from title, use year from ProductionYear
                            val parts = dateFromTitle.split(" ")
                            if (parts.size >= 2) {
                                val month = parts[0]
                                val day = parts[1].toIntOrNull() ?: 1
                                addSuffixToDate("$month $day $year")
                            } else {
                                addSuffixToDate("$dateFromTitle $year")
                            }
                        } else {
                            // If no date in title, use DateCreated
                            val dateCreated = item.optString("DateCreated", "")
                            if (dateCreated.isNotEmpty()) {
                                val date = java.time.LocalDate.parse(dateCreated.substring(0, 10))
                                val month = date.month.toString().lowercase().replaceFirstChar { it.uppercase() }
                                val day = date.dayOfMonth
                                addSuffixToDate("$month $day $year")
                            } else {
                                ""
                            }
                        }
                    }
                    else -> {
                        // Get the date from the title after the | character
                        val dateMatch = Regex("\\|\\s*(.*)$").find(item.getString("Name"))
                        val dateFromTitle = dateMatch?.groupValues?.get(1)?.trim() ?: ""
                        if (dateFromTitle.isNotEmpty()) {
                            addSuffixToDate(dateFromTitle)
                        } else {
                            // If no date in title, use DateCreated
                            val dateCreated = item.optString("DateCreated", "")
                            if (dateCreated.isNotEmpty()) {
                                val date = java.time.LocalDate.parse(dateCreated.substring(0, 10))
                                val month = date.month.toString().lowercase().replaceFirstChar { it.uppercase() }
                                val day = date.dayOfMonth
                                val year = date.year
                                addSuffixToDate("$month $day $year")
                            } else {
                                ""
                            }
                        }
                    }
                }
                
                val message = Message(
                    id = item.getString("Id"),
                    title = title,
                    description = item.optString("Overview", ""),
                    speaker = speaker,
                    date = formattedDate,
                    thumbnailUrl = "$jellyfinServerUrl/Items/${item.getString("Id")}/Images/Primary?maxHeight=300&maxWidth=300&quality=90",
                    videoUrl = "",
                    isLiveStream = false
                )
                messages.add(message)
            }

            Log.d("JellyfinService", "Retrieved ${messages.size} sermons")
            return@withContext messages
        } catch (e: Exception) {
            Log.e("JellyfinService", "Error getting sermons", e)
            throw e
        }
    }

    suspend fun getLiveStreams(): List<Message> = withContext(Dispatchers.IO) {
        if (jellyfinApiKey == null) {
            initialize()
        }

        try {
            if (livestreamsLibraryId == null) {
                livestreamsLibraryId = getLibraryId("LiveStreams")
            }

            val request = buildRequest("Items?ParentId=${livestreamsLibraryId}&Fields=Path,PremiereDate,ProductionYear,Overview,DateCreated&Recursive=true&IncludeItemTypes=Movie,Video,Episode&SortBy=DateCreated&SortOrder=Descending")
            val response = okHttpClient.newCall(request).execute()
            
            if (!response.isSuccessful) {
                Log.e("JellyfinService", "Failed to get live streams: ${response.code}")
                throw IllegalStateException("Failed to get live streams: ${response.code}")
            }
            
            val jsonResponse = JSONObject(response.body?.string() ?: throw IllegalStateException("Empty response"))
            val items = jsonResponse.getJSONArray("Items")
            val messages = mutableListOf<Message>()

            for (i in 0 until items.length()) {
                val item = items.getJSONObject(i)
                val (title, speaker) = extractTitleAndSpeaker(item.getString("Name"))
                
                // Get the date from the title after the | character
                val dateMatch = Regex("\\|\\s*(.*)$").find(item.getString("Name"))
                val dateFromTitle = dateMatch?.groupValues?.get(1)?.trim() ?: ""
                val formattedDate = addSuffixToDate(dateFromTitle)
                
                val message = Message(
                    id = item.getString("Id"),
                    title = title,
                    description = item.optString("Overview", ""),
                    speaker = speaker,
                    date = formattedDate,
                    thumbnailUrl = "$jellyfinServerUrl/Items/${item.getString("Id")}/Images/Primary?maxHeight=300&maxWidth=300&quality=90",
                    videoUrl = "",
                    isLiveStream = true
                )
                messages.add(message)
            }

            Log.d("JellyfinService", "Retrieved ${messages.size} live streams")
            return@withContext messages
        } catch (e: Exception) {
            Log.e("JellyfinService", "Error getting live streams", e)
            throw e
        }
    }

    suspend fun getVideoUrl(messageId: String): String {
        return withContext(Dispatchers.IO) {
            val serverUrl = requireNotNull(jellyfinServerUrl) { "Jellyfin server URL not initialized" }
            val apiKey = requireNotNull(jellyfinApiKey) { "Jellyfin API key not initialized" }
            
            // First get the playback info to get the correct media source ID
            val request = buildRequest("Items/$messageId/PlaybackInfo")
            val response = okHttpClient.newCall(request).execute()
            
            if (!response.isSuccessful) {
                throw Exception("Failed to get playback info: ${response.code}")
            }
            
            val responseBody = response.body?.string()
            val json = JSONObject(responseBody)
            val mediaSources = json.getJSONArray("MediaSources")
            if (mediaSources.length() == 0) {
                throw Exception("No media sources found for item $messageId")
            }
            
            val mediaSource = mediaSources.getJSONObject(0)
            val mediaSourceId = mediaSource.getString("Id")
            
            // Construct the streaming URL with the correct format
            "$serverUrl/Videos/$messageId/stream?static=true&mediaSourceId=$mediaSourceId&api_key=$apiKey"
        }
    }

    suspend fun getLiveStreamViewerCount(streamId: String): Int = withContext(Dispatchers.IO) {
        if (jellyfinApiKey == null) {
            initialize()
        }

        try {
            val request = buildRequest("Sessions")
            val response = okHttpClient.newCall(request).execute()
            
            if (!response.isSuccessful) {
                Log.e("JellyfinService", "Failed to get sessions: ${response.code}")
                return@withContext 0
            }
            
            val responseBody = response.body?.string() ?: return@withContext 0
            val sessions = JSONArray(responseBody)
            var viewerCount = 0

            for (i in 0 until sessions.length()) {
                val session = sessions.getJSONObject(i)
                val nowPlayingItem = session.optJSONObject("NowPlayingItem")
                if (nowPlayingItem != null && nowPlayingItem.getString("Id") == streamId) {
                    viewerCount++
                }
            }

            Log.d("JellyfinService", "Viewer count for stream $streamId: $viewerCount")
            return@withContext viewerCount
        } catch (e: Exception) {
            Log.e("JellyfinService", "Error getting viewer count", e)
            return@withContext 0
        }
    }
} 