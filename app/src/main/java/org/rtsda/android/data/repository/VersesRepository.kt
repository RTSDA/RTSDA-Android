package org.rtsda.android.data.repository

import org.rtsda.android.data.model.VersesRecord

interface VersesRepository {
    suspend fun getVerses(): List<VersesRecord>
    suspend fun getVerse(id: String): VersesRecord
} 