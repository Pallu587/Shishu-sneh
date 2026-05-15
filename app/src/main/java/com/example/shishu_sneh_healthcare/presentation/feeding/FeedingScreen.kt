package com.example.shishu_sneh_healthcare.presentation.feeding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shishu_sneh_healthcare.data.local.entity.FeedingLogEntity
import com.example.shishu_sneh_healthcare.util.VoiceHelper
import java.text.SimpleDateFormat
import java.util.*

import com.example.shishu_sneh_healthcare.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedingScreen(
    babyId: Long,
    onBackClick: () -> Unit,
    viewModel: FeedingViewModel = hiltViewModel()
) {
    val feedingLogs by viewModel.feedingLogs.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val voiceHelper = remember { VoiceHelper(context) }

    LaunchedEffect(key1 = babyId) {
        viewModel.loadFeedingLogs(babyId)
    }

    DisposableEffect(Unit) {
        onDispose { voiceHelper.shutdown() }
    }

    Scaffold(
        containerColor = WarmWhite,
        topBar = {
            TopAppBar(
                title = { Text("Feeding & Nutrition", fontWeight = FontWeight.Bold) },
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
                containerColor = SoftPink,
                contentColor = TextPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Log")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                NutritionTipsSection(onSpeak = { voiceHelper.speak(it) })
            }

            item {
                Text(
                    text = "Recent Logs",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            if (feedingLogs.isEmpty()) {
                item {
                    EmptyFeedingState()
                }
            } else {
                items(feedingLogs) { log ->
                    FeedingLogItem(log = log)
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showAddDialog) {
        AddFeedingDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { type, amount, duration ->
                viewModel.addFeedingLog(
                    FeedingLogEntity(
                        babyId = babyId,
                        type = type,
                        startTime = System.currentTimeMillis(),
                        duration = duration,
                        amount = amount,
                        foodItem = null,
                        notes = null
                    )
                )
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AddFeedingDialog(onDismiss: () -> Unit, onConfirm: (String, Double, Int) -> Unit) {
    var type by remember { mutableStateOf("Breastfeeding") }
    var amount by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Feeding") },
        text = {
            Column {
                Text("Type", style = MaterialTheme.typography.labelMedium)
                val types = listOf("Breastfeeding", "Bottle", "Solids")
                types.forEach { t ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = type == t, onClick = { type = t })
                        Text(text = t)
                    }
                }
                
                if (type != "Breastfeeding") {
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Amount (ml)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Duration (minutes)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(type, amount.toDoubleOrNull() ?: 0.0, duration.toIntOrNull() ?: 0)
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
fun NutritionTipsSection(onSpeak: (String) -> Unit) {
    val tips = listOf(
        "Exclusive breastfeeding is recommended for the first 6 months.",
        "Mothers should stay hydrated and eat iron-rich foods.",
        "Vitamin D drops are essential for breastfed babies.",
        "Avoid adding salt or sugar to baby's food before 1 year.",
        "Try one new food at a time to check for allergies."
    )

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFFFFB300))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Daily Nutrition Tips",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(tips) { tip ->
                TipCard(tip = tip, onSpeak = { onSpeak(tip) })
            }
        }
    }
}

@Composable
fun TipCard(tip: String, onSpeak: () -> Unit) {
    Card(
        modifier = Modifier.width(250.dp).height(120.dp),
        shape = RoundedCornerShape(AppDimensions.CardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            Text(
                text = tip,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary,
                modifier = Modifier.align(Alignment.Center)
            )
            IconButton(
                onClick = onSpeak,
                modifier = Modifier.align(Alignment.BottomEnd).size(32.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Listen", tint = TextPrimary, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun FeedingLogItem(log: FeedingLogEntity) {
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val startTime = timeFormat.format(Date(log.startTime))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AppDimensions.CardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = log.type, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
                Text(text = "Started at: $startTime", fontSize = 14.sp, color = TextSecondary)
                if (log.amount > 0) {
                    Text(text = "Amount: ${log.amount} ml", fontSize = 14.sp, color = TextSecondary)
                }
            }
            if (log.duration > 0) {
                Text(
                    text = "${log.duration} min",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun EmptyFeedingState() {
    Box(modifier = Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "🍼", fontSize = 80.sp)
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Add today\'s feeding record", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 18.sp)
            Text(text = "Keep track of nutrition and routines", fontSize = 14.sp, color = TextSecondary)
        }
    }
}
