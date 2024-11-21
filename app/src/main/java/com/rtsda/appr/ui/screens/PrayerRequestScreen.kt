package com.rtsda.appr.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Email
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
    var emailError by remember { mutableStateOf<String?>(null) }
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                )

                // Email Field
                OutlinedTextField(
                    value = email,
                    onValueChange = { 
                        email = it
                        emailError = if (it.isNotEmpty() && !isValidEmail(it)) {
                            "Please enter a valid email address"
                        } else {
                            null
                        }
                    },
                    label = { Text("Email") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    singleLine = true,
                    isError = emailError != null,
                    supportingText = {
                        emailError?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    )
                )

                // Request Field
                OutlinedTextField(
                    value = request,
                    onValueChange = { request = it },
                    label = { Text("Prayer Request") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5
                )

                // Request Type
                Text(
                    text = "Request Type",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                val requestTypes = listOf("Personal", "Family", "Health", "Other")
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2), 
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp), 
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    userScrollEnabled = false 
                ) {
                    items(requestTypes) { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(type) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Private Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Keep request private",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Switch(
                        checked = isPrivate,
                        onCheckedChange = { isPrivate = it }
                    )
                }

                // Submit Button
                Button(
                    onClick = {
                        if (email.isEmpty() || !isValidEmail(email)) {
                            emailError = "Please enter a valid email address"
                            return@Button
                        }
                        focusManager.clearFocus()
                        viewModel.submitPrayerRequest(
                            name = name,
                            email = email,
                            request = request,
                            isPrivate = isPrivate,
                            requestType = selectedType
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    enabled = name.isNotBlank() && email.isNotBlank() && request.isNotBlank() && emailError == null && !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Submit Request")
                    }
                }
            }
        }
    }

    // Error Dialog
    if (showError) {
        AlertDialog(
            onDismissRequest = { showError = false },
            title = { Text("Error") },
            text = { Text(state.error ?: "An unknown error occurred") },
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
            text = { Text("Your prayer request has been submitted. We will be praying for you.") },
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
