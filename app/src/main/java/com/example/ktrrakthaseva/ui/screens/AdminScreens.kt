package com.example.ktrrakthaseva.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ktrrakthaseva.ui.components.*
import com.example.ktrrakthaseva.ui.viewmodel.AdminViewModel

@Composable
fun AdminDashboardScreen(
    onBack: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val stats by viewModel.adminStats.collectAsState()

    MainBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TechTopBar(
                    title = "Command Center",
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
                Text("OPERATIONAL OVERVIEW", color = GlowRed, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    StatCard("Active Emergencies", "${stats.activeRequests}", Icons.Default.Warning, modifier = Modifier.weight(1f))
                    StatCard("Available Donors", "${stats.availableDonors}", Icons.Default.Person, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    StatCard("Avg Response", "${stats.avgResponseTimeMin.toInt()}m", Icons.Default.Timer, modifier = Modifier.weight(1f))
                    StatCard("Success Rate", "${stats.successRate.toInt()}%", Icons.Default.CheckCircle, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text("DEMAND BY BLOOD GROUP", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(8.dp))
                
                TechCard {
                    if (stats.demandTrends.isEmpty()) {
                        Text("No demand data available.", color = Color.Gray, modifier = Modifier.padding(16.dp))
                    } else {
                        stats.demandTrends.forEach { (type, count) ->
                            AdminDemandRow(type, count, stats.demandTrends.values.maxOrNull() ?: 1)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text("NETWORK HEALTH", color = GlowRed, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                Spacer(modifier = Modifier.height(16.dp))
                
                TechCard {
                    ProfileInfoRow(Icons.Default.Groups, "Total Enrolled Donors", "${stats.totalDonors}")
                    ProfileInfoRow(Icons.Default.CloudSync, "System Status", "OPTIMAL")
                    ProfileInfoRow(Icons.Default.Security, "Firewall Integrity", "256-BIT")
                }

                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = { /* Generate Report Logic */ },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkGray),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GlowRed.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.Description, contentDescription = null, tint = GlowRed)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("GENERATE MISSION LOG REPORT", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AdminDemandRow(type: String, count: Int, max: Int) {
    val progress = count.toFloat() / max
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(type, color = Color.White, fontWeight = FontWeight.Bold)
            Text("$count Requests", color = GlowRed, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
            color = GlowRed,
            trackColor = DarkGray,
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )
    }
}
