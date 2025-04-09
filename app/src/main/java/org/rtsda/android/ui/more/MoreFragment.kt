package org.rtsda.android.ui.more

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.rtsda.android.BuildConfig
import org.rtsda.android.MainActivity
import org.rtsda.android.R
import org.rtsda.android.databinding.FragmentMoreBinding

class MoreFragment : Fragment() {
    private var _binding: FragmentMoreBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Resources Section
        binding.bibleButton.setOnClickListener {
            openAppOrStore("market://details?id=com.sirma.mobile.bible.android")
        }

        binding.sabbathSchoolButton.setOnClickListener {
            openAppOrStore("market://details?id=com.cryart.sabbathschool")
        }

        binding.egwButton.setOnClickListener {
            openAppOrStore("market://details?id=com.whiteestate.egwwritings")
        }

        binding.hymnalButton.apply {
            text = "Adventist Hymnal (Coming Soon)"
            isEnabled = false
            alpha = 0.5f
        }

        // Connect Section
        binding.contactButton.setOnClickListener {
            // TODO: Create ContactActivity and update this
            // (requireActivity() as MainActivity).navigateToContact()
        }

        binding.callButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:8608750450")
            }
            startActivity(intent)
        }

        binding.facebookButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://www.facebook.com/rockvilletollandsdachurch/")
            }
            startActivity(intent)
        }

        binding.directionsButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://maps.google.com/?q=9+Hartford+Turnpike,+Tolland,+CT+06084")
            }
            startActivity(intent)
        }

        // About Section
        binding.beliefsButton.setOnClickListener {
            (requireActivity() as MainActivity).navigateToBeliefs()
        }

        // App Info Section
        binding.versionText.text = "Version ${BuildConfig.VERSION_NAME}"
    }

    private fun openAppOrStore(storeUri: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(storeUri)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 