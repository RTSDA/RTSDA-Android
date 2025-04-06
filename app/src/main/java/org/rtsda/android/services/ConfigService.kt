package org.rtsda.android.services

import android.content.SharedPreferences
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import org.rtsda.android.api.ConfigApi
import org.rtsda.android.models.Config
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigService @Inject constructor(
    private val api: ConfigApi,
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) {
    companion object {
        private const val KEY_CONFIG = "config"
    }

    private var cachedConfig: Config? = null

    suspend fun getConfig(forceRefresh: Boolean = false): Result<Config> {
        // Return cached config if available and not forcing refresh
        if (!forceRefresh && cachedConfig != null) {
            return Result.Success(cachedConfig!!)
        }

        // Try to get from local storage if not forcing refresh
        if (!forceRefresh) {
            val storedConfig = sharedPreferences.getString(KEY_CONFIG, null)
            if (storedConfig != null) {
                try {
                    cachedConfig = gson.fromJson(storedConfig, Config::class.java)
                    return Result.Success(cachedConfig!!)
                } catch (e: Exception) {
                    // Invalid stored config, will fetch from network
                }
            }
        }

        // Fetch from network
        return try {
            val response = api.getConfig()
            if (response.isSuccessful && response.body() != null) {
                val config = response.body()!!
                cachedConfig = config
                
                // Store in local storage
                sharedPreferences.edit()
                    .putString(KEY_CONFIG, gson.toJson(config))
                    .apply()
                
                Result.Success(config)
            } else {
                Result.Error(HttpException(response))
            }
        } catch (e: IOException) {
            Result.Error(e)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    fun clearCache() {
        cachedConfig = null
        sharedPreferences.edit().remove(KEY_CONFIG).apply()
    }
} 