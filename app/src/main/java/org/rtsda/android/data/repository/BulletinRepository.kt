package org.rtsda.android.data.repository

import org.rtsda.android.data.model.Bulletin

interface BulletinRepository {
    suspend fun getBulletins(): List<Bulletin>
    suspend fun getBulletin(id: String): Bulletin
    fun getBaseUrl(): String
} 