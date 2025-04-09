package org.rtsda.android.presentation.splash

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.rtsda.android.R
import org.rtsda.android.data.model.BibleVerse
import org.rtsda.android.data.model.VersesRecord
import org.rtsda.android.data.service.BibleService
import org.rtsda.android.databinding.ActivitySplashBinding
import org.rtsda.android.MainActivity
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var bibleService: BibleService

    private lateinit var binding: ActivitySplashBinding
    private var verseText: String = ""
    private var verseReference: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Start loading data immediately
        lifecycleScope.launch {
            try {
                val response = bibleService.getVerses()
                if (response.isSuccessful) {
                    val verses = response.body()?.items ?: emptyList()
                    if (verses.isNotEmpty()) {
                        val randomVerse = verses.random().verses.verses.random()
                        verseText = randomVerse.text
                        verseReference = randomVerse.reference
                    } else {
                        verseText = getString(R.string.default_bible_verse)
                        verseReference = getString(R.string.default_bible_reference)
                    }
                } else {
                    verseText = getString(R.string.default_bible_verse)
                    verseReference = getString(R.string.default_bible_reference)
                }
            } catch (e: Exception) {
                verseText = getString(R.string.default_bible_verse)
                verseReference = getString(R.string.default_bible_reference)
            }

            // Now that data is loaded, show the view
            showSplashScreen()
        }
    }

    private fun showSplashScreen() {
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set initial state
        binding.root.scaleX = 0.8f
        binding.root.scaleY = 0.8f
        binding.root.alpha = 0.5f

        // Set verse and reference together in a single operation
        binding.bibleVerseView.setVerse(verseText, verseReference)

        // Animate everything at once
        val scaleX = ObjectAnimator.ofFloat(binding.root, View.SCALE_X, 0.8f, 0.9f)
        val scaleY = ObjectAnimator.ofFloat(binding.root, View.SCALE_Y, 0.8f, 0.9f)
        val alpha = ObjectAnimator.ofFloat(binding.root, View.ALPHA, 0.5f, 1.0f)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleX, scaleY, alpha)
        animatorSet.duration = 800
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.start()

        // Wait for animation to complete plus a small buffer
        lifecycleScope.launch {
            delay(1000)
            startMainActivity()
        }
    }

    private fun startMainActivity() {
        val options = ActivityOptionsCompat.makeCustomAnimation(
            this,
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
        startActivity(Intent(this, MainActivity::class.java), options.toBundle())
        finish()
    }
} 