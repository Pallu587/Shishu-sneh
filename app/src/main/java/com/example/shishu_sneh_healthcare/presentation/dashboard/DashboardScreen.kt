package com.example.shishu_sneh_healthcare.presentation.dashboard

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.shishu_sneh_healthcare.R
import com.example.shishu_sneh_healthcare.data.local.entity.BabyEntity
import com.example.shishu_sneh_healthcare.domain.use_case.Insight
import com.example.shishu_sneh_healthcare.presentation.navigation.Screen
import com.example.shishu_sneh_healthcare.ui.theme.*
import com.example.shishu_sneh_healthcare.util.VoiceHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavHostController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val appLanguage by viewModel.appLanguage.collectAsState()
    var showBabySelector by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val voiceHelper = remember { VoiceHelper(context) }

    DisposableEffect(Unit) {
        onDispose { voiceHelper.shutdown() }
    }

    Scaffold(
        containerColor = WarmWhite,
        bottomBar = { 
            DashboardBottomNavigation(
                navController = navController, 
                selectedTab = currentTab, 
                onTabSelected = viewModel::setCurrentTab,
                babyId = state.selectedBaby?.id ?: -1L
            ) 
        }
    ) { padding ->
        val babyId = state.selectedBaby?.id ?: -1L
        
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                DashboardHeader(
                    selectedBaby = state.selectedBaby,
                    onProfileClick = { navController.navigate(Screen.Profile.route) },
                    onBabyNameClick = { showBabySelector = true }
                )
            }

            if (state.babies.isNotEmpty()) {
                item {
                    BabySummaryCard(state.selectedBaby)
                }

                item {
                    SummaryCardsSection(navController, babyId)
                }

                item {
                    QuickActionsGrid(navController, babyId)
                }

                item {
                    MedicineAlertSection(navController, babyId)
                }

                item {
                    UpcomingAlertsSection(onSpeak = { voiceHelper.speak(it, appLanguage) })
                }

                item {
                    AIInsightsCard(
                        insights = state.insights,
                        onSpeak = { voiceHelper.speak(it, appLanguage) }
                    )
                }
            } else {
                item {
                    EmptyDashboardState {
                        navController.navigate(Screen.ProfileSetup.route)
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showBabySelector) {
        BabySelectorDialog(
            babies = state.babies,
            selectedBabyId = state.selectedBaby?.id ?: -1L,
            onBabySelected = {
                viewModel.selectBaby(it)
                showBabySelector = false
            },
            onDismiss = { showBabySelector = false },
            onAddNew = {
                showBabySelector = false
                navController.navigate(Screen.ProfileSetup.route)
            }
        )
    }
}

@Composable
fun BabySummaryCard(baby: BabyEntity?) {
    if (baby == null || baby.dob == 0L) return
    
    val diffMillis = System.currentTimeMillis() - baby.dob
    val ageWeeks = (diffMillis / (1000 * 60 * 60 * 24 * 7)).toInt()
    val ageMonths = (diffMillis / (1000L * 60 * 60 * 24 * 30)).toInt()
    
    val ageText = when {
        ageMonths >= 12 -> "${ageMonths / 12} Years Old"
        ageMonths >= 1 -> "$ageMonths Months Old"
        else -> "$ageWeeks Weeks Old"
    }
    
    Card(
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp).fillMaxWidth(),
        shape = RoundedCornerShape(AppDimensions.CardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = CircleShape,
                color = SoftPink.copy(alpha = 0.3f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = if (baby.gender == "Boy") "👦" else "👧", fontSize = 32.sp)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = baby.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                Text(text = "$ageText • Healthy Growth", color = TextSecondary, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun EmptyDashboardState(onAddBabyClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "👶", fontSize = 64.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.no_babies_yet),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = stringResource(R.string.add_baby_desc),
            textAlign = TextAlign.Center,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onAddBabyClick,
            shape = RoundedCornerShape(AppDimensions.CardCornerRadius),
            colors = ButtonDefaults.buttonColors(containerColor = SoftPink, contentColor = TextPrimary)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.add_baby_profile))
        }
    }
}

