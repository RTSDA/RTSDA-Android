package org.rtsda.android.ui.bulletins

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.rtsda.android.R
import org.rtsda.android.databinding.FragmentBulletinDetailBinding
import org.rtsda.android.ui.bulletins.adapter.BulletinSectionAdapter
import org.rtsda.android.ui.bulletins.viewmodel.BulletinDetailViewModel
import org.rtsda.android.ui.bulletins.viewmodel.BulletinDetailViewModel.BulletinDetailEvent
import org.rtsda.android.ui.bulletins.viewmodel.BulletinDetailViewModel.BulletinDetailState
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class BulletinDetailFragment : Fragment() {

    private var _binding: FragmentBulletinDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BulletinDetailViewModel by viewModels()
    private val sectionsAdapter = BulletinSectionAdapter()
    private val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBulletinDetailBinding.inflate(inflater, container, false)
        
        // Get the bulletin ID from the arguments
        val bulletinId = arguments?.getString("bulletinId")
        if (bulletinId != null) {
            viewModel.setBulletinId(bulletinId)
        }
        
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        binding.sectionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = sectionsAdapter
        }
    }

    private fun setupClickListeners() {
        binding.downloadButton.setOnClickListener {
            viewModel.onDownloadPdfClicked()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collectLatest { state ->
                updateUI(state)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.events.collectLatest { event ->
                handleEvent(event)
                viewModel.clearEvent()
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
                
                binding.bulletinTitle.text = state.bulletin.title
                binding.bulletinDate.text = dateFormat.format(state.bulletin.date)
                
                if (state.bulletin.pdfUrl != null) {
                    binding.downloadButton.visibility = View.VISIBLE
                } else {
                    binding.downloadButton.visibility = View.GONE
                }
                
                sectionsAdapter.submitList(state.bulletin.sections)
            }
        }
    }

    private fun handleEvent(event: BulletinDetailEvent) {
        when (event) {
            is BulletinDetailEvent.None -> {
                // Do nothing
            }
            is BulletinDetailEvent.NavigateBack -> {
                findNavController().navigateUp()
            }
            is BulletinDetailEvent.ShowError -> {
                // Error is already shown in the UI through the state
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 