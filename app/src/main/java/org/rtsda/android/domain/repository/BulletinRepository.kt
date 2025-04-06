package org.rtsda.android.domain.repository

import org.rtsda.android.domain.model.Bulletin

interface BulletinRepository {
    suspend fun getBulletinById(id: String): Bulletin
    suspend fun getBulletins(): List<Bulletin>
    fun getBaseUrl(): String
} 