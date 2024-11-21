package com.rtsda.appr.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rtsda.appr.viewmodels.PrayerRequestViewModel
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerRequestScreen(
    onDismiss: () -> Unit,
    viewModel: PrayerRequestViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var request by remember { mutableStateOf("") }
    var isPrivate by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf("Personal") }
    var showError by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }
    
    val focusManager = LocalFocusManager.current
    val state by viewModel.state.collectAsState()

    // Email validation function
    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
        return email.matches(emailRegex.toRegex())
    }

    // Phone validation function
    fun isValidPhone(phone: String): Boolean {
        if (phone.isEmpty()) return true // Optional field
        // Allow formats: (123) 456-7890, 123-456-7890, 1234567890
        val phoneRegex = """^(\+\d{1,2}\s?)?\(?\d{3}\)?[\s.-]?\d{3}[\s.-]?\d{4}$""".toRegex()
        return phone.matches(phoneRegex)
    }

    fun formatPhoneNumber(input: String): String {
        // Strip all non-digits
        val digitsOnly = input.replace(Regex("[^0-9]"), "")
        
        // Format as (XXX) XXX-XXXX if we have enough digits
        return when {
            digitsOnly.length > 10 -> digitsOnly.substring(0, 10)
            digitsOnly.length == 10 -> "(${digitsOnly.substring(0,3)}) ${digitsOnly.substring(3,6)}-${digitsOnly.substring(6)}"
            digitsOnly.length > 6 -> "(${digitsOnly.substring(0,3)}) ${digitsOnly.substring(3,6)}-${digitsOnly.substring(6)}"
            digitsOnly.length > 3 -> "(${digitsOnly.substring(0,3)}) ${digitsOnly.substring(3)}"
            digitsOnly.isNotEmpty() -> "(${digitsOnly}"
            else -> ""
        }
    }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            showSuccess = true
        }
    }

    LaunchedEffect(state.error) {
        if (state.error != null) {
            showError = true
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            // Top Bar
            TopAppBar(
                title = { Text("Prayer Request") },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            )

            // Form Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Name Field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Next) }
                    ),
                    singleLine = true
                )

                // Email Field
                OutlinedTextField(
                    value = email,
                    onValueChange = { 
                        email = it
                        emailError = if (it.isNotEmpty() && !isValidEmail(it)) {
                            "Please enter a valid email address"
                        } else null
                    },
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Next) }
                    ),
                    isError = emailError != null,
                    supportingText = { emailError?.let { Text(it) } },
                    singleLine = true
                )

                // Phone Field
                OutlinedTextField(
                    value = phone,
                    onValueChange = { input -> 
                        val formatted = formatPhoneNumber(input)
                        phone = formatted
                        phoneError = if (formatted.isNotEmpty() && !isValidPhone(formatted)) {
                            "Please enter a valid phone number"
                        } else null
                    },
                    label = { Text("Phone (Optional)") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Next) }
                    ),
                    isError = phoneError != null,
                    supportingText = { phoneError?.let { Text(it) } },
                    singleLine = true,
                    placeholder = { Text("(123) 456-7890") }
                )

                // Prayer Request Field
                OutlinedTextField(
                    value = request,
                    onValueChange = { request = it },
                    label = { Text("Prayer Request") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    )
                )

                // Private Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(
                        checked = isPrivate,
                        onCheckedChange = { isPrivate = it }
                    )
                    Text(
                        text = "Keep my request private",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                // Submit Button
                Button(
                    onClick = {
                        if (name.isBlank()) {
                            showError = true
                            return@Button
                        }
                        if (email.isNotEmpty() && !isValidEmail(email)) {
                            return@Button
                        }
                        viewModel.submitPrayerRequest(
                            name = name,
                            email = email,
                            phone = phone,
                            request = request,
                            isPrivate = isPrivate
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Submit")
                }
            }
        }
    }

    // Error Dialog
    if (showError) {
        AlertDialog(
            onDismissRequest = { showError = false },
            title = { Text("Error") },
            text = { Text(state.error ?: "Please fill in all required fields.") },
            confirmButton = {
                TextButton(onClick = { showError = false }) {
                    Text("OK")
                }
            }
        )
    }

    // Success Dialog
    if (showSuccess) {
        AlertDialog(
            onDismissRequest = {
                showSuccess = false
                viewModel.resetState()
                onDismiss()
            },
            title = { Text("Success") },
            text = { Text("Your prayer request has been submitted.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSuccess = false
                        viewModel.resetState()
                        onDismiss()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}
