package org.rtsda.android.data.remote

import org.rtsda.android.data.model.Bulletin
import org.rtsda.android.data.model.Event
import org.rtsda.android.data.model.Message
import org.rtsda.android.data.model.VersesRecord
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class PocketBaseApiImpl @Inject constructor(
    @Named("pocketbase") private val retrofit: retrofit2.Retrofit
) : PocketBaseApi {

    private val api: PocketBaseApi by lazy {
        retrofit.create(PocketBaseApi::class.java)
    }

    fun getBaseUrl(): String {
        return retrofit.baseUrl().toString()
    }

    override suspend fun getEvents(filter: String?): retrofit2.Response<PocketBaseResponse<Event>> {
        return api.getEvents(filter)
    }

    override suspend fun getBulletins(filter: String?, sort: String?): retrofit2.Response<PocketBaseResponse<Bulletin>> {
        return api.getBulletins(filter, sort)
    }

    override suspend fun getMessages(filter: String?): retrofit2.Response<PocketBaseResponse<Message>> {
        return api.getMessages(filter)
    }

    override suspend fun getBulletin(id: String): retrofit2.Response<Bulletin> {
        return api.getBulletin(id)
    }

    override suspend fun downloadBulletin(id: String): retrofit2.Response<Unit> {
        return api.downloadBulletin(id)
    }

    override suspend fun getVerses(filter: String?): retrofit2.Response<PocketBaseResponse<VersesRecord>> {
        return api.getVerses(filter)
    }

    override suspend fun getVerse(id: String): retrofit2.Response<VersesRecord> {
        return api.getVerse(id)
    }
} 