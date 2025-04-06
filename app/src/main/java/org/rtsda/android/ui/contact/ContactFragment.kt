package org.rtsda.android.ui.contact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.rtsda.android.R
import org.rtsda.android.data.model.ContactForm
import org.rtsda.android.data.repository.ContactRepository
import org.rtsda.android.databinding.FragmentContactBinding
import java.util.regex.Pattern
import javax.inject.Inject

@AndroidEntryPoint
class ContactFragment : Fragment() {
    @Inject
    lateinit var contactRepository: ContactRepository

    private var _binding: FragmentContactBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.submitButton.setOnClickListener {
            if (validateForm()) {
                submitForm()
            }
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        // Validate first name
        if (binding.firstNameEditText.text.isNullOrEmpty()) {
            binding.firstNameLayout.error = getString(R.string.error_required, getString(R.string.first_name))
            isValid = false
        } else {
            binding.firstNameLayout.error = null
        }

        // Validate last name
        if (binding.lastNameEditText.text.isNullOrEmpty()) {
            binding.lastNameLayout.error = getString(R.string.error_required, getString(R.string.last_name))
            isValid = false
        } else {
            binding.lastNameLayout.error = null
        }

        // Validate email
        val email = binding.emailEditText.text.toString()
        if (email.isEmpty()) {
            binding.emailLayout.error = getString(R.string.error_required, getString(R.string.email))
            isValid = false
        } else if (!isValidEmail(email)) {
            binding.emailLayout.error = getString(R.string.error_invalid_email)
            isValid = false
        } else {
            binding.emailLayout.error = null
        }

        // Validate phone (optional)
        val phone = binding.phoneEditText.text.toString()
        if (phone.isNotEmpty() && !isValidPhone(phone)) {
            binding.phoneLayout.error = getString(R.string.error_invalid_phone)
            isValid = false
        } else {
            binding.phoneLayout.error = null
        }

        // Validate message
        if (binding.messageEditText.text.isNullOrEmpty()) {
            binding.messageLayout.error = getString(R.string.error_required, getString(R.string.message))
            isValid = false
        } else {
            binding.messageLayout.error = null
        }

        return isValid
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$"
        return Pattern.compile(emailRegex).matcher(email).matches()
    }

    private fun isValidPhone(phone: String): Boolean {
        val phoneRegex = "^[0-9]{10}$"
        return Pattern.compile(phoneRegex).matcher(phone.replace("[^0-9]".toRegex(), "")).matches()
    }

    private fun submitForm() {
        val form = ContactForm(
            firstName = binding.firstNameEditText.text.toString(),
            lastName = binding.lastNameEditText.text.toString(),
            email = binding.emailEditText.text.toString(),
            phone = binding.phoneEditText.text.toString().takeIf { it.isNotEmpty() },
            message = binding.messageEditText.text.toString()
        )

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                contactRepository.submitContactForm(form)
                showSuccessDialog()
            } catch (e: Exception) {
                showErrorDialog()
            }
        }
    }

    private fun showSuccessDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.success_title)
            .setMessage(R.string.success_message)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                dialog.dismiss()
                clearForm()
            }
            .show()
    }

    private fun showErrorDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.error_title)
            .setMessage(R.string.error_message)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun clearForm() {
        binding.firstNameEditText.text?.clear()
        binding.lastNameEditText.text?.clear()
        binding.emailEditText.text?.clear()
        binding.phoneEditText.text?.clear()
        binding.messageEditText.text?.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 