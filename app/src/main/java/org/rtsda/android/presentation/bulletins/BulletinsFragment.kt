package org.rtsda.android.presentation.bulletins

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.rtsda.android.R
import org.rtsda.android.databinding.FragmentBulletinsBinding
import org.rtsda.android.presentation.bulletins.adapter.BulletinsAdapter

@AndroidEntryPoint
class BulletinsFragment : Fragment() {

    private var _binding: FragmentBulletinsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BulletinsViewModel by viewModels()
    private val adapter = BulletinsAdapter { bulletin ->
        findNavController().navigate(
            R.id.action_bulletins_to_bulletin_detail,
            Bundle().apply {
                putString("bulletinId", bulletin.id)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBulletinsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSwipeRefresh()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.bulletinsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@BulletinsFragment.adapter
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadBulletins()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    binding.swipeRefresh.isRefreshing = state.isLoading
                    binding.errorText.visibility = if (state.error != null) View.VISIBLE else View.GONE

                    state.error?.let { error ->
                        binding.errorText.text = error
                    }

                    adapter.submitList(state.bulletins)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 