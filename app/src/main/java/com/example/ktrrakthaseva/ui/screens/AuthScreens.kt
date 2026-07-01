package com.example.ktrrakthaseva.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ktrrakthaseva.data.model.*
import com.example.ktrrakthaseva.ui.components.LoadingOverlay
import com.example.ktrrakthaseva.ui.viewmodel.AuthState
import com.example.ktrrakthaseva.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

// High-tech Theme Colors
private val DarkRed = Color(0xFF8B0000)
private val DeepBlack = Color(0xFF0F0F0F)
private val GlowRed = Color(0xFFFF3131)
private val DarkGray = Color(0xFF1E1E1E)

@Composable
fun AuthBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DeepBlack, DarkRed.copy(alpha = 0.2f), DeepBlack)
                )
            )
    ) {
        content()
    }
}

@Composable
fun SplashScreen(onAnimationFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000)
        onAnimationFinished()
    }
    AuthBackground {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.Bloodtype,
                    contentDescription = null,
                    tint = GlowRed,
                    modifier = Modifier.size(120.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "RAKTA SEVA",
                    color = Color.White,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 4.sp
                )
                Text(
                    "GIFT OF LIFE",
                    color = GlowRed,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Light,
                    letterSpacing = 2.sp
                )
            }
        }
    }
}

@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    AuthBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Icon(Icons.Default.VolunteerActivism, null, tint = GlowRed, modifier = Modifier.size(100.dp))
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                "Every drop counts.",
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Connecting voluntary blood donors with those in need, instantly and securely.",
                color = Color.LightGray,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(64.dp))
            Button(
                onClick = onFinished,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GlowRed),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("GET STARTED", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showForgotDialog by remember { mutableStateOf(false) }

    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            onLoginSuccess()
        }
    }

    AuthBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Bloodtype, null, tint = GlowRed, modifier = Modifier.size(80.dp))
            Text("LOGIN", color = Color.White, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black)
            
            Spacer(modifier = Modifier.height(48.dp))

            AuthTextField(value = email, onValueChange = { email = it }, label = "Email Address", icon = Icons.Default.Email)
            Spacer(modifier = Modifier.height(16.dp))
            
            Column(modifier = Modifier.fillMaxWidth()) {
                AuthTextField(
                    value = password, 
                    onValueChange = { password = it }, 
                    label = "Password", 
                    icon = Icons.Default.Lock,
                    isPassword = true
                )
                TextButton(
                    onClick = { showForgotDialog = true },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Forgot Password?", color = GlowRed, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.login(email, password) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GlowRed),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("LOGIN", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Google Sign In
            OutlinedButton(
                onClick = { /* Implement Google Sign In */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.Gray)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("G", color = Color(0xFF4285F4), fontWeight = FontWeight.Black, fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Sign in with Google", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Don't have an account?", color = Color.Gray)
                TextButton(onClick = onNavigateToRegister) {
                    Text("Sign Up", color = GlowRed, fontWeight = FontWeight.Bold)
                }
            }

            if (authState is AuthState.Error) {
                Text(
                    (authState as AuthState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    if (showForgotDialog) {
        ForgotPasswordDialog(
            onDismiss = { showForgotDialog = false },
            onSend = { viewModel.sendPasswordReset(it) }
        )
    }

    if (authState is AuthState.Loading) LoadingOverlay()
}

@Composable
fun ForgotPasswordDialog(onDismiss: () -> Unit, onSend: (String) -> Unit) {
    var email by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkGray,
        titleContentColor = Color.White,
        textContentColor = Color.LightGray,
        title = { Text("Reset Password") },
        text = {
            Column {
                Text("Enter your email to receive a recovery link.")
                Spacer(modifier = Modifier.height(16.dp))
                AuthTextField(value = email, onValueChange = { email = it }, label = "Email", icon = Icons.Default.Email)
            }
        },
        confirmButton = {
            Button(onClick = { onSend(email); onDismiss() }, colors = ButtonDefaults.buttonColors(containerColor = GlowRed)) {
                Text("Send Link")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Color.Gray) }
        }
    )
}

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var step by remember { mutableStateOf(1) }
    val authState by viewModel.authState.collectAsState()

    // Form States
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf(Gender.MALE) }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var bloodType by remember { mutableStateOf<BloodType?>(null) }
    var donorType by remember { mutableStateOf(DonorType.VOLUNTARY) }
    var hasMedical by remember { mutableStateOf(false) }
    var medicalNotes by remember { mutableStateOf("") }
    var agreeTerms by remember { mutableStateOf(false) }
    var agreePrivacy by remember { mutableStateOf(false) }
    var agreeAlerts by remember { mutableStateOf(false) }

    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) onRegisterSuccess()
    }

    AuthBackground {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Spacer(modifier = Modifier.height(16.dp))
            RegisterStepIndicator(currentStep = step)
            Spacer(modifier = Modifier.height(32.dp))

            Box(modifier = Modifier.weight(1f)) {
                AnimatedContent(targetState = step, transitionSpec = { fadeIn() togetherWith fadeOut() }) { targetStep ->
                    when (targetStep) {
                        1 -> RegisterStep1(name, { name = it }, email, { email = it }, phone, { phone = it }, password, { password = it }, confirmPassword, { confirmPassword = it })
                        2 -> RegisterStep2(dob, { dob = it }, weight, { weight = it }, gender, { gender = it }, city, { city = it }, state, { state = it }, bloodType, { bloodType = it }, donorType, { donorType = it }, hasMedical, { hasMedical = it }, medicalNotes, { medicalNotes = it })
                        3 -> RegisterStep3(name, email, bloodType, agreeTerms, { agreeTerms = it }, agreePrivacy, { agreePrivacy = it }, agreeAlerts, { agreeAlerts = it })
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                if (step > 1) {
                    OutlinedButton(
                        onClick = { step-- },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, GlowRed)
                    ) { Text("BACK", color = GlowRed) }
                    Spacer(modifier = Modifier.width(16.dp))
                }
                
                Button(
                    onClick = {
                        if (step < 3) step++
                        else viewModel.register(name, email, phone, password, confirmPassword, dob, gender, weight, city, state, bloodType, donorType, hasMedical, medicalNotes, agreeTerms, agreePrivacy)
                    },
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GlowRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (step < 3) "NEXT" else "CREATE ACCOUNT", fontWeight = FontWeight.Bold)
                }
            }

            if (authState is AuthState.Error) {
                Text((authState as AuthState.Error).message, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp).fillMaxWidth(), textAlign = TextAlign.Center)
            }
        }
    }
    if (authState is AuthState.Loading) LoadingOverlay()
}

