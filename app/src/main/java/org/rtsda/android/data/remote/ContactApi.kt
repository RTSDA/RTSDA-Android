package org.rtsda.android.data.remote

import org.rtsda.android.data.model.ContactForm
import retrofit2.http.Body
import retrofit2.http.POST

interface ContactApi {
    @POST("https://contact.rockvilletollandsda.church/api/contact")
    suspend fun submitContactForm(@Body form: ContactForm)
} 