package org.rtsda.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.github.barteksc.pdfviewer.PDFView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedInputStream
import javax.inject.Inject

@AndroidEntryPoint
class PdfViewerActivity : AppCompatActivity() {
    
    @Inject
    lateinit var okHttpClient: OkHttpClient
    
    private lateinit var pdfView: PDFView
    private lateinit var backFab: FloatingActionButton

    companion object {
        private const val EXTRA_PDF_URL = "pdf_url"
        private const val EXTRA_TITLE = "title"

        fun newIntent(context: Context, pdfUrl: String, title: String): Intent {
            return Intent(context, PdfViewerActivity::class.java).apply {
                putExtra(EXTRA_PDF_URL, pdfUrl)
                putExtra(EXTRA_TITLE, title)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Create layout programmatically
        val coordinatorLayout = CoordinatorLayout(this)
        pdfView = PDFView(this, null)
        backFab = FloatingActionButton(this).apply {
            setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        }
        
        // Add views to coordinator layout
        coordinatorLayout.addView(pdfView, CoordinatorLayout.LayoutParams(
            CoordinatorLayout.LayoutParams.MATCH_PARENT,
            CoordinatorLayout.LayoutParams.MATCH_PARENT
        ))
        
        val fabParams = CoordinatorLayout.LayoutParams(
            CoordinatorLayout.LayoutParams.WRAP_CONTENT,
            CoordinatorLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = android.view.Gravity.BOTTOM or android.view.Gravity.END
            marginEnd = resources.getDimensionPixelSize(android.R.dimen.app_icon_size)
            bottomMargin = resources.getDimensionPixelSize(android.R.dimen.app_icon_size)
        }
        coordinatorLayout.addView(backFab, fabParams)
        
        setContentView(coordinatorLayout)
        
        // Set up toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = intent.getStringExtra(EXTRA_TITLE) ?: "PDF Viewer"
        
        val pdfUrl = intent.getStringExtra(EXTRA_PDF_URL)
        if (pdfUrl == null) {
            finish()
            return
        }
        
        loadPdf(pdfUrl)
    }
    
    private fun loadPdf(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = Request.Builder().url(url).build()
                val response = okHttpClient.newCall(request).execute()
                val input = BufferedInputStream(response.body?.byteStream())
                
                withContext(Dispatchers.Main) {
                    pdfView.fromStream(input)
                        .enableSwipe(true)
                        .swipeHorizontal(false)
                        .enableDoubletap(true)
                        .defaultPage(0)
                        .load()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    // Handle error
                    finish()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
} 