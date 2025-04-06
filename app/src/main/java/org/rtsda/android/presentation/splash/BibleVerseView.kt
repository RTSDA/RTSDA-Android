package org.rtsda.android.presentation.splash

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import org.rtsda.android.databinding.ViewBibleVerseBinding

class BibleVerseView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding: ViewBibleVerseBinding

    init {
        binding = ViewBibleVerseBinding.inflate(LayoutInflater.from(context), this)
        orientation = VERTICAL
    }

    fun setVerse(verse: String, reference: String) {
        binding.bibleVerse.text = verse
        binding.verseReference.text = reference
    }
} 