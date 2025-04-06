package org.rtsda.android.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.rtsda.android.R
import org.rtsda.android.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val PHONE = "8608750450"
        private const val FACEBOOK_URL = "https://www.facebook.com/rockvilletollandsdachurch/"
        private const val MAPS_URL = "https://maps.google.com/?q=9+Hartford+Turnpike,+Tolland,+CT+06084"
        private const val GIVE_URL = "https://adventistgiving.org/donate/AN4MJG"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.contactButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_contactFragment)
        }

        binding.directionsButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(MAPS_URL)
            }
            startActivity(intent)
        }

        binding.callButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$PHONE")
            }
            startActivity(intent)
        }

        binding.giveButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(GIVE_URL)
            }
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 