package org.rtsda.android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.rtsda.android.databinding.ActivityBulletinDetailBinding
import org.rtsda.android.ui.bulletins.BulletinDetailViewModel
import org.rtsda.android.ui.bulletins.BulletinDetailState
import org.rtsda.android.ui.bulletins.adapter.BulletinSectionsAdapter
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class BulletinDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBulletinDetailBinding
    private val viewModel: BulletinDetailViewModel by viewModels()
    private val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
    private val sectionsAdapter = BulletinSectionsAdapter()

    companion object {
        private const val EXTRA_BULLETIN_ID = "bulletin_id"

        fun newIntent(context: Context, bulletinId: String): Intent {
            return Intent(context, BulletinDetailActivity::class.java).apply {
                putExtra(EXTRA_BULLETIN_ID, bulletinId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBulletinDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Bulletins"

        // Handle back button click
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Set up RecyclerView
        binding.sectionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@BulletinDetailActivity)
            adapter = sectionsAdapter
        }

        // Get bulletin data from intent
        val bulletinId = intent.getStringExtra(EXTRA_BULLETIN_ID)
        if (bulletinId == null) {
            finish()
            return
        }

        // Load bulletin details
        viewModel.loadBulletin(bulletinId)

        // Observe view model state
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                updateUI(state)
            }
        }
    }

    private fun updateUI(state: BulletinDetailState) {
        when (state) {
            is BulletinDetailState.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.errorText.visibility = View.GONE
            }
            is BulletinDetailState.Error -> {
                binding.progressBar.visibility = View.GONE
                binding.errorText.visibility = View.VISIBLE
                binding.errorText.text = state.message
            }
            is BulletinDetailState.Success -> {
                binding.progressBar.visibility = View.GONE
                binding.errorText.visibility = View.GONE
                
                binding.bulletinTitleText.text = state.bulletin.title
                binding.bulletinDateText.text = dateFormat.format(state.bulletin.date)
                
                // Show download button if PDF URL is available
                if (state.bulletin.pdfUrl != null) {
                    binding.downloadButton.visibility = View.VISIBLE
                    binding.downloadButton.setOnClickListener {
                        startActivity(PdfViewerActivity.newIntent(
                            this,
                            state.bulletin.pdfUrl,
                            state.bulletin.title
                        ))
                    }
                } else {
                    binding.downloadButton.visibility = View.GONE
                }
                
                // Update sections
                sectionsAdapter.submitList(state.bulletin.sections)
            }
        }
    }
} 