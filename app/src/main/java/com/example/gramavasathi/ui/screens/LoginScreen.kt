package com.example.gramavasathi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gramavasathi.demo.DemoCredentials
import com.example.gramavasathi.navigation.AppFlow
import com.example.gramavasathi.viewmodel.AppFlowViewModel
import com.example.gramavasathi.viewmodel.AuthState
import com.example.gramavasathi.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    loginFlow: AppFlow,
    onNavigateBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
    appFlowViewModel: AppFlowViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit
) {
    val authState by viewModel.authState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isSignUpMode by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }

    LaunchedEffect(loginFlow) {
        when (loginFlow) {
            AppFlow.Guest -> appFlowViewModel.switchToGuest()
            AppFlow.Host -> appFlowViewModel.switchToHost()
        }
    }

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onLoginSuccess()
        } else if (authState is AuthState.Error) {
            snackbarHostState.showSnackbar(
                (authState as AuthState.Error).message
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (loginFlow == AppFlow.Guest) "Guest sign in" else "Host sign in",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF5EEE6)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFFF5EEE6)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2C1810),
                    titleContentColor = Color(0xFFF5EEE6),
                    navigationIconContentColor = Color(0xFFF5EEE6)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFF2C1810))
        ) {
            // Top section — logo
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Park,
                    contentDescription = null,
                    tint = Color(0xFFF5EEE6),
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Grama Vasathi",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF5EEE6)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Rural Home-stay Accelerator",
                    fontSize = 14.sp,
                    color = Color(0xFFF5EEE6).copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Matti-Vasane — Scent of the Soil",
                    fontSize = 12.sp,
                    fontStyle = FontStyle.Italic,
                    color = Color(0xFFF5EEE6).copy(alpha = 0.6f)
                )
            }

            // Bottom card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(Color.White)
                    .padding(32.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (isSignUpMode) "Create Account" else "Welcome Back",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C1810)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = when {
                            isSignUpMode && loginFlow == AppFlow.Guest ->
                                "Join us to discover authentic rural homestays"
                            isSignUpMode ->
                                "Create your host account to track readiness"
                            loginFlow == AppFlow.Guest ->
                                "Sign in to browse stays and manage bookings"
                            else ->
                                "Sign in to open your host readiness checklist"
                        },
                        fontSize = 13.sp,
                        color = Color(0xFF7C5C3B)
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    // Name field — only for sign up
                    if (isSignUpMode) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Full Name") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = Color(0xFF7C5C3B)
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF2C1810),
                                unfocusedBorderColor = Color(0xFFEDE0D4),
                                focusedLabelColor = Color(0xFF2C1810),
                                cursorColor = Color(0xFF2C1810)
                            ),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Email field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email address") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = Color(0xFF7C5C3B)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2C1810),
                            unfocusedBorderColor = Color(0xFFEDE0D4),
                            focusedLabelColor = Color(0xFF2C1810),
                            cursorColor = Color(0xFF2C1810)
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Password field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color(0xFF7C5C3B)
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    passwordVisible = !passwordVisible
                                }
                            ) {
                                Icon(
                                    imageVector = if (passwordVisible)
                                        Icons.Default.VisibilityOff
                                    else
                                        Icons.Default.Visibility,
                                    contentDescription = if (passwordVisible)
                                        "Hide password"
                                    else
                                        "Show password",
                                    tint = Color(0xFF7C5C3B)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2C1810),
                            unfocusedBorderColor = Color(0xFFEDE0D4),
                            focusedLabelColor = Color(0xFF2C1810),
                            cursorColor = Color(0xFF2C1810)
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = {
                            email = DemoCredentials.emailFor(loginFlow)
                            password = DemoCredentials.passwordFor(loginFlow)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF2C1810)
                        )
                    ) {
                        Text("Fill demo credentials", fontWeight = FontWeight.Medium)
                    }

                    if (loginFlow == AppFlow.Guest) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Demo listing: ${DemoCredentials.DEMO_STAY_NAME}\nID ${DemoCredentials.DEMO_STAY_ID}",
                            fontSize = 11.sp,
                            color = Color(0xFF7C5C3B),
                            lineHeight = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Main button
                    Button(
                        onClick = {
                            if (isSignUpMode) {
                                viewModel.signUpWithEmail(
                                    email.trim(),
                                    password.trim(),
                                    name.trim()
                                )
                            } else {
                                viewModel.signInWithEmailAllowingDemoBootstrap(
                                    email.trim(),
                                    password.trim()
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2C1810)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = authState != AuthState.Loading
                                && email.isNotBlank()
                                && password.isNotBlank()
                    ) {
                        if (authState == AuthState.Loading) {
                            CircularProgressIndicator(
                                color = Color(0xFFF5EEE6),
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = if (isSignUpMode)
                                    "Create Account"
                                else
                                    "Sign In",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF5EEE6)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Toggle sign in / sign up
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isSignUpMode)
                                "Already have an account? "
                            else
                                "New to Grama Vasathi? ",
                            fontSize = 13.sp,
                            color = Color(0xFF7C5C3B)
                        )
                        TextButton(
                            onClick = {
                                isSignUpMode = !isSignUpMode
                                email = ""
                                password = ""
                                name = ""
                            },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = if (isSignUpMode)
                                    "Sign In"
                                else
                                    "Sign Up",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2C1810)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "By continuing you agree to our Terms of Service",
                        fontSize = 10.sp,
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}