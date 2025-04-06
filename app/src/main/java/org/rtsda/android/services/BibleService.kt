package org.rtsda.android.services

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

data class BibleVerse(
    val book: String,
    val chapter: Int,
    val verse: Int,
    val text: String
)

@Singleton
class BibleService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) {
    private var verses: List<BibleVerse>? = null

    fun loadVerses(): Result<List<BibleVerse>> {
        return try {
            if (verses != null) {
                return Result.Success(verses!!)
            }

            val jsonString = context.assets
                .open("bible_verses.json")
                .bufferedReader()
                .use { it.readText() }

            val type = object : TypeToken<List<BibleVerse>>() {}.type
            verses = gson.fromJson(jsonString, type)
            Result.Success(verses!!)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    fun searchVerses(query: String): Result<List<BibleVerse>> {
        return try {
            val loadResult = loadVerses()
            if (loadResult is Result.Error) {
                return loadResult
            }

            val searchResults = (loadResult as Result.Success).data
                .filter { verse ->
                    verse.text.contains(query, ignoreCase = true) ||
                    "${verse.book} ${verse.chapter}:${verse.verse}".contains(query, ignoreCase = true)
                }

            Result.Success(searchResults)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    fun getVerse(book: String, chapter: Int, verse: Int): Result<BibleVerse?> {
        return try {
            val loadResult = loadVerses()
            if (loadResult is Result.Error) {
                return loadResult
            }

            val foundVerse = (loadResult as Result.Success).data
                .find { it.book == book && it.chapter == chapter && it.verse == verse }

            Result.Success(foundVerse)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
} 