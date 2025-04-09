package org.rtsda.android.presentation.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.button.MaterialButton
import android.widget.LinearLayout
import android.view.Gravity
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.rtsda.android.R
import org.rtsda.android.domain.model.MediaType
import org.rtsda.android.domain.model.Message
import org.rtsda.android.databinding.DialogFilterBinding
import org.rtsda.android.databinding.FragmentMessagesBinding
import org.rtsda.android.viewmodels.MessagesViewModel
import org.rtsda.android.presentation.video.VideoPlayerActivity
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent

@AndroidEntryPoint
class MessagesFragment : Fragment() {

    private var _binding: FragmentMessagesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MessagesViewModel by viewModels()
    private lateinit var adapter: MessagesAdapter
    private var dialogBinding: DialogFilterBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupSwipeRefresh()
        setupFilterButton()
    }

    private fun setupRecyclerView() {
        adapter = MessagesAdapter(
            onMessageClick = { message ->
                viewModel.selectMessage(message)
            },
            onLiveStreamClick = {
                // Create a temporary message for the live stream
                val liveStreamMessage = Message(
                    id = "live",
                    title = viewModel.liveStreamStatus.value?.title ?: "Live Stream",
                    speaker = "Live",
                    videoUrl = viewModel.liveStreamStatus.value?.title ?: "",
                    thumbnailUrl = null,
                    date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                    description = viewModel.liveStreamStatus.value?.description ?: "",
                    isLiveStream = true
                )
                viewModel.selectMessage(liveStreamMessage)
            }
        )
        
        binding.messagesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = this@MessagesFragment.adapter
            // Prevent auto-scrolling when items are added
            itemAnimator = null
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.filteredMessages.collectLatest { messages ->
                android.util.Log.d("MessagesFragment", "Messages updated: ${messages.size}")
                // Store current scroll position
                val layoutManager = binding.messagesRecyclerView.layoutManager as LinearLayoutManager
                val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
                val firstVisibleView = layoutManager.findViewByPosition(firstVisiblePosition)
                val offset = firstVisibleView?.top ?: 0

                adapter.submitList(messages) {
                    // Restore scroll position after list update
                    if (firstVisiblePosition >= 0) {
                        layoutManager.scrollToPositionWithOffset(firstVisiblePosition, offset)
                    }
                }
                updateEmptyState(messages.isEmpty())
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.swipeRefresh.isRefreshing = isLoading
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collectLatest { error ->
                binding.errorText.visibility = if (error != null) View.VISIBLE else View.GONE
                binding.errorText.text = error
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.playbackState.collectLatest { state ->
                when (state) {
                    is MessagesViewModel.PlaybackState.Playing -> {
                        if (viewModel.shouldLaunchPlayer) {
                            val intent = Intent(requireContext(), VideoPlayerActivity::class.java).apply {
                                putExtra(VideoPlayerActivity.EXTRA_VIDEO_URL, state.videoUrl)
                                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                            }
                            startActivity(intent)
                            viewModel.onPlayerLaunched()
                        }
                    }
                    is MessagesViewModel.PlaybackState.Error -> {
                        binding.errorText.visibility = View.VISIBLE
                        binding.errorText.text = state.message
                    }
                    MessagesViewModel.PlaybackState.Idle -> {
                        // Do nothing
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.liveStreamStatus.collectLatest { status ->
                adapter.setLiveStreamStatus(status)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentMediaType.collectLatest { mediaType ->
                android.util.Log.d("MessagesFragment", "Media type updated: $mediaType")
                binding.toolbar.title = if (mediaType == MediaType.SERMONS) {
                    getString(R.string.sermons)
                } else {
                    getString(R.string.live_archives)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedYear.collectLatest { year ->
                android.util.Log.d("MessagesFragment", "Selected year updated: $year")
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedMonth.collectLatest { month ->
                android.util.Log.d("MessagesFragment", "Selected month updated: $month")
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.availableYears.collectLatest { years ->
                android.util.Log.d("MessagesFragment", "Available years updated: $years")
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.availableMonths.collectLatest { months ->
                android.util.Log.d("MessagesFragment", "Available months updated: $months")
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshContent()
        }
    }

    private fun setupFilterButton() {
        binding.toolbar.findViewById<ImageButton>(R.id.filterButton).setOnClickListener {
            showFilterDialog()
        }
    }

    private fun showFilterDialog() {
        dialogBinding = DialogFilterBinding.inflate(layoutInflater)
        
        // Setup media type toggle
        val currentMediaType = viewModel.currentMediaType.value
        android.util.Log.d("MessagesFragment", "Current media type: $currentMediaType")
        
        dialogBinding?.mediaTypeToggle?.check(
            when (currentMediaType) {
                MediaType.SERMONS -> R.id.sermonsButton
                MediaType.LIVESTREAMS -> R.id.liveArchivesButton
            }
        )
        
        dialogBinding?.mediaTypeToggle?.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val newType = when (checkedId) {
                    R.id.sermonsButton -> MediaType.SERMONS
                    R.id.liveArchivesButton -> MediaType.LIVESTREAMS
                    else -> return@addOnButtonCheckedListener
                }
                android.util.Log.d("MessagesFragment", "Media type changed to: $newType")
                viewModel.setMediaType(newType)
                
                // Wait for the available years to be updated
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.availableYears.collectLatest { years ->
                        android.util.Log.d("MessagesFragment", "Available years updated: $years")
                        updateYearChips()
                    }
                }
            }
        }

        // Setup year chips
        updateYearChips()

        // Setup month chips
        updateMonthChips()

        val dialogView = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            addView(dialogBinding?.root)
            addView(layoutInflater.inflate(R.layout.dialog_filter_buttons, this, false))
        }

        MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .create()
            .apply {
                window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                
                setOnShowListener { dialog ->
                    val positiveButton = dialogView.findViewById<MaterialButton>(R.id.positiveButton)
                    val negativeButton = dialogView.findViewById<MaterialButton>(R.id.negativeButton)
                    
                    positiveButton.setOnClickListener {
                        dialog.dismiss()
                    }
                    
                    negativeButton.setOnClickListener {
                        viewModel.resetFilters()
                        dialog.dismiss()
                    }
                }
            }
            .show()
    }

    private fun updateMonthChips() {
        val availableMonths = viewModel.availableMonths.value
        val selectedMonth = viewModel.selectedMonth.value
        android.util.Log.d("MessagesFragment", "Available months: $availableMonths, Selected month: $selectedMonth")
        
        dialogBinding?.monthChips?.apply {
            removeAllViews()
            availableMonths.forEach { month ->
                addView(Chip(context).apply {
                    text = month
                    isCheckable = true
                    isChecked = month == selectedMonth
                    setOnClickListener {
                        android.util.Log.d("MessagesFragment", "Month chip clicked: $month, isChecked: $isChecked")
                        
                        // Check if this month is currently selected
                        val isCurrentlySelected = month == viewModel.selectedMonth.value
                        
                        // If it's currently selected, deselect it
                        if (isCurrentlySelected) {
                            viewModel.setMonth(null)
                        } else {
                            // If it's not selected, select it
                            viewModel.setMonth(month)
                        }
                    }
                })
            }
        }
    }

    private fun updateYearChips() {
        val availableYears = viewModel.availableYears.value
        val selectedYear = viewModel.selectedYear.value
        android.util.Log.d("MessagesFragment", "Available years: $availableYears, Selected year: $selectedYear")
        
        dialogBinding?.yearChips?.apply {
            removeAllViews()
            availableYears.forEach { year ->
                addView(Chip(context).apply {
                    text = year
                    isCheckable = true
                    isChecked = year == selectedYear
                    setOnClickListener {
                        android.util.Log.d("MessagesFragment", "Year chip clicked: $year, isChecked: $isChecked")
                        
                        // Check if this year is currently selected
                        val isCurrentlySelected = year == viewModel.selectedYear.value
                        
                        // If it's currently selected, deselect it
                        if (isCurrentlySelected) {
                            viewModel.setYear(null)
                            dialogBinding?.monthChips?.removeAllViews()
                        } else {
                            // If it's not selected, select it
                            viewModel.setYear(year)
                            // Clear any existing month selection
                            viewModel.setMonth(null)
                            // Wait for the available months to be updated
                            viewLifecycleOwner.lifecycleScope.launch {
                                viewModel.availableMonths.collectLatest { months ->
                                    dialogBinding?.monthChips?.removeAllViews()
                                    if (months.isNotEmpty()) {
                                        updateMonthChips()
                                    }
                                }
                            }
                        }
                    }
                })
            }
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        binding.errorText.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.errorText.text = getString(R.string.no_data)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dialogBinding = null
        _binding = null
    }
} 