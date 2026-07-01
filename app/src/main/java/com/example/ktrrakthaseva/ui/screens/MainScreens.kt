package com.example.ktrrakthaseva.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ktrrakthaseva.data.model.BloodType
import com.example.ktrrakthaseva.ui.components.*
import com.example.ktrrakthaseva.ui.viewmodel.HomeViewModel
import com.example.ktrrakthaseva.ui.viewmodel.MapViewModel
import com.example.ktrrakthaseva.ui.viewmodel.SearchViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

// High-tech Theme Colors
private val DarkRed = Color(0xFF8B0000)
private val DeepBlack = Color(0xFF0F0F0F)
private val GlowRed = Color(0xFFFF3131)
private val DarkGray = Color(0xFF1E1E1E)

@Composable
fun AnimatedTechButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = GlowRed,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    Button(
        onClick = onClick,
        modifier = modifier.scale(scale),
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        shape = RoundedCornerShape(12.dp)
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
fun HomeScreen(
    onNavigateToPostRequest: () -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToMap: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToFindDonors: () -> Unit,
    onNavigateToAlerts: () -> Unit,
    onNavigateToRequestDetail: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val requests by viewModel.bloodRequests.collectAsState()
    var showMenu by remember { mutableStateOf(false) }

    MainBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TechTopBar(
                    title = "Rakta Seva",
                    actions = {
                        IconButton(onClick = onNavigateToAlerts) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = "Alerts")
                        }
                        IconButton(onClick = { showMenu = !showMenu }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(DarkGray)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Leaderboard", color = Color.White) },
                                onClick = { showMenu = false; onNavigateToLeaderboard() },
                                leadingIcon = { Icon(Icons.Default.EmojiEvents, null, tint = GlowRed) }
                            )
                            DropdownMenuItem(
                                text = { Text("Profile", color = Color.White) },
                                onClick = { showMenu = false; onNavigateToProfile() },
                                leadingIcon = { Icon(Icons.Default.AccountCircle, null, tint = GlowRed) }
                            )
                            DropdownMenuItem(
                                text = { Text("History", color = Color.White) },
                                onClick = { showMenu = false; onNavigateToHistory() },
                                leadingIcon = { Icon(Icons.AutoMirrored.Filled.ListAlt, null, tint = GlowRed) }
                            )
                            DropdownMenuItem(
                                text = { Text("Settings", color = Color.White) },
                                onClick = { showMenu = false; onNavigateToSettings() },
                                leadingIcon = { Icon(Icons.Default.Settings, null, tint = GlowRed) }
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onNavigateToPostRequest,
                    containerColor = GlowRed,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Post Request", modifier = Modifier.size(32.dp))
                }
            },
            bottomBar = {
                TechBottomNavigation(
                    onMapClick = onNavigateToMap,
                    onSearchClick = onNavigateToFindDonors,
                    onProfileClick = onNavigateToProfile
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).padding(horizontal = 16.dp)) {
                if (requests.isEmpty()) {
                    EmptyState(message = "No active blood requests in your area.", icon = Icons.Default.Bloodtype)
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "EMERGENCY REQUESTS",
                                style = MaterialTheme.typography.labelLarge,
                                color = GlowRed,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        items(requests) { request ->
                            BloodRequestCard(
                                request = request,
                                onClick = { onNavigateToRequestDetail(request.requestId) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TechBottomNavigation(
    onMapClick: () -> Unit,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Surface(
        color = DarkGray.copy(alpha = 0.9f),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = Modifier.fillMaxWidth().height(80.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onMapClick) {
                Icon(Icons.Default.Map, "Map", tint = Color.Gray)
            }
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.size(56.dp).background(GlowRed.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(Icons.Default.Search, "Search", tint = GlowRed, modifier = Modifier.size(28.dp))
            }
            IconButton(onClick = onProfileClick) {
                Icon(Icons.Default.Person, "Profile", tint = Color.Gray)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindDonorsScreen(
    onBack: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val results by viewModel.searchResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    var searchTypeAI by remember { mutableStateOf(true) }

    MainBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TechTopBar(
                    title = if (searchTypeAI) "AI SMART MATCH" else "FIND DONORS",
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { searchTypeAI = !searchTypeAI }) {
                            Icon(if (searchTypeAI) Icons.Default.AutoAwesome else Icons.Default.Search, contentDescription = "Toggle Search Type", tint = GlowRed)
                        }
                    }
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { 
                        searchQuery = it
                        if (it.length >= 2) viewModel.searchDonors(null, null, 10.0) 
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(if (searchTypeAI) "AI scanning for blood types..." else "Search Blood Type (O+, A- ...)", color = Color.Gray) },
                    leadingIcon = { Icon(if (searchTypeAI) Icons.Default.AutoAwesome else Icons.Default.Search, contentDescription = null, tint = GlowRed) },
                    trailingIcon = { if (searchQuery.isNotEmpty()) IconButton(onClick = { searchQuery = "" }) { Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray) } },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GlowRed,
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = DarkGray.copy(alpha = 0.5f),
                        unfocusedContainerColor = DarkGray.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (searchTypeAI) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(GlowRed.copy(alpha = 0.1f))
                            .padding(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = GlowRed, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("AI Matching prioritized by compatibility & distance.", color = GlowRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                if (isSearching) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = GlowRed)
                    }
                } else if (results.isEmpty()) {
                    EmptyState(message = if (searchTypeAI) "Initiate AI scan for compatible donors." else "Enter a blood type to find nearby life-savers.", icon = if (searchTypeAI) Icons.Default.AutoAwesome else Icons.Default.PersonSearch)
                } else {
                    LazyColumn {
                        items(results) { donor ->
                            DonorCard(user = donor, onConnect = { /* Connect Logic */ })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BloodMapScreen(onBack: () -> Unit, viewModel: MapViewModel = hiltViewModel()) {
    val userLoc by viewModel.userLocation.collectAsState()
    val nearbyDonors by viewModel.nearbyDonors.collectAsState()
    val activeRequests by viewModel.activeRequests.collectAsState()
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(userLoc.latitude, userLoc.longitude), 12f)
    }

    LaunchedEffect(userLoc) {
        cameraPositionState.animate(
            CameraUpdateFactory.newLatLng(LatLng(userLoc.latitude, userLoc.longitude))
        )
    }

    MainBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TechTopBar(
                    title = "LIVE NETWORK",
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        isMyLocationEnabled = true, 
                        mapType = MapType.SATELLITE,
                        isTrafficEnabled = true
                    ),
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = false,
                        myLocationButtonEnabled = true
                    )
                ) {
                    nearbyDonors.forEach { donor ->
                        donor.location?.let { loc ->
                            Marker(
                                state = MarkerState(position = LatLng(loc.latitude, loc.longitude)),
                                title = "Donor: ${donor.name}",
                                snippet = "Blood Type: ${donor.bloodType}",
                                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                            )
                        }
                    }

                    activeRequests.forEach { request ->
                        request.hospitalLocation?.let { loc ->
                            Marker(
                                state = MarkerState(position = LatLng(loc.latitude, loc.longitude)),
                                title = "Request: ${request.bloodType}",
                                snippet = "Hospital: ${request.hospitalName}",
                                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                            )
                        }
                    }
                }

                // Overlay Controls
                Column(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    FloatingActionButton(
                        onClick = { /* Refresh handled by flow */ },
                        containerColor = DarkGray,
                        contentColor = GlowRed,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
                
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(24.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(DeepBlack.copy(alpha = 0.8f))
                        .border(1.dp, GlowRed.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(GlowRed))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("${activeRequests.size} Active Emergency Requests in your area", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AlertsScreen(onBack: () -> Unit) {
    MainBackground {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TechTopBar(
                    title = "Broadcasts",
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                EmptyState(message = "No emergency broadcasts in your vicinity.", icon = Icons.Default.NotificationsActive)
            }
        }
    }
}
