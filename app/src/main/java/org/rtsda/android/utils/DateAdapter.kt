package org.rtsda.android.utils

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DateAdapter : TypeAdapter<Date>() {
    private val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    override fun write(out: JsonWriter, value: Date?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(format.format(value))
        }
    }

    override fun read(reader: JsonReader): Date? {
        val dateStr = reader.nextString()
        return try {
            format.parse(dateStr)
        } catch (e: Exception) {
            null
        }
    }
} 