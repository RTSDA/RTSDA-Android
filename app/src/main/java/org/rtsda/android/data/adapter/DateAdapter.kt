package org.rtsda.android.data.adapter

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateAdapter : TypeAdapter<Date>() {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS'Z'", Locale.US)

    override fun write(out: JsonWriter, value: Date?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(dateFormat.format(value))
        }
    }

    override fun read(reader: JsonReader): Date? {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull()
            return null
        }
        val dateString = reader.nextString()
        return try {
            dateFormat.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }
} 