package com.rtsda.appr.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import java.util.Date

enum class RecurrenceType {
    NONE,
    WEEKLY,
    BIWEEKLY,
    MONTHLY,
    FIRST_TUESDAY;

    companion object {
        fun fromString(value: String?): RecurrenceType {
            return try {
                valueOf(value?.uppercase() ?: NONE.name)
            } catch (e: IllegalArgumentException) {
                NONE
            }
        }
    }

    fun toDisplayString(): String {
        return when (this) {
            NONE -> "One-time"
            WEEKLY -> "Weekly"
            BIWEEKLY -> "Bi-weekly"
            MONTHLY -> "Monthly"
            FIRST_TUESDAY -> "First Tuesday"
        }
    }
}

data class CalendarEvent(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val startDate: Double = Date().time / 1000.0, // TimeInterval (seconds since 1970)
    val endDate: Double = Date().time / 1000.0,   // TimeInterval (seconds since 1970)
    val recurrenceType: RecurrenceType = RecurrenceType.NONE,
    val parentEventId: String? = null
) {
    companion object {
        fun fromDocument(document: DocumentSnapshot): CalendarEvent? {
            return try {
                val data = document.data ?: return null
                CalendarEvent(
                    id = document.id,
                    title = data["title"] as? String ?: "",
                    description = data["description"] as? String ?: "",
                    location = data["location"] as? String ?: "",
                    startDate = (data["startDate"] as? Double) ?: Date().time / 1000.0,
                    endDate = (data["endDate"] as? Double) ?: Date().time / 1000.0,
                    recurrenceType = RecurrenceType.fromString(data["recurrenceType"] as? String),
                    parentEventId = data["parentEventId"] as? String
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "title" to title,
            "description" to description,
            "location" to location,
            "startDate" to startDate,
            "endDate" to endDate,
            "recurrenceType" to recurrenceType.name,
            "parentEventId" to (parentEventId ?: "")
        )
    }

    val startDateTime: Date
        get() = Date((startDate * 1000).toLong())

    val endDateTime: Date
        get() = Date((endDate * 1000).toLong())
}
