package org.rtsda.android.data.repository

import android.util.Log
import org.rtsda.android.data.remote.PocketBaseApiImpl
import org.rtsda.android.domain.model.Bulletin
import org.rtsda.android.domain.model.BulletinSection
import org.rtsda.android.domain.repository.BulletinRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BulletinRepositoryImpl @Inject constructor(
    private val api: PocketBaseApiImpl
) : BulletinRepository {

    override suspend fun getBulletins(): List<Bulletin> {
        val response = api.getBulletins(filter = "is_active=true", sort = "-date")
        return if (response.isSuccessful && response.body() != null) {
            response.body()!!.items.map { it.toDomainModel() }
        } else {
            Log.e("BulletinRepository", "Failed to get bulletins: ${response.errorBody()?.string()}")
            emptyList()
        }
    }

    override suspend fun getBulletinById(id: String): Bulletin {
        val response = api.getBulletin(id)
        if (response.isSuccessful && response.body() != null) {
            val bulletin = response.body()!!
            Log.d("BulletinRepository", "Bulletin data: $bulletin")
            Log.d("BulletinRepository", "Divine Worship: ${bulletin.divineWorship}")
            Log.d("BulletinRepository", "Sabbath School: ${bulletin.sabbathSchool}")
            Log.d("BulletinRepository", "Scripture Reading: ${bulletin.scriptureReading}")
            Log.d("BulletinRepository", "Sunset: ${bulletin.sunset}")
            return bulletin.toDomainModel()
        } else {
            Log.e("BulletinRepository", "Failed to get bulletin: ${response.errorBody()?.string()}")
            throw Exception("Failed to get bulletin")
        }
    }

    private fun org.rtsda.android.data.model.Bulletin.toDomainModel(): Bulletin {
        val baseUrl = api.getBaseUrl()
        val pdfUrl = if (pdf.isNotEmpty()) {
            "$baseUrl/api/files/${collectionId}/$id/$pdf"
        } else null

        return Bulletin(
            id = id,
            title = title,
            date = date,
            pdfUrl = pdfUrl,
            sections = listOf(
                BulletinSection("Sabbath School", sabbathSchool),
                BulletinSection("Divine Worship", divineWorship),
                BulletinSection("Scripture Reading", scriptureReading),
                BulletinSection("Sunset Times", sunset)
            )
        )
    }

    override fun getBaseUrl(): String {
        return api.getBaseUrl()
    }
} 