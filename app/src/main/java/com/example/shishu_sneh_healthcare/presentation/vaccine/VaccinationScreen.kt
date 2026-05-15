package com.example.shishu_sneh_healthcare.presentation.vaccine

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shishu_sneh_healthcare.data.local.entity.VaccineEntity
import java.text.SimpleDateFormat
import java.util.*

import androidx.compose.foundation.shape.CircleShape
import com.example.shishu_sneh_healthcare.ui.theme.*

import androidx.compose.foundation.lazy.itemsIndexed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaccinationScreen(
    babyId: Long,
    onBackClick: () -> Unit,
    viewModel: VaccinationViewModel = hiltViewModel()
) {
    val vaccines by viewModel.vaccines.collectAsState()

    LaunchedEffect(key1 = babyId) {
        viewModel.loadVaccines(babyId)
    }

    Scaffold(
        containerColor = WarmWhite,
        topBar = {
            TopAppBar(
                title = { Text("Immunization Calendar", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = WarmWhite)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Track your baby\'s protection",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (vaccines.isEmpty()) {
                EmptyVaccineState()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    itemsIndexed(vaccines) { index, vaccine ->
                        VaccineTimelineItem(
                            vaccine = vaccine,
                            isLast = index == vaccines.size - 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VaccineTimelineItem(vaccine: VaccineEntity, isLast: Boolean) {
    val statusColor = when (vaccine.status) {
        "Done" -> MintGreen
        "Overdue" -> SoftRed
        "Due Today" -> PeachWarning
        else -> Color.LightGray.copy(alpha = 0.5f)
    }

    val icon = when (vaccine.status) {
        "Done" -> "✅"
        "Upcoming", "Due Today" -> "🟡"
        else -> "⚪"
    }

    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(48.dp)
        ) {
            Surface(
                color = statusColor,
                shape = CircleShape,
                modifier = Modifier.size(24.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = icon, fontSize = 12.sp)
                }
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(2.dp)
                        .background(Color.LightGray.copy(alpha = 0.3f))
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.padding(bottom = 24.dp)) {
            VaccineCard(vaccine = vaccine)
        }
    }
}

@Composable
fun VaccineCard(vaccine: VaccineEntity) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val dueDate = dateFormat.format(Date(vaccine.scheduledDate))

    val statusColor = when (vaccine.status) {
        "Done" -> MintGreen
        "Overdue" -> SoftRed
        "Due Today" -> PeachWarning
        else -> SoftLavender
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AppDimensions.CardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = statusColor.copy(alpha = 0.3f),
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (vaccine.status == "Done") Icons.Default.CheckCircle else Icons.Default.CheckCircle, 
                        contentDescription = null,
                        tint = TextPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = vaccine.name, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = TextPrimary)
                Text(text = vaccine.disease, fontSize = 13.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Due: $dueDate", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                }
            }
            StatusChip(status = vaccine.status, color = statusColor)
        }
    }
}

@Composable
fun StatusChip(status: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.4f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = status,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun EmptyVaccineState() {
    Box(modifier = Modifier.fillMaxSize().padding(top = 100.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "💉", fontSize = 80.sp)
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "No vaccines due today", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 18.sp)
            Text(text = "All protections up to date", fontSize = 14.sp, color = TextSecondary)
        }
    }
}
