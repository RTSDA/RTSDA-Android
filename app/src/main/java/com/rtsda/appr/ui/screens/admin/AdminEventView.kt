package com.rtsda.appr.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rtsda.appr.R
import com.rtsda.appr.data.model.CalendarEvent
import com.rtsda.appr.ui.components.LoadingSpinner
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEventView(
    modifier: Modifier = Modifier,
    viewModel: AdminEventViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    var showAddEventDialog by remember { mutableStateOf(false) }
    
    Column(modifier = modifier.fillMaxSize()) {
        // Top App Bar
        TopAppBar(
            title = { Text("Event Management") },
            navigationIcon = {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )
        
        // Offline indicator
        if (!isOnline) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ) {
                Text(
                    text = "Offline Mode",
                    modifier = Modifier.padding(8.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
        
        Box(modifier = Modifier.weight(1f)) {
            when (val state = viewModel.uiState) {
                is EventUiState.Loading -> {
                    LoadingSpinner(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                
                is EventUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.events.sortedBy { it.startDate }, key = { it.id }) { event ->
                            EventCard(
                                event = event,
                                onEdit = {
                                    viewModel.updateEventToEdit(it)
                                    showAddEventDialog = true
                                },
                                onDelete = { viewModel.deleteEvent(it) }
                            )
                        }
                    }
                    
                    // FAB for adding new event
                    FloatingActionButton(
                        onClick = { showAddEventDialog = true },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Event"
                        )
                    }
                }
                
                is EventUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.setupEventsListener() }
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
    
    if (showAddEventDialog) {
        EventFormDialog(
            event = viewModel.eventToEdit,
            onDismiss = {
                showAddEventDialog = false
                viewModel.updateEventToEdit(null)
            },
            onSave = { event ->
                if (event.id.isEmpty()) {
                    viewModel.addEvent(event)
                } else {
                    viewModel.updateEvent(event)
                }
                showAddEventDialog = false
                viewModel.updateEventToEdit(null)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventCard(
    event: CalendarEvent,
    onEdit: (CalendarEvent) -> Unit,
    onDelete: (CalendarEvent) -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault()) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onEdit(event) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleMedium
            )
            
            if (event.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            if (event.location.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = event.location,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    // Convert TimeInterval (seconds) to Date
                    val startDate = Date((event.startDate * 1000).toLong())
                    val endDate = Date((event.endDate * 1000).toLong())
                    Text(
                        text = "Starts: ${dateFormatter.format(startDate)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Ends: ${dateFormatter.format(endDate)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                TextButton(
                    onClick = { onDelete(event) }
                ) {
                    Text("Delete")
                }
            }
        }
    }
}
