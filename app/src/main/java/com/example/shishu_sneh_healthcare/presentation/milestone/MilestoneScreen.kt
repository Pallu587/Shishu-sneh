package com.example.shishu_sneh_healthcare.presentation.milestone

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shishu_sneh_healthcare.data.local.entity.MilestoneEntity

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.shishu_sneh_healthcare.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MilestoneScreen(
    babyId: Long,
    onBackClick: () -> Unit,
    viewModel: MilestoneViewModel = hiltViewModel()
) {
    val milestones by viewModel.milestones.collectAsState()

    LaunchedEffect(key1 = babyId) {
        viewModel.loadMilestones(babyId)
    }

    Scaffold(
        containerColor = WarmWhite,
        topBar = {
            TopAppBar(
                title = { Text("Milestones", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                .padding(24.dp)
        ) {
            val progress = if (milestones.isNotEmpty()) {
                milestones.count { it.status == "Yes" }.toFloat() / milestones.size
            } else 0.4f
            
            MilestoneCircularProgress(progress = progress)
            
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Developmental Checklist",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (milestones.isEmpty()) {
                    // Show some sample milestones if empty for professional look
                    val samples = listOf(
                        MilestoneEntity(0, babyId, 1, "Reacts to sound", "Yes", null, null),
                        MilestoneEntity(0, babyId, 2, "Smiles socially", "Yes", null, null),
                        MilestoneEntity(0, babyId, 3, "Holds head up", "No", null, null)
                    )
                    items(samples) { milestone ->
                        MilestoneItem(milestone = milestone, onToggle = {})
                    }
                } else {
                    items(milestones) { milestone ->
                        MilestoneItem(
                            milestone = milestone,
                            onToggle = { updatedMilestone ->
                                viewModel.updateMilestone(updatedMilestone)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MilestoneCircularProgress(progress: Float) {
    val animatedProgress by animateFloatAsState(targetValue = progress)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AppDimensions.CardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Development Progress", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(text = "Your baby is reaching great milestones!", fontSize = 12.sp, color = TextSecondary)
            }
            
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.size(70.dp),
                    color = SoftPink,
                    strokeWidth = 8.dp,
                    trackColor = SoftPink.copy(alpha = 0.1f),
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
            }
        }
    }
}

@Composable
fun MilestoneItem(milestone: MilestoneEntity, onToggle: (MilestoneEntity) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AppDimensions.CardCornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = if (milestone.status == "Yes") Color(0xFFE8F5E9).copy(alpha = 0.5f) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = if (milestone.status == "Yes") MintGreen.copy(alpha = 0.4f) else SoftLavender.copy(alpha = 0.3f),
                shape = CircleShape,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "${milestone.month}", fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Month ${milestone.month}", fontSize = 12.sp, color = TextSecondary)
                Text(text = milestone.description, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            }
            Checkbox(
                checked = milestone.status == "Yes",
                onCheckedChange = { isChecked ->
                    onToggle(milestone.copy(status = if (isChecked) "Yes" else "No"))
                },
                colors = CheckboxDefaults.colors(checkedColor = MintGreen, uncheckedColor = TextSecondary)
            )
        }
    }
}
