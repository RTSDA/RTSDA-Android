package org.rtsda.android.ui.contact

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
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

    companion object {
        fun newInstance() = ContactFragment()
    }

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

        // Enable back press handling
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            }
        )

        setupPhoneNumberFormatting()
        binding.submitButton.setOnClickListener {
            if (validateForm()) {
                submitForm()
            }
        }
    }

    private fun setupPhoneNumberFormatting() {
        binding.phoneEditText.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No implementation needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No implementation needed
            }

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
                isFormatting = true

                val digits = s.toString().replace("[^0-9]".toRegex(), "")
                val cursorPosition = binding.phoneEditText.selectionStart

                // Only format if we have exactly 10 digits
                if (digits.length == 10) {
                    val formatted = "(${digits.substring(0, 3)}) ${digits.substring(3, 6)}-${digits.substring(6)}"
                    if (s.toString() != formatted) {
                        s?.replace(0, s.length, formatted)
                        // Set cursor to end after formatting
                        binding.phoneEditText.setSelection(formatted.length)
                    }
                } else {
                    // Just show the digits as they are typed
                    if (s.toString() != digits) {
                        s?.replace(0, s.length, digits)
                        // Keep cursor at the end of the digits
                        binding.phoneEditText.setSelection(digits.length)
                    }
                }

                isFormatting = false
            }
        })
    }

    private fun validateForm(): Boolean {
        var isValid = true
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

        // Validate first name
        if (binding.firstNameEditText.text.isNullOrBlank()) {
            binding.firstNameLayout.error = getString(R.string.contact_name_error)
            isValid = false
        } else {
            binding.firstNameLayout.error = null
        }

        // Validate last name
        if (binding.lastNameEditText.text.isNullOrBlank()) {
            binding.lastNameLayout.error = getString(R.string.contact_name_error)
            isValid = false
        } else {
            binding.lastNameLayout.error = null
        }

        // Validate email
        val email = binding.emailEditText.text.toString()
        if (email.isBlank()) {
            binding.emailLayout.error = getString(R.string.contact_email_error)
            isValid = false
        } else if (!email.matches(emailPattern.toRegex())) {
            binding.emailLayout.error = getString(R.string.contact_email_invalid)
            isValid = false
        } else {
            binding.emailLayout.error = null
        }

        // Validate message
        if (binding.messageEditText.text.isNullOrBlank()) {
            binding.messageLayout.error = getString(R.string.contact_message_error)
            isValid = false
        } else {
            binding.messageLayout.error = null
        }

        // Validate phone number
        val phoneNumber = binding.phoneEditText.text.toString().filter { it.isDigit() }
        if (phoneNumber.isNotBlank() && phoneNumber.length != 10) {
            binding.phoneLayout.error = getString(R.string.contact_phone_error)
            isValid = false
        } else {
            binding.phoneLayout.error = null
        }

        return isValid
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$"
        return Pattern.compile(emailRegex).matcher(email).matches()
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
        MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Rounded)
            .setTitle(R.string.success_title)
            .setMessage(R.string.success_message)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                dialog.dismiss()
                clearForm()
                requireActivity().finish()
            }
            .setBackgroundInsetStart(32)
            .setBackgroundInsetEnd(32)
            .show()
    }

    private fun showErrorDialog() {
        MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_Rounded)
            .setTitle(R.string.error_title)
            .setMessage(R.string.error_message)
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }
            .setBackgroundInsetStart(32)
            .setBackgroundInsetEnd(32)
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