@Composable
fun BabySelectorDialog(
    babies: List<BabyEntity>,
    selectedBabyId: Long,
    onBabySelected: (BabyEntity) -> Unit,
    onDismiss: () -> Unit,
    onAddNew: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Baby Profile", color = TextPrimary) },
        text = {
            LazyColumn {
                items(babies, key = { it.id }) { baby ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onBabySelected(baby) }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = baby.id == selectedBabyId, onClick = { onBabySelected(baby) })
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = baby.name, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                    }
                }
                item {
                    TextButton(onClick = onAddNew, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = SoftPink)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add New Baby Profile", color = SoftPink)
                    }
                }
            }
        },
        confirmButton = {}
    )
}

@Composable
fun DashboardHeader(
    selectedBaby: BabyEntity?,
    onProfileClick: () -> Unit,
    onBabyNameClick: () -> Unit
) {
    val babyName = selectedBaby?.name ?: "Parent"
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(PinkHeader, LavenderHeader)
                ),
                shape = RoundedCornerShape(bottomStart = AppDimensions.CardCornerRadius, bottomEnd = AppDimensions.CardCornerRadius)
            )
            .padding(24.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.clickable { onBabyNameClick() }) {
                    Text(
                        text = stringResource(R.string.hello),
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 16.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.guardian_tag, babyName),
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White)
                    }
                }
                Surface(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .clickable { onProfileClick() },
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "👩", fontSize = 32.sp)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Celebration, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (selectedBaby != null) stringResource(R.string.check_tips) else stringResource(R.string.add_baby_tips),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun MedicineAlertSection(navController: NavHostController, babyId: Long) {
    Column(modifier = Modifier.padding(24.dp)) {
        Text(
            text = stringResource(R.string.medicine_reminders),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth().clickable { navController.navigate(Screen.Medications.route + "/$babyId") },
            shape = RoundedCornerShape(AppDimensions.CardCornerRadius),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = SoftLavender.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Medication, contentDescription = null, tint = TextPrimary)
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Vitamin D3 Drops", fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(text = "Next dose at 8:00 PM", fontSize = 13.sp, color = TextSecondary)
                }
                IconButton(onClick = { /* Mark as taken */ }) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MintGreen)
                }
            }
        }
    }
}

@Composable
fun SummaryCardsSection(navController: NavHostController, babyId: Long) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        Text(
            text = stringResource(R.string.today_summary),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SummaryCard("Next Vaccine", "5 Days", Icons.Default.Vaccines, SoftPink) {
                    navController.navigate(Screen.Vaccination.route + "/$babyId")
                }
            }
            item {
                SummaryCard("Growth", "Healthy", Icons.Default.Timeline, SoftLavender) {
                    navController.navigate(Screen.GrowthChart.route + "/$babyId")
                }
            }
            item {
                SummaryCard("Feeding", "2h ago", Icons.Default.ChildCare, BabyBlue) {
                    navController.navigate(Screen.Feeding.route + "/$babyId")
                }
            }
        }
    }
}

@Composable
fun SummaryCard(title: String, value: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier.size(width = 150.dp, height = 120.dp),
        shape = RoundedCornerShape(AppDimensions.CardCornerRadius),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                color = color.copy(alpha = 0.2f),
                shape = CircleShape,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(20.dp))
                }
            }
            Column {
                Text(text = title, fontSize = 12.sp, color = TextSecondary)
                Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        }
    }
}

