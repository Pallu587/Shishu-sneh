package com.example.shishu_sneh_healthcare.presentation.diaper

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
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shishu_sneh_healthcare.data.local.entity.DiaperLogEntity
import java.text.SimpleDateFormat
import java.util.*

import com.example.shishu_sneh_healthcare.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaperScreen(
    babyId: Long,
    onBackClick: () -> Unit,
    viewModel: DiaperViewModel = hiltViewModel()
) {
    val diaperLogs by viewModel.diaperLogs.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = babyId) {
        viewModel.loadDiaperLogs(babyId)
    }

    Scaffold(
        containerColor = WarmWhite,
        topBar = {
            TopAppBar(
                title = { Text("Diaper Tracker", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmWhite)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = ButtonCTA,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Log Diaper")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            Text(
                text = "Diaper History",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (diaperLogs.isEmpty()) {
                EmptyDiaperState { showAddDialog = true }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(diaperLogs, key = { it.id }) { log ->
                        DiaperLogItem(log = log)
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddDiaperDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { type, texture, color, notes ->
                viewModel.addDiaperLog(babyId, type, texture, color, notes)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun EmptyDiaperState(onAddClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "💧", fontSize = 80.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Track diaper changes",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Text(
            text = "Monitoring diaper frequency is a key indicator of hydration and health.",
            color = TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onAddClick,
            colors = ButtonDefaults.buttonColors(containerColor = ButtonCTA)
        ) {
            Text("Log Diaper Change")
        }
    }
}

@Composable
fun DiaperLogItem(log: DiaperLogEntity) {
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
    val time = timeFormat.format(Date(log.time))
    val date = dateFormat.format(Date(log.time))
    
    val icon = when (log.type) {
        "Wet" -> "💧"
        "Dirty" -> "💩"
        else -> "💦"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = log.type, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(text = "$date • $time", fontSize = 14.sp)
                if (!log.texture.isNullOrEmpty()) {
                    Text(text = "Texture: ${log.texture}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun AddDiaperDialog(onDismiss: () -> Unit, onConfirm: (String, String?, String?, String?) -> Unit) {
    var type by remember { mutableStateOf("Wet") }
    var texture by remember { mutableStateOf("Normal") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Diaper Change") },
        text = {
            Column {
                Text("Type", style = MaterialTheme.typography.labelMedium)
                Row {
                    listOf("Wet", "Dirty", "Both").forEach { t ->
                        FilterChip(
                            selected = type == t,
                            onClick = { type = t },
                            label = { Text(t) },
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                }
                if (type != "Wet") {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Texture", style = MaterialTheme.typography.labelMedium)
                    Row {
                        listOf("Normal", "Hard", "Loose").forEach { tex ->
                            FilterChip(
                                selected = texture == tex,
                                onClick = { texture = tex },
                                label = { Text(tex) },
                                modifier = Modifier.padding(end = 4.dp)
                            )
                        }
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
                onConfirm(type, if (type != "Wet") texture else null, null, notes)
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
