package com.rtsda.appr.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class PrayerRequest(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    @get:PropertyName("requestText")
    val requestText: String = "",
    val details: String = "",
    val isConfidential: Boolean = false,
    val requestType: RequestType = RequestType.OTHER,
    @get:PropertyName("timestamp")
    val timestamp: Timestamp = Timestamp.now(),
    val prayedFor: Boolean = false,
    val prayedForDate: Timestamp? = null
) {
    // Helper properties to convert between Timestamp and Long
    @get:PropertyName("timestampMillis")
    val timestampMillis: Long
        get() = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000

    @get:PropertyName("prayedForDateMillis")
    val prayedForDateMillis: Long?
        get() = prayedForDate?.let { it.seconds * 1000 + it.nanoseconds / 1000000 }
}

enum class RequestType {
    PERSONAL,
    FAMILY,
    FRIEND,
    CHURCH,
    OTHER;

    override fun toString(): String {
        return when (this) {
            PERSONAL -> "Personal"
            FAMILY -> "Family"
            FRIEND -> "Friend"
            CHURCH -> "Church"
            OTHER -> "Other"
        }
    }

    companion object {
        fun fromString(value: String): RequestType {
            return when (value.lowercase()) {
                "personal" -> PERSONAL
                "family" -> FAMILY
                "friend" -> FRIEND
                "church" -> CHURCH
                else -> OTHER
            }
        }
    }
}
