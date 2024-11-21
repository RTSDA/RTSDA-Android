package com.rtsda.appr.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import java.util.UUID

data class PrayerRequest(
    @get:PropertyName("id")
    val id: String = UUID.randomUUID().toString(),
    
    @get:PropertyName("name")
    val name: String = "",
    
    @get:PropertyName("email")
    val email: String = "",
    
    @get:PropertyName("phone")
    val phone: String = "",
    
    @get:PropertyName("request")
    val request: String = "",
    
    @get:PropertyName("timestamp")
    val timestamp: Timestamp = Timestamp.now(),
    
    @get:PropertyName("status")
    val status: RequestStatus = RequestStatus.NEW,
    
    @get:PropertyName("isPrivate")
    val isPrivate: Boolean = false
)

enum class RequestStatus {
    @PropertyName("new")
    NEW,
    @PropertyName("prayed")
    PRAYED,
    @PropertyName("completed")
    COMPLETED;

    override fun toString(): String {
        return when (this) {
            NEW -> "new"
            PRAYED -> "prayed"
            COMPLETED -> "completed"
        }
    }

    companion object {
        fun fromString(value: String): RequestStatus {
            return when (value.lowercase()) {
                "new" -> NEW
                "prayed" -> PRAYED
                "completed" -> COMPLETED
                else -> NEW
            }
        }
    }
}