@Composable
fun RegisterStepIndicator(currentStep: Int) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        repeat(3) { index ->
            val step = index + 1
            val isActive = step <= currentStep
            Box(
                modifier = Modifier
                    .size(if (step == currentStep) 12.dp else 8.dp)
                    .clip(CircleShape)
                    .background(if (isActive) GlowRed else Color.Gray)
            )
            if (index < 2) {
                Box(modifier = Modifier.width(40.dp).height(2.dp).background(if (step < currentStep) GlowRed else Color.Gray))
            }
        }
    }
}

@Composable
fun RegisterStep1(
    name: String, onNameChange: (String) -> Unit,
    email: String, onEmailChange: (String) -> Unit,
    phone: String, onPhoneChange: (String) -> Unit,
    psw: String, onPswChange: (String) -> Unit,
    confirmPsw: String, onConfirmPswChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Text("Personal Details", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        AuthTextField(name, onNameChange, "Full Name", Icons.Default.Person)
        Spacer(modifier = Modifier.height(16.dp))
        AuthTextField(email, onEmailChange, "Email Address", Icons.Default.Email)
        Spacer(modifier = Modifier.height(16.dp))
        AuthTextField(phone, onPhoneChange, "Phone Number", Icons.Default.Phone, keyboardType = KeyboardType.Phone)
        Spacer(modifier = Modifier.height(16.dp))
        AuthTextField(psw, onPswChange, "Password", Icons.Default.Lock, isPassword = true)
        
        // Strength Meter
        PasswordStrengthMeter(psw)
        
        Spacer(modifier = Modifier.height(16.dp))
        AuthTextField(confirmPsw, onConfirmPswChange, "Confirm Password", Icons.Default.Lock, isPassword = true)
    }
}

