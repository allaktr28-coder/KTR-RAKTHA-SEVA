package com.example.ktrrakthaseva.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ktrrakthaseva.ui.components.*
import com.example.ktrrakthaseva.ui.viewmodel.AuthViewModel
import com.example.ktrrakthaseva.ui.viewmodel.HomeViewModel

private val DarkRed = Color(0xFF8B0000)
private val DeepBlack = Color(0xFF0F0F0F)
private val GlowRed = Color(0xFFFF3131)
private val DarkGray = Color(0xFF1E1E1E)

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToBadges: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToDigitalCard: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val user by viewModel.currentUser.collectAsState()

    MainBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TechTopBar(
                    title = "Commander Profile",
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Image with Glow
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Brush.radialGradient(listOf(GlowRed.copy(alpha = 0.3f), Color.Transparent)))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(DarkGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            user?.name?.take(1)?.uppercase() ?: "U",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    user?.name ?: "UNKNOWN OPERATIVE",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    user?.email ?: "ENCRYPTED EMAIL",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GlowRed,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AnimatedTechButton(
                        onClick = onNavigateToDigitalCard,
                        modifier = Modifier.weight(1f),
                        containerColor = DarkGray
                    ) {
                        Icon(Icons.Default.QrCode, contentDescription = null, tint = GlowRed)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ID CARD", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    AnimatedTechButton(
                        onClick = onNavigateToAnalytics,
                        modifier = Modifier.weight(1f),
                        containerColor = DarkGray
                    ) {
                        Icon(Icons.Default.BarChart, contentDescription = null, tint = GlowRed)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("STATS", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                
                if (user?.isAdmin == true) {
                    AnimatedTechButton(
                        onClick = onNavigateToAdmin,
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = GlowRed.copy(alpha = 0.15f)
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, null, tint = GlowRed)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ACCESS COMMAND CENTER", color = Color.White, fontWeight = FontWeight.Black)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Stats Row
                Row(modifier = Modifier.fillMaxWidth()) {
                    StatCard(
                        title = "Blood Type",
                        value = user?.bloodType?.name?.replace("_POSITIVE", "+")?.replace("_NEGATIVE", "-") ?: "--",
                        icon = Icons.Default.Bloodtype,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Donations",
                        value = "${user?.totalDonations ?: 0}",
                        icon = Icons.Default.VolunteerActivism,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Medical Info Section
                TechCard {
                    Text("BIOMETRIC DATA", color = GlowRed, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(12.dp))
                    ProfileInfoRow(Icons.Default.MonitorWeight, "Weight", "${user?.weightKg ?: 0} KG")
                    ProfileInfoRow(Icons.Default.Wc, "Gender", user?.gender ?: "Not Set")
                    ProfileInfoRow(Icons.Default.CalendarToday, "DOB", user?.dateOfBirth ?: "Not Set")
                    ProfileInfoRow(Icons.Default.LocalHospital, "Donor Type", user?.donorType ?: "Voluntary")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onNavigateToBadges,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkGray),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlowRed.copy(alpha = 0.3f))
                ) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = GlowRed)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("VIEW ACHIEVEMENT BADGES", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(32.dp))

                TextButton(
                    onClick = { 
                        viewModel.signOut()
                        onLogout()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("TERMINATE SESSION (LOGOUT)", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
fun ProfileInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(label, color = Color.Gray, modifier = Modifier.weight(1f))
        Text(value, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun AnalyticsScreen(onBack: () -> Unit) {
    MainBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TechTopBar(
                    title = "Impact Analytics",
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("NETWORK STATISTICS", color = GlowRed, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    StatCard("Lives Saved", "24", Icons.Default.Favorite, modifier = Modifier.weight(1f))
                    StatCard("Network Rank", "Top 5%", Icons.AutoMirrored.Filled.TrendingUp, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                TechCard {
                    Text("DONATION FREQUENCY", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    // Simple Chart Placeholder using Canvas
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val points = listOf(0.2f, 0.5f, 0.3f, 0.8f, 0.6f, 0.9f)
                            val width = size.width / (points.size - 1)
                            for (i in 0 until points.size - 1) {
                                drawLine(
                                    color = GlowRed,
                                    start = androidx.compose.ui.geometry.Offset(i * width, size.height * (1 - points[i])),
                                    end = androidx.compose.ui.geometry.Offset((i + 1) * width, size.height * (1 - points[i + 1])),
                                    strokeWidth = 4f,
                                    cap = StrokeCap.Round
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text("BLOOD SUPPLY METRICS", color = GlowRed, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                Spacer(modifier = Modifier.height(16.dp))
                
                BloodSupplyRow("O+", 85)
                BloodSupplyRow("A-", 32)
                BloodSupplyRow("B+", 64)
            }
        }
    }
}

@Composable
fun BloodSupplyRow(type: String, percentage: Int) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(type, color = Color.White, fontWeight = FontWeight.Bold)
            Text("$percentage%", color = Color.Gray, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { percentage / 100f },
            modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
            color = GlowRed,
            trackColor = DarkGray
        )
    }
}

@Composable
fun DigitalCardScreen(onBack: () -> Unit, viewModel: AuthViewModel = hiltViewModel()) {
    val user by viewModel.currentUser.collectAsState()

    MainBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TechTopBar(
                    title = "Digital Donor Card",
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                TechCard(
                    modifier = Modifier.padding(24.dp).widthIn(max = 400.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Bloodtype, contentDescription = null, tint = GlowRed, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("RAKTA SEVA NETWORK", color = Color.White, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                        Text("CERTIFIED DONOR", color = GlowRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        // QR Code Placeholder
                        Box(
                            modifier = Modifier
                                .size(180.dp)
                                .background(Color.White, RoundedCornerShape(12.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.QrCode2, contentDescription = null, tint = Color.Black, modifier = Modifier.fillMaxSize())
                        }
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Text(user?.name?.uppercase() ?: "UNKNOWN", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                        Text("UID: ${user?.uid?.take(12)?.uppercase() ?: "--------"}", color = Color.Gray, fontSize = 10.sp)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("BLOOD GROUP", color = Color.Gray, fontSize = 10.sp)
                                Text(user?.bloodType?.name?.replace("_POSITIVE", "+")?.replace("_NEGATIVE", "-") ?: "--", color = Color.White, fontWeight = FontWeight.Black)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("STATUS", color = Color.Gray, fontSize = 10.sp)
                                Text("ACTIVE", color = Color(0xFF4CAF50), fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LeaderboardScreen(onBack: () -> Unit, viewModel: HomeViewModel = hiltViewModel()) {
    val leaderboard by viewModel.leaderboard.collectAsState()

    MainBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TechTopBar(
                    title = "Global Rankings",
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).padding(horizontal = 16.dp)) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "TOP SAVIORS",
                    style = MaterialTheme.typography.labelLarge,
                    color = GlowRed,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                if (leaderboard.isEmpty()) {
                    EmptyState(message = "Recalculating rankings...", icon = Icons.Default.QueryStats)
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(leaderboard) { entry ->
                            LeaderboardRow(entry = entry)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BadgesScreen(onBack: () -> Unit) {
    MainBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TechTopBar(
                    title = "Achievements",
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(padding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(150.dp).background(GlowRed.copy(alpha = 0.05f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Lock, modifier = Modifier.size(64.dp), tint = GlowRed.copy(alpha = 0.2f), contentDescription = null)
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text("RESTRICTED ACCESS", color = Color.White, fontWeight = FontWeight.Black)
                Text(
                    "Complete your first donation to unlock\nyour achievement dossier.",
                    color = Color.Gray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    MainBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TechTopBar(
                    title = "System Settings",
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
                Text("INTERFACE CONFIG", color = GlowRed, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                Spacer(modifier = Modifier.height(16.dp))
                
                TechCard {
                    SettingsToggle(Icons.Default.NotificationsActive, "Push Notifications", true)
                    HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))
                    SettingsToggle(Icons.Default.LocationOn, "Geofence Alerts", true)
                    HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))
                    SettingsToggle(Icons.Default.Security, "Biometric Lock", false)
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                Text("SYSTEM INFO", color = GlowRed, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                Spacer(modifier = Modifier.height(16.dp))
                
                TechCard {
                    ProfileInfoRow(Icons.Default.Info, "Version", "2.0.0-PRO")
                    ProfileInfoRow(Icons.Default.Build, "Build Path", "KTR-RS-ALPHA")
                }
            }
        }
    }
}

@Composable
fun SettingsToggle(icon: ImageVector, label: String, initialValue: Boolean) {
    var checked by remember { mutableStateOf(initialValue) }
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color.Gray)
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, color = Color.White, modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = { checked = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = GlowRed,
                checkedTrackColor = GlowRed.copy(alpha = 0.3f),
                uncheckedThumbColor = Color.Gray,
                uncheckedTrackColor = DarkGray
            )
        )
    }
}
