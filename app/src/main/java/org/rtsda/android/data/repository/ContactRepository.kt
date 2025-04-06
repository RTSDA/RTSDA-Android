package org.rtsda.android.data.repository

import org.rtsda.android.data.model.ContactForm
import org.rtsda.android.data.remote.ContactApi
import javax.inject.Inject

class ContactRepository @Inject constructor(
    private val api: ContactApi
) {
    suspend fun submitContactForm(form: ContactForm): Result<Unit> {
        return try {
            api.submitContactForm(form)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 