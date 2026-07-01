package com.example.ktrrakthaseva.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.example.ktrrakthaseva.data.model.BloodRequest
import com.example.ktrrakthaseva.data.model.BloodType
import com.example.ktrrakthaseva.data.model.RequestStatus
import com.example.ktrrakthaseva.data.model.Urgency
import com.example.ktrrakthaseva.ui.components.*
import com.example.ktrrakthaseva.ui.viewmodel.ChatViewModel
import com.example.ktrrakthaseva.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostRequestScreen(onBack: () -> Unit, viewModel: HomeViewModel = hiltViewModel()) {
    var patientName by remember { mutableStateOf("") }
    var hospitalName by remember { mutableStateOf("") }
    var unitsRequired by remember { mutableStateOf("1") }
    var selectedBloodType by remember { mutableStateOf(BloodType.O_POSITIVE) }
    var selectedUrgency by remember { mutableStateOf(Urgency.MEDIUM) }
    
    val isPosting by viewModel.isPosting.collectAsState()

    MainBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TechTopBar(
                    title = "New Dispatch",
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
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("REQUEST PARAMETERS", color = GlowRed, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                Spacer(modifier = Modifier.height(16.dp))

                TechCard {
                    AuthTextField(patientName, { patientName = it }, "Patient Name", Icons.Default.Person)
                    Spacer(modifier = Modifier.height(16.dp))
                    AuthTextField(hospitalName, { hospitalName = it }, "Hospital & Location", Icons.Default.LocalHospital)
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text("BLOOD TYPE REQUIRED", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .horizontalScroll(rememberScrollState())
                ) {
                    BloodType.entries.forEach { type ->
                        FilterChip(
                            selected = selectedBloodType == type,
                            onClick = { selectedBloodType = type },
                            label = { Text(type.name.replace("_POSITIVE", "+").replace("_NEGATIVE", "-")) },
                            modifier = Modifier.padding(end = 8.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GlowRed,
                                selectedLabelColor = Color.White,
                                labelColor = Color.Gray
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                AuthTextField(unitsRequired, { if (it.all { c -> c.isDigit() }) unitsRequired = it }, "Units Required", Icons.Default.Inventory, keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)

                Spacer(modifier = Modifier.height(24.dp))
                Text("PRIORITY LEVEL", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Urgency.entries.forEach { urgency ->
                        FilterChip(
                            selected = selectedUrgency == urgency,
                            onClick = { selectedUrgency = urgency },
                            label = { Text(urgency.name) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = getUrgencyColor(urgency),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
                Button(
                    onClick = {
                        val request = BloodRequest(
                            patientName = patientName,
                            hospitalName = hospitalName,
                            bloodType = selectedBloodType,
                            unitsRequired = unitsRequired.toIntOrNull() ?: 1,
                            urgency = selectedUrgency
                        )
                        viewModel.postRequest(request) { onBack() }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GlowRed),
                    shape = RoundedCornerShape(12.dp),
                    enabled = patientName.isNotBlank() && hospitalName.isNotBlank() && !isPosting
                ) {
                    if (isPosting) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Text("BROADCAST REQUEST", fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestDetailScreen(
    requestId: String?,
    onBack: () -> Unit,
    onNavigateToChat: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val requests by viewModel.bloodRequests.collectAsState()
    val request = remember(requests, requestId) {
        requests.find { it.requestId == requestId }
    }
    val isAccepting by viewModel.isAccepting.collectAsState()

    MainBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TechTopBar(
                    title = "Operation Intel",
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { requestId?.let { onNavigateToChat(it) } }) {
                            Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Chat", tint = GlowRed)
                        }
                    }
                )
            }
        ) { padding ->
            if (request == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GlowRed)
                }
            } else {
                Column(modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("IDENTIFIER:", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(request.requestId.take(8).uppercase(), color = GlowRed, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.weight(1f))
                        UrgencyChip(urgency = request.urgency)
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    TechCard {
                        Text("PATIENT PROFILE", color = GlowRed, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                        Spacer(modifier = Modifier.height(12.dp))
                        ProfileInfoRow(Icons.Default.Person, "Name", request.patientName)
                        ProfileInfoRow(Icons.Default.Bloodtype, "Blood Group", request.bloodType?.name?.replace("_POSITIVE", "+")?.replace("_NEGATIVE", "-") ?: "ANY")
                        ProfileInfoRow(Icons.Default.Inventory, "Required Units", "${request.unitsRequired}")
                        ProfileInfoRow(Icons.Default.LocalHospital, "Hospital", request.hospitalName)
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(GlowRed.copy(alpha = 0.1f))
                            .border(1.dp, GlowRed.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = GlowRed)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Real-time encryption active. All data is synchronized via secure Firebase channels.", color = Color.Gray, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                    
                    if (request.status == RequestStatus.OPEN) {
                        Button(
                            onClick = { 
                                viewModel.acceptBloodRequest(request.requestId) {
                                    // Feedback on success could be a Toast or Navigation
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(60.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GlowRed),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isAccepting
                        ) {
                            if (isAccepting) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Icon(Icons.Default.VolunteerActivism, contentDescription = null)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("INITIATE DONATION", fontWeight = FontWeight.Black, fontSize = 18.sp)
                            }
                        }
                    } else {
                        TechCard {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                Icon(Icons.Default.CheckCircle, null, tint = Color.Green)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("MISSION IN PROGRESS", color = Color.Green, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(requestId: String?, onBack: () -> Unit, viewModel: ChatViewModel = hiltViewModel()) {
    val messages by viewModel.messages.collectAsState()
    var messageText by remember { mutableStateOf("") }
    LaunchedEffect(requestId) { requestId?.let { viewModel.setRequestId(it) } }

    MainBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TechTopBar(
                    title = "Secure Comms",
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            },
            bottomBar = {
                Surface(
                    color = DarkGray,
                    modifier = Modifier.fillMaxWidth().imePadding()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Encrypted message...", color = Color.Gray) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedIndicatorColor = GlowRed
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { if (messageText.isNotBlank()) { viewModel.sendMessage(messageText); messageText = "" } },
                            modifier = Modifier.background(GlowRed, CircleShape)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
                        }
                    }
                }
            }
        ) { padding ->
            if (messages.isEmpty()) {
                EmptyState(message = "Establish a connection. Start secure transmission.", icon = Icons.AutoMirrored.Filled.Chat)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                    reverseLayout = false
                ) {
                    items(messages) { message ->
                        ChatBubble(message.text, isFromMe = true) // Placeholder logic for sender
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(text: String, isFromMe: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = if (isFromMe) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = if (isFromMe) GlowRed else DarkGray,
            shape = RoundedCornerShape(
                topStart = 12.dp,
                topEnd = 12.dp,
                bottomStart = if (isFromMe) 12.dp else 0.dp,
                bottomEnd = if (isFromMe) 0.dp else 12.dp
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(text, color = Color.White, modifier = Modifier.padding(12.dp), fontSize = 14.sp)
        }
    }
}

@Composable
fun HistoryScreen(onBack: () -> Unit) {
    MainBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TechTopBar(
                    title = "Operation History",
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                EmptyState(message = "No mission logs found. Complete a donation to record your impact.", icon = Icons.AutoMirrored.Filled.ListAlt)
            }
        }
    }
}
