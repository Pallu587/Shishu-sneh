package com.example.shishu_sneh_healthcare.presentation.sleep

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shishu_sneh_healthcare.data.local.entity.SleepLogEntity
import java.text.SimpleDateFormat
import java.util.*

import com.example.shishu_sneh_healthcare.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepScreen(
    babyId: Long,
    onBackClick: () -> Unit,
    viewModel: SleepViewModel = hiltViewModel()
) {
    val sleepLogs by viewModel.sleepLogs.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = babyId) {
        viewModel.loadSleepLogs(babyId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sleep Tracker", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Log Sleep")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Sleep History",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (sleepLogs.isEmpty()) {
                EmptySleepState { showAddDialog = true }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(sleepLogs, key = { it.id }) { log ->
                        SleepLogItem(log = log)
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddSleepDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { start, end, quality, notes ->
                viewModel.addSleepLog(babyId, start, end, quality, notes)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun SleepLogItem(log: SleepLogEntity) {
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
    val startTime = timeFormat.format(Date(log.startTime))
    val date = dateFormat.format(Date(log.startTime))
    
    val duration = if (log.endTime != null) {
        val diff = log.endTime - log.startTime
        val hours = diff / (1000 * 60 * 60)
        val minutes = (diff / (1000 * 60)) % 60
        "${hours}h ${minutes}m"
    } else "In progress"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = date, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(text = "Started at $startTime", fontSize = 14.sp)
                if (log.quality != null) {
                    Text(text = "Quality: ${log.quality}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Text(
                text = duration,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun AddSleepDialog(onDismiss: () -> Unit, onConfirm: (Long, Long?, String?, String?) -> Unit) {
    var quality by remember { mutableStateOf("Restful") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Sleep Session") },
        text = {
            Column {
                Text("How was the baby's sleep?", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    listOf("Restful", "Fretful", "Woke Up").forEach { q ->
                        FilterChip(
                            selected = quality == q,
                            onClick = { quality = q },
                            label = { Text(q) },
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val now = System.currentTimeMillis()
                val threeHoursAgo = now - (3 * 60 * 60 * 1000)
                onConfirm(threeHoursAgo, now, quality, notes)
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EmptySleepState(onAddClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "🌙", fontSize = 80.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Track your baby\'s sleep patterns",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Text(
            text = "Monitoring rest helps you understand baby\'s routine better.",
            color = TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onAddClick,
            colors = ButtonDefaults.buttonColors(containerColor = ButtonCTA)
        ) {
            Text("Log First Nap")
        }
    }
}
