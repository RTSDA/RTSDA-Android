package org.rtsda.android.data.repository

import org.rtsda.android.data.model.VersesRecord
import org.rtsda.android.data.remote.PocketBaseApiImpl
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VersesRepositoryImpl @Inject constructor(
    private val api: PocketBaseApiImpl
) : VersesRepository {

    override suspend fun getVerses(): List<VersesRecord> {
        val response = api.getVerses()
        return if (response.isSuccessful && response.body() != null) {
            response.body()!!.items
        } else {
            emptyList()
        }
    }

    override suspend fun getVerse(id: String): VersesRecord {
        val response = api.getVerse(id)
        return if (response.isSuccessful && response.body() != null) {
            response.body()!!
        } else {
            throw Exception("Failed to get verse")
        }
    }
} 