package com.example.ktrrakthaseva.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ktrrakthaseva.data.model.BloodRequest
import com.example.ktrrakthaseva.data.model.LeaderboardEntry
import com.example.ktrrakthaseva.data.model.Urgency
import com.example.ktrrakthaseva.data.model.User

// High-tech Theme Colors
val DarkRed = Color(0xFF8B0000)
val DeepBlack = Color(0xFF0F0F0F)
val GlowRed = Color(0xFFFF3131)
val DarkGray = Color(0xFF1E1E1E)

@Composable
fun MainBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DeepBlack, DarkRed.copy(alpha = 0.1f), DeepBlack)
                )
            )
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TechTopBar(
    title: String,
    actions: @Composable RowScope.() -> Unit = {},
    navigationIcon: @Composable () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                title.uppercase(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = Color.White
            )
        },
        navigationIcon = navigationIcon,
        actions = actions,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent,
            navigationIconContentColor = Color.White,
            actionIconContentColor = GlowRed,
            titleContentColor = Color.White
        )
    )
}

@Composable
fun TechCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(1.dp, Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        colors = CardDefaults.cardColors(containerColor = DarkGray.copy(alpha = 0.8f)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
fun BloodRequestCard(request: BloodRequest, onClick: () -> Unit) {
    TechCard(onClick = onClick) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(GlowRed.copy(alpha = 0.15f))
                        .border(1.dp, GlowRed.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bloodtype,
                        contentDescription = null,
                        tint = GlowRed,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = request.bloodType?.name?.replace("_POSITIVE", "+")?.replace("_NEGATIVE", "-") ?: "ANY",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = request.hospitalName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.LightGray,
                        maxLines = 1
                    )
                }
            }
            UrgencyChip(urgency = request.urgency)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Progress", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
            Text("${request.unitsCollected}/${request.unitsRequired} Units", color = GlowRed, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
        }
        Spacer(modifier = Modifier.height(8.dp))
        val progress = if (request.unitsRequired > 0) request.unitsCollected.toFloat() / request.unitsRequired else 0f
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = GlowRed,
            trackColor = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(request.address, color = Color.Gray, style = MaterialTheme.typography.bodySmall, maxLines = 1)
        }
    }
}

@Composable
fun DonorCard(user: User, onConnect: () -> Unit) {
    TechCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(GlowRed, DarkRed)))
                    .padding(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(DarkGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        user.name.take(1).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(user.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Bloodtype, null, tint = GlowRed, modifier = Modifier.size(14.dp))
                    Text(
                        user.bloodType?.name?.replace("_POSITIVE", "+")?.replace("_NEGATIVE", "-") ?: "Donor",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("•", color = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(user.address, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
            Button(
                onClick = onConnect,
                colors = ButtonDefaults.buttonColors(containerColor = GlowRed),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text("CONNECT", fontSize = 12.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
fun LeaderboardRow(entry: LeaderboardEntry) {
    TechCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(32.dp),
                contentAlignment = Alignment.Center
            ) {
                if (entry.rank <= 3) {
                    Icon(
                        Icons.Default.EmojiEvents,
                        null,
                        tint = when (entry.rank) {
                            1 -> Color(0xFFFFD700)
                            2 -> Color(0xFFC0C0C0)
                            else -> Color(0xFFCD7F32)
                        },
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("#${entry.rank}", color = Color.Gray, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(entry.name, color = Color.White, fontWeight = FontWeight.Bold)
                Text("${entry.totalDonations} Donations", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            }
            Text(
                "${entry.points} PTS",
                color = GlowRed,
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun StatCard(title: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .border(1.dp, GlowRed.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = DarkGray)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = GlowRed, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = Color.White)
            Text(title, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
    }
}

@Composable
fun EmptyState(message: String, icon: ImageVector = Icons.Default.Info) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(GlowRed.copy(alpha = 0.05f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, modifier = Modifier.size(48.dp), tint = GlowRed.copy(alpha = 0.4f))
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            message,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
fun LoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = GlowRed)
    }
}

@Composable
fun UrgencyChip(urgency: Urgency) {
    val color = getUrgencyColor(urgency)
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Text(
            urgency.name,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            color = color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

fun getUrgencyColor(urgency: Urgency): Color = when (urgency) {
    Urgency.LOW -> Color(0xFF4CAF50)
    Urgency.MEDIUM -> Color(0xFFFFC107)
    Urgency.HIGH -> Color(0xFFFF9800)
    Urgency.EMERGENCY -> Color(0xFFFF3131)
}