@Composable
fun RegisterStep2(
    dob: String, onDobChange: (String) -> Unit,
    weight: String, onWeightChange: (String) -> Unit,
    gender: String, onGenderChange: (String) -> Unit,
    city: String, onCityChange: (String) -> Unit,
    state: String, onStateChange: (String) -> Unit,
    bloodType: BloodType?, onBloodTypeChange: (BloodType?) -> Unit,
    donorType: String, onDonorTypeChange: (String) -> Unit,
    hasMedical: Boolean, onHasMedicalChange: (Boolean) -> Unit,
    medicalNotes: String, onMedicalNotesChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Text("Medical Information", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(modifier = Modifier.fillMaxWidth()) {
            AuthTextField(dob, onDobChange, "DOB (DD/MM/YYYY)", Icons.Default.CalendarToday, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(16.dp))
            AuthTextField(weight, onWeightChange, "Weight (kg)", Icons.Default.MonitorWeight, modifier = Modifier.weight(1f), keyboardType = KeyboardType.Number)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text("Gender", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf(Gender.MALE, Gender.FEMALE, Gender.OTHER).forEach { g ->
                FilterChip(
                    selected = gender == g,
                    onClick = { onGenderChange(g) },
                    label = { Text(g) },
                    modifier = Modifier.padding(end = 8.dp),
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GlowRed, selectedLabelColor = Color.White, labelColor = Color.Gray)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            AuthTextField(city, onCityChange, "City", Icons.Default.LocationCity, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(16.dp))
            AuthTextField(state, onStateChange, "State", Icons.Default.Map, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Blood Group", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
        LazyVerticalGrid(columns = GridCells.Fixed(4), modifier = Modifier.height(100.dp), userScrollEnabled = false) {
            items(BloodType.values()) { type ->
                val isSelected = bloodType == type
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) GlowRed else DarkGray)
                        .clickable { onBloodTypeChange(type) }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(type.name.replace("_POSITIVE", "+").replace("_NEGATIVE", "-"), color = if (isSelected) Color.White else Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Medical Conditions?", color = Color.White)
            Spacer(modifier = Modifier.weight(1f))
            Switch(checked = hasMedical, onCheckedChange = onHasMedicalChange, colors = SwitchDefaults.colors(checkedThumbColor = GlowRed))
        }
        if (hasMedical) {
            AuthTextField(medicalNotes, onMedicalNotesChange, "Describe conditions...", Icons.Default.Notes)
        }
    }
}

@Composable
fun RegisterStep3(
    name: String, email: String, bloodType: BloodType?,
    agreeTerms: Boolean, onAgreeTerms: (Boolean) -> Unit,
    agreePrivacy: Boolean, onAgreePrivacy: (Boolean) -> Unit,
    agreeAlerts: Boolean, onAgreeAlerts: (Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Text("Review & Consent", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        
        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = DarkGray)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Profile Preview", color = GlowRed, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Name: $name", color = Color.White)
                Text("Email: $email", color = Color.White)
                Text("Blood Type: ${bloodType?.name ?: "Not selected"}", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        ConsentCheckbox("I agree to the Terms of Service", agreeTerms, onAgreeTerms)
        ConsentCheckbox("I agree to the Privacy Policy", agreePrivacy, onAgreePrivacy)
        ConsentCheckbox("Receive urgent blood request alerts", agreeAlerts, onAgreeAlerts)

        Spacer(modifier = Modifier.height(32.dp))
        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(GlowRed.copy(alpha = 0.1f)).padding(16.dp)) {
            Row {
                Icon(Icons.Default.Info, null, tint = GlowRed)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("What happens next?", color = GlowRed, fontWeight = FontWeight.Bold)
                    Text("Your profile will be visible to hospitals and recipients in need. You can toggle your availability anytime.", color = Color.LightGray, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun ConsentCheckbox(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { onCheckedChange(!checked) }.padding(vertical = 4.dp)) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange, colors = CheckboxDefaults.colors(checkedColor = GlowRed))
        Text(label, color = Color.LightGray, fontSize = 14.sp)
    }
}

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        leadingIcon = { Icon(icon, null, tint = Color.Gray) },
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedTextColor = Color.White,
            focusedTextColor = Color.White,
            focusedBorderColor = GlowRed,
            unfocusedBorderColor = Color.Gray,
            focusedLabelColor = GlowRed,
            unfocusedLabelColor = Color.Gray
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}

@Composable
fun PasswordStrengthMeter(psw: String) {
    if (psw.isEmpty()) return
    val strength = when {
        psw.length < 6 -> 0.25f
        psw.length < 10 -> 0.5f
        psw.any { it.isDigit() } && psw.any { it.isUpperCase() } -> 1f
        else -> 0.75f
    }
    val (color, label) = when (strength) {
        0.25f -> Color.Red to "Weak"
        0.5f -> Color(0xFFFFA500) to "Fair"
        0.75f -> Color.Yellow to "Good"
        else -> Color.Green to "Strong"
    }
    
    Column(modifier = Modifier.padding(top = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Strength: $label", color = color, fontSize = 12.sp)
        }
        LinearProgressIndicator(progress = { strength }, modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape), color = color, trackColor = Color.Gray)
    }
}
