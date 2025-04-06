package org.rtsda.android.services

import dagger.hilt.android.scopes.ServiceScoped
import org.rtsda.android.data.model.Bulletin
import org.rtsda.android.data.model.Event
import org.rtsda.android.data.model.Message
import org.rtsda.android.data.remote.PocketBaseApi
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}

@ServiceScoped
class PocketBaseService @Inject constructor(
    private val api: PocketBaseApi
) {
    suspend fun getEvents(filter: String? = null): Result<List<Event>> {
        return try {
            val response = api.getEvents(filter)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!.items)
            } else {
                Result.Error(HttpException(response))
            }
        } catch (e: IOException) {
            Result.Error(e)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getBulletins(filter: String? = null): Result<List<Bulletin>> {
        return try {
            val response = api.getBulletins(filter)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!.items)
            } else {
                Result.Error(HttpException(response))
            }
        } catch (e: IOException) {
            Result.Error(e)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getMessages(filter: String? = null): Result<List<Message>> {
        return try {
            val response = api.getMessages(filter)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!.items)
            } else {
                Result.Error(HttpException(response))
            }
        } catch (e: IOException) {
            Result.Error(e)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun downloadBulletin(bulletin: Bulletin): Result<Unit> {
        return try {
            val response = api.downloadBulletin(bulletin.id)
            if (response.isSuccessful) {
                Result.Success(Unit)
            } else {
                Result.Error(HttpException(response))
            }
        } catch (e: IOException) {
            Result.Error(e)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
} 