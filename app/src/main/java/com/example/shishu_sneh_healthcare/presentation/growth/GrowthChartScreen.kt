package com.example.shishu_sneh_healthcare.presentation.growth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shishu_sneh_healthcare.data.local.entity.GrowthEntryEntity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

import com.example.shishu_sneh_healthcare.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrowthChartScreen(
    babyId: Long,
    onBackClick: () -> Unit,
    viewModel: GrowthViewModel = hiltViewModel()
) {
    val growthEntries by viewModel.growthEntries.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = babyId) {
        viewModel.loadGrowthEntries(babyId)
    }

    Scaffold(
        containerColor = WarmWhite,
        topBar = {
            TopAppBar(
                title = { Text("Growth Analytics", fontWeight = FontWeight.Bold) },
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
                Icon(Icons.Default.Add, contentDescription = "Add Entry")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            if (growthEntries.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(AppDimensions.CardCornerRadius),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Weight Progress (kg)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        GrowthLineChart(entries = growthEntries.mapIndexed { index, entry ->
                            Entry(index.toFloat(), entry.weight.toFloat())
                        })
                    }
                }
            } else {
                EmptyGrowthState()
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (growthEntries.isNotEmpty()) {
                Text(
                    text = "Key Statistics",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatCard("Current", "${growthEntries.last().weight} kg", Modifier.weight(1f))
                    val gain = if (growthEntries.size > 1) {
                        growthEntries.last().weight - growthEntries[growthEntries.size - 2].weight
                    } else 0.0
                    StatCard("Monthly Gain", "+${String.format("%.1f", gain)} kg", Modifier.weight(1f))
                }
            }
        }
    }

    if (showAddDialog) {
        AddGrowthDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { weight, height ->
                viewModel.addGrowthEntry(
                    GrowthEntryEntity(
                        babyId = babyId,
                        date = System.currentTimeMillis(),
                        weight = weight,
                        height = height,
                        headCirc = 0.0,
                        notes = "Monthly Checkup"
                    )
                )
                showAddDialog = false
            }
        )
    }
}

@Composable
fun EmptyGrowthState() {
    Box(modifier = Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "📈", fontSize = 80.sp)
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Start tracking your baby\'s growth", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 18.sp)
            Text(text = "Tap + to add your first record", fontSize = 14.sp, color = TextSecondary)
        }
    }
}

@Composable
fun AddGrowthDialog(onDismiss: () -> Unit, onConfirm: (Double, Double) -> Unit) {
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Growth") },
        text = {
            Column {
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Weight (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = height,
                    onValueChange = { height = it },
                    label = { Text("Height (cm)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val w = weight.toDoubleOrNull() ?: 0.0
                val h = height.toDoubleOrNull() ?: 0.0
                onConfirm(w, h)
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
fun StatCard(label: String, value: String, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(AppDimensions.CardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, fontSize = 12.sp, color = TextSecondary)
            Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
        }
    }
}

@Composable
fun GrowthLineChart(entries: List<Entry>) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                setTouchEnabled(true)
                setPinchZoom(true)
                setScaleEnabled(true)
                setDrawGridBackground(false)
                
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    granularity = 1f
                    labelCount = entries.size
                }
                
                axisLeft.apply {
                    setDrawGridLines(true)
                    gridColor = android.graphics.Color.LTGRAY
                    
                    // Add WHO Healthy Range Limit Lines (approximate for demo)
                    val upperLimit = com.github.mikephil.charting.components.LimitLine(9f, "Upper Range").apply {
                        lineWidth = 200f // Large width to create background area
                        lineColor = android.graphics.Color.parseColor("#E8F5E9") // Very soft green
                        enableDashedLine(10f, 10f, 0f)
                    }
                    addLimitLine(upperLimit)
                }
                
                axisRight.isEnabled = false
                legend.isEnabled = true
            }
        },
        update = { chart ->
            val dataSet = LineDataSet(entries, "Weight").apply {
                setColor(android.graphics.Color.parseColor("#F8BBD0")) // SoftPink
                setCircleColor(android.graphics.Color.parseColor("#F8BBD0"))
                lineWidth = 4f
                circleRadius = 6f
                setDrawCircleHole(true)
                circleHoleRadius = 3f
                valueTextSize = 10f
                setDrawFilled(true)
                fillColor = android.graphics.Color.parseColor("#F8BBD0")
                fillAlpha = 60
                mode = LineDataSet.Mode.CUBIC_BEZIER
            }
            chart.data = LineData(dataSet)
            chart.animateX(1000)
            chart.invalidate()
        }
    )
}