@Composable
fun QuickActionsGrid(navController: NavHostController, babyId: Long) {
    Column(modifier = Modifier.padding(24.dp)) {
        Text(
            text = stringResource(R.string.quick_actions),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            QuickActionButton(stringResource(R.string.log_growth), Icons.Default.Scale, Modifier.weight(1f), SoftPink) {
                navController.navigate(Screen.GrowthChart.route + "/$babyId")
            }
            QuickActionButton(stringResource(R.string.add_medicine), Icons.Default.AddModerator, Modifier.weight(1f), SoftLavender) {
                navController.navigate(Screen.Medications.route + "/$babyId")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            QuickActionButton(stringResource(R.string.log_sleep), Icons.Default.Bedtime, Modifier.weight(1f), BabyBlue) {
                navController.navigate(Screen.Sleep.route + "/$babyId")
            }
            QuickActionButton(stringResource(R.string.log_diaper), Icons.Default.BabyChangingStation, Modifier.weight(1f), PeachWarning) {
                navController.navigate(Screen.Diaper.route + "/$babyId")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            QuickActionButton("Ask Sneh AI", Icons.Default.AutoAwesome, Modifier.weight(1f), SoftLavender) {
                navController.navigate(Screen.Chat.route)
            }
            Box(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun QuickActionButton(label: String, icon: ImageVector, modifier: Modifier, containerColor: Color, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(70.dp),
        shape = RoundedCornerShape(AppDimensions.CardCornerRadius),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = containerColor.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = TextPrimary)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = label, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
        }
    }
}

@Composable
fun UpcomingAlertsSection(onSpeak: (String) -> Unit = {}) {
    Column(modifier = Modifier.padding(24.dp)) {
        Text(
            text = stringResource(R.string.upcoming_events),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(AppDimensions.CardCornerRadius),
            colors = CardDefaults.cardColors(containerColor = PeachWarning.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Surface(
                        color = Color.White.copy(alpha = 0.5f),
                        shape = CircleShape,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Vaccines, contentDescription = null, tint = TextPrimary)
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = "Polio Protection Due Tomorrow 💉", fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(text = "Visit nearest healthcare center", fontSize = 12.sp, color = TextPrimary.copy(alpha = 0.7f))
                    }
                }
                
                IconButton(onClick = { onSpeak("Polio Protection is due tomorrow. Please visit your nearest healthcare center.") }) {
                    Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Listen", tint = TextPrimary)
                }
            }
        }
    }
}

@Composable
fun AIInsightsCard(
    insights: List<Insight> = emptyList(),
    onSpeak: (String) -> Unit = {}
) {
    if (insights.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { insights.size })

    Card(
        modifier = Modifier.padding(24.dp).fillMaxWidth(),
        shape = RoundedCornerShape(AppDimensions.CardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = SoftLavender.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TextPrimary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = stringResource(R.string.ai_health_insights), fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                
                IconButton(onClick = { onSpeak(insights[pagerState.currentPage].description) }) {
                    Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Listen", tint = TextPrimary)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            HorizontalPager(state = pagerState) { index ->
                val insight = insights[index]
                Column {
                    Text(text = insight.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = insight.description,
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        color = TextSecondary
                    )
                }
            }
            
            if (insights.size > 1) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(insights.size) { iteration ->
                        val color = if (pagerState.currentPage == iteration) TextPrimary else TextPrimary.copy(alpha = 0.2f)
                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .size(6.dp)
                                .background(color, CircleShape)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardBottomNavigation(
    navController: NavHostController,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    babyId: Long
) {
    NavigationBar(
        tonalElevation = 8.dp,
        containerColor = Color.White
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Home", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
            selected = selectedTab == 0,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TextPrimary,
                selectedTextColor = TextPrimary,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = SoftPink.copy(alpha = 0.3f)
            ),
            onClick = { 
                onTabSelected(0)
                navController.navigate(Screen.Dashboard.route) {
                    popUpTo(Screen.Dashboard.route) { inclusive = true }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Timeline, contentDescription = null) },
            label = { Text("Growth", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
            selected = selectedTab == 1,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TextPrimary,
                selectedTextColor = TextPrimary,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = SoftLavender.copy(alpha = 0.3f)
            ),
            onClick = { 
                onTabSelected(1)
                if (babyId != -1L) {
                    navController.navigate(Screen.GrowthChart.route + "/$babyId")
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.History, contentDescription = null) },
            label = { Text("Records", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) },
            selected = selectedTab == 2,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TextPrimary,
                selectedTextColor = TextPrimary,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = BabyBlue.copy(alpha = 0.3f)
            ),
            onClick = { 
                onTabSelected(2)
                if (babyId != -1L) {
                    navController.navigate(Screen.HealthRecords.route + "/$babyId")
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
            label = { Text("Settings", fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal) },
            selected = selectedTab == 3,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TextPrimary,
                selectedTextColor = TextPrimary,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = MintGreen.copy(alpha = 0.3f)
            ),
            onClick = { 
                onTabSelected(3)
                navController.navigate(Screen.Settings.route)
            }
        )
    }
}
