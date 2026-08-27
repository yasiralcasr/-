package com.example.ui.screens

import android.app.Activity
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.AppLanguage
import com.example.data.model.RoleRank
import com.example.ui.theme.*

enum class AuthModeTab {
    EMAIL_PASSWORD,
    SOVEREIGN_ROOT,
    DEMO_PRESETS
}

@Composable
fun LoginScreen(
    language: AppLanguage,
    isLoading: Boolean,
    errorMessage: String?,
    onSignInWithGoogle: (Activity) -> Unit,
    onSignInWithEmail: (email: String, pass: String) -> Unit,
    onSignUpWithEmail: (email: String, pass: String, name: String) -> Unit,
    onContinueAsGuest: () -> Unit,
    onOpenSovereignDialog: () -> Unit,
    onSendPasswordReset: ((String) -> Unit)? = null,
    onSelectPresetRole: ((RoleRank) -> Unit)? = null,
    onToggleLanguage: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isAr = language == AppLanguage.ARABIC
    val context = LocalContext.current
    val activity = context as? Activity

    var selectedTab by remember { mutableStateOf(AuthModeTab.EMAIL_PASSWORD) }
    var isSignUpMode by remember { mutableStateOf(false) }

    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(true) }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Forgot password dialog
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var forgotPasswordEmail by remember { mutableStateOf("") }

    // Sovereign root code quick input
    var sovereignCodeInput by remember { mutableStateOf("") }
    var isBiometricScanning by remember { mutableStateOf(false) }

    // Pulsing animation for active security node
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Navy900,
                        Color(0xFF071120),
                        Navy800,
                        Navy900
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 18.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Top Quick Bar: Language & Live Node Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Live Node Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Navy800.copy(alpha = 0.85f),
                    border = BorderStroke(1.dp, Cyan500.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(GreenSuccess.copy(alpha = pulseAlpha))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAr) "العقدة: الرياض-01 (مؤمنة)" else "NODE: SA-RYD-01 (SECURE)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Cyan300
                            )
                        )
                    }
                }

                // Language Toggle Button
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Navy800.copy(alpha = 0.85f),
                    border = BorderStroke(1.dp, Gold500.copy(alpha = 0.6f)),
                    modifier = Modifier.clickable { onToggleLanguage?.invoke() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Change Language",
                            tint = Gold400,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAr) "English 🌐" else "العربية 🇸🇦",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Gold300
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Brand Identity Card (Father Yasser & Sovereign Platform)
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Navy800.copy(alpha = 0.95f),
                border = BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(Gold500, Cyan400, Gold400))),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_brand_header")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar & Glowing Golden Crown Ring
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(92.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(92.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(Gold400.copy(alpha = 0.35f), Color.Transparent)
                                    )
                                )
                        )
                        Box(
                            modifier = Modifier
                                .size(78.dp)
                                .clip(CircleShape)
                                .border(2.5.dp, Gold400, CircleShape)
                                .background(Navy900)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_father_yasser_avatar),
                                contentDescription = "Father Yasser Portrait",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Floating Crown Badge
                        Surface(
                            shape = CircleShape,
                            color = Navy900,
                            border = BorderStroke(1.dp, Gold400),
                            modifier = Modifier
                                .size(26.dp)
                                .align(Alignment.BottomEnd)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "👑", fontSize = 13.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isAr) "شركة الشرق والغرب للحلول الذكية" else "East & West Global Smart Solutions",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Gold300,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                    )

                    Text(
                        text = if (isAr) "بوابة المصادقة السيادية وإدارة الصلاحيات الشاملة" else "Sovereign Authentication & Role Governance Portal",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Slate300,
                            fontSize = 11.5.sp,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(top = 3.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Security Badges Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Navy700,
                            border = BorderStroke(0.5.dp, Cyan400.copy(alpha = 0.7f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Shield, contentDescription = null, tint = Cyan400, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "ISO-27001",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Cyan300,
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Navy700,
                            border = BorderStroke(0.5.dp, GreenSuccess.copy(alpha = 0.7f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = GreenSuccess, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "TLS 1.3 & SHA-256",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = GreenSuccess,
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Error Banner if present
            AnimatedVisibility(
                visible = errorMessage != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                if (errorMessage != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = RedDanger.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, RedDanger),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .testTag("auth_error_banner")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = RedDanger, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = errorMessage,
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 16.sp)
                            )
                        }
                    }
                }
            }

            // PRIMARY: Google Sign-In with Credential Manager Button
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                shadowElevation = 6.dp,
                border = BorderStroke(1.dp, Color(0xFFDADCE0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable(enabled = !isLoading) {
                        if (activity != null) {
                            onSignInWithGoogle(activity)
                        }
                    }
                    .testTag("google_credential_manager_sign_in_button")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Google Multi-Color Icon Representation
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF4285F4),
                        modifier = Modifier.size(26.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "G",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                fontFamily = FontFamily.SansSerif
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = if (isAr) "الدخول بحساب Google المعتمد" else "Sign in with Google Account",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F1F1F),
                            fontSize = 14.5.sp
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFE8F0FE)
                    ) {
                        Text(
                            text = "One-Tap",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF1967D2),
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.5.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Divider "OR CHOOSE METHOD"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Slate700.copy(alpha = 0.7f))
                Text(
                    text = if (isAr) " أو اختر طريقة الدخول " else " OR CHOOSE METHOD ",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Slate400,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.5.sp
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = Slate700.copy(alpha = 0.7f))
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Multi-Mode Tabs Selector
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Navy900,
                border = BorderStroke(1.dp, Slate700),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    // Tab 1: Email/Password
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (selectedTab == AuthModeTab.EMAIL_PASSWORD) Navy700 else Color.Transparent,
                        border = if (selectedTab == AuthModeTab.EMAIL_PASSWORD) BorderStroke(1.dp, Gold400) else null,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { selectedTab = AuthModeTab.EMAIL_PASSWORD }
                            .testTag("tab_email_auth")
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = if (selectedTab == AuthModeTab.EMAIL_PASSWORD) Gold400 else Slate400,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isAr) "البريد وكلمة السر" else "Email / Pass",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.5.sp,
                                    fontWeight = if (selectedTab == AuthModeTab.EMAIL_PASSWORD) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == AuthModeTab.EMAIL_PASSWORD) Gold300 else Slate400
                                )
                            )
                        }
                    }

                    // Tab 2: Sovereign Root Key
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (selectedTab == AuthModeTab.SOVEREIGN_ROOT) Navy700 else Color.Transparent,
                        border = if (selectedTab == AuthModeTab.SOVEREIGN_ROOT) BorderStroke(1.dp, Gold400) else null,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { selectedTab = AuthModeTab.SOVEREIGN_ROOT }
                            .testTag("tab_sovereign_root")
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.VpnKey,
                                contentDescription = null,
                                tint = if (selectedTab == AuthModeTab.SOVEREIGN_ROOT) Gold400 else Slate400,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isAr) "كود السيادة (Root)" else "Master Root",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.5.sp,
                                    fontWeight = if (selectedTab == AuthModeTab.SOVEREIGN_ROOT) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == AuthModeTab.SOVEREIGN_ROOT) Gold300 else Slate400
                                )
                            )
                        }
                    }

                    // Tab 3: Demo Fast Login
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (selectedTab == AuthModeTab.DEMO_PRESETS) Navy700 else Color.Transparent,
                        border = if (selectedTab == AuthModeTab.DEMO_PRESETS) BorderStroke(1.dp, Gold400) else null,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { selectedTab = AuthModeTab.DEMO_PRESETS }
                            .testTag("tab_demo_presets")
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = null,
                                tint = if (selectedTab == AuthModeTab.DEMO_PRESETS) Gold400 else Slate400,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isAr) "دخول سريع" else "Demo Roles",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.5.sp,
                                    fontWeight = if (selectedTab == AuthModeTab.DEMO_PRESETS) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == AuthModeTab.DEMO_PRESETS) Gold300 else Slate400
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // TAB CONTENT BOX
            when (selectedTab) {
                AuthModeTab.EMAIL_PASSWORD -> {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Navy800,
                        border = BorderStroke(1.dp, Slate700),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            // Sub-toggle: Sign In vs Sign Up
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Navy900)
                                    .padding(3.dp)
                            ) {
                                Button(
                                    onClick = { isSignUpMode = false },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (!isSignUpMode) Gold500 else Color.Transparent,
                                        contentColor = if (!isSignUpMode) Navy900 else Slate400
                                    ),
                                    shape = RoundedCornerShape(6.dp),
                                    elevation = null,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(36.dp)
                                ) {
                                    Text(
                                        text = if (isAr) "تسجيل الدخول" else "Sign In",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }

                                Button(
                                    onClick = { isSignUpMode = true },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSignUpMode) Gold500 else Color.Transparent,
                                        contentColor = if (isSignUpMode) Navy900 else Slate400
                                    ),
                                    shape = RoundedCornerShape(6.dp),
                                    elevation = null,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(36.dp)
                                ) {
                                    Text(
                                        text = if (isAr) "حساب جديد" else "Register",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Name field if registering
                            if (isSignUpMode) {
                                OutlinedTextField(
                                    value = nameInput,
                                    onValueChange = { nameInput = it },
                                    label = { Text(if (isAr) "الاسم واللقب الرسمي" else "Full Legal Name") },
                                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Cyan400) },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Cyan400,
                                        unfocusedBorderColor = Slate700,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedLabelColor = Cyan400,
                                        unfocusedLabelColor = Slate400
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("auth_name_field")
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }

                            // Email Field
                            OutlinedTextField(
                                value = emailInput,
                                onValueChange = { emailInput = it },
                                label = { Text(if (isAr) "البريد الإلكتروني المعتمد" else "Authorized Email Address") },
                                placeholder = { Text("user@eastwestglobal.sa", color = Slate400.copy(alpha = 0.5f)) },
                                leadingIcon = { Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = Cyan400) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Cyan400,
                                    unfocusedBorderColor = Slate700,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedLabelColor = Cyan400,
                                    unfocusedLabelColor = Slate400
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_email_field")
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Password Field
                            OutlinedTextField(
                                value = passwordInput,
                                onValueChange = { passwordInput = it },
                                label = { Text(if (isAr) "كلمة المرور المشفرة" else "Encrypted Password") },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Cyan400) },
                                trailingIcon = {
                                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                        Icon(
                                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Toggle Password Visibility",
                                            tint = Slate400
                                        )
                                    }
                                },
                                singleLine = true,
                                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        if (isSignUpMode) {
                                            onSignUpWithEmail(emailInput, passwordInput, nameInput)
                                        } else {
                                            onSignInWithEmail(emailInput, passwordInput)
                                        }
                                    }
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Cyan400,
                                    unfocusedBorderColor = Slate700,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedLabelColor = Cyan400,
                                    unfocusedLabelColor = Slate400
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_password_field")
                            )

                            // Password Strength Indicator (if entering password in sign-up)
                            if (isSignUpMode && passwordInput.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                val strengthScore = when {
                                    passwordInput.length >= 10 && passwordInput.any { it.isDigit() } && passwordInput.any { !it.isLetterOrDigit() } -> 3
                                    passwordInput.length >= 6 && passwordInput.any { it.isDigit() } -> 2
                                    else -> 1
                                }
                                val (strengthColor, strengthLabel) = when (strengthScore) {
                                    3 -> GreenSuccess to (if (isAr) "قوة سيادية فائقة (Sovereign Grade)" else "Sovereign Grade (Strong)")
                                    2 -> OrangeWarning to (if (isAr) "متوسطة ومقبولة" else "Moderate Strength")
                                    else -> RedDanger to (if (isAr) "ضعيفة - يرجى تقويتها" else "Weak Password")
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    LinearProgressIndicator(
                                        progress = { strengthScore / 3f },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(4.dp)
                                            .clip(RoundedCornerShape(2.dp)),
                                        color = strengthColor,
                                        trackColor = Slate700
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = strengthLabel,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = strengthColor,
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Options Row: Remember Me & Forgot Password
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { rememberMe = !rememberMe }
                                ) {
                                    Checkbox(
                                        checked = rememberMe,
                                        onCheckedChange = { rememberMe = it },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = Cyan400,
                                            checkmarkColor = Navy900,
                                            uncheckedColor = Slate400
                                        ),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isAr) "تذكر الجلسة" else "Remember Session",
                                        style = MaterialTheme.typography.bodySmall.copy(color = Slate300, fontSize = 11.sp)
                                    )
                                }

                                if (!isSignUpMode) {
                                    Text(
                                        text = if (isAr) "نسيت كلمة المرور؟" else "Forgot Password?",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Gold400,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        modifier = Modifier
                                            .clickable {
                                                forgotPasswordEmail = emailInput
                                                showForgotPasswordDialog = true
                                            }
                                            .testTag("forgot_password_btn")
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Submit Button
                            Button(
                                onClick = {
                                    if (isSignUpMode) {
                                        onSignUpWithEmail(emailInput, passwordInput, nameInput)
                                    } else {
                                        onSignInWithEmail(emailInput, passwordInput)
                                    }
                                },
                                enabled = !isLoading && emailInput.isNotBlank() && passwordInput.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Gold500,
                                    contentColor = Navy900
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("auth_submit_email_button")
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Navy900, strokeWidth = 2.dp)
                                } else {
                                    Icon(
                                        imageVector = if (isSignUpMode) Icons.Default.PersonAdd else Icons.Default.Login,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isSignUpMode) {
                                            if (isAr) "إنشاء حساب وتوثيق الصلاحية" else "Register & Authenticate"
                                        } else {
                                            if (isAr) "تسجيل الدخول للمنظومة" else "Sign In to Platform"
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }

                AuthModeTab.SOVEREIGN_ROOT -> {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Navy800,
                        border = BorderStroke(1.dp, Gold500),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Navy900,
                                border = BorderStroke(1.dp, Gold400),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.VpnKey, contentDescription = null, tint = Gold400, modifier = Modifier.size(24.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = if (isAr) "منظومة مفتاح السيادة والجذر (Root Key)" else "Sovereign Master Key & Root Access",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Gold300,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center
                                )
                            )

                            Text(
                                text = if (isAr)
                                    "صلاحية مطلقة مخصصة للأب ياسر (الرئيس الأعلى) لإدارة الطوارئ والحوكمة الشاملة"
                                else
                                    "Absolute governance override reserved for Father Yasser (Supreme Commander)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Slate300,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = sovereignCodeInput,
                                onValueChange = { sovereignCodeInput = it },
                                label = { Text(if (isAr) "أدخل كود السيادة السيبراني" else "Master Sovereignty Code") },
                                placeholder = { Text("1073781088@...", color = Slate400.copy(alpha = 0.5f)) },
                                leadingIcon = { Icon(Icons.Default.Security, contentDescription = null, tint = Gold400) },
                                trailingIcon = {
                                    TextButton(
                                        onClick = {
                                            sovereignCodeInput = "1073781088@0503026675#8054$8051%"
                                        }
                                    ) {
                                        Text(
                                            text = if (isAr) "تعبئة تلقائية" else "Auto-Fill",
                                            color = Cyan400,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                },
                                singleLine = true,
                                visualTransformation = PasswordVisualTransformation(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Gold400,
                                    unfocusedBorderColor = Slate700,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedLabelColor = Gold400,
                                    unfocusedLabelColor = Slate400
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("sovereign_root_input_field")
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Biometric Quick Scan Simulation Button
                            Button(
                                onClick = {
                                    isBiometricScanning = true
                                    onOpenSovereignDialog()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Gold500,
                                    contentColor = Navy900
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("sovereign_root_btn")
                            ) {
                                Icon(Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isAr) "المصادقة وتفعيل صلاحيات الجذر (Root)" else "Authenticate & Elevate to Root",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp
                                )
                            }
                        }
                    }
                }

                AuthModeTab.DEMO_PRESETS -> {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Navy800,
                        border = BorderStroke(1.dp, Slate700),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = if (isAr) "⚡ اختر رتبة معتمدة للدخول التجريبي الفوري:" else "⚡ Select a Verified Role for Fast Demo Login:",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Gold300,
                                    fontSize = 12.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Role 1: Supreme Commander (Father Yasser)
                            PresetRoleCard(
                                title = if (isAr) "ياسر الرشيدي (الرئيس التنفيذي للمجموعة - Group CEO)" else "Yasser Al-Rashidi (Group CEO & Founder)",
                                subtitle = if (isAr) "حوكمة الشركة والشركات التابعة، صلاحيات الجذر، كود السيادة" else "Full Control of Subsidiaries, Sovereign Override, Root CEO",
                                rankBadge = "👑 RANK 6 (CEO)",
                                badgeColor = Gold400,
                                isAr = isAr,
                                onClick = { onSelectPresetRole?.invoke(RoleRank.SUPREME_COMMANDER) },
                                testTag = "demo_role_yasser_btn"
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Role 2: CSO / General
                            PresetRoleCard(
                                title = if (isAr) "اللواء م. خالد العتيبي (مدير الأمن والرقابة)" else "Gen. Khalid Al-Otaibi (Chief Security Officer)",
                                subtitle = if (isAr) "إدارة الفرق، كاشف للأمان، مراجعة السجلات" else "Team Management, Kashef Security, Audit Logs",
                                rankBadge = "🛡️ RANK 5",
                                badgeColor = Cyan400,
                                isAr = isAr,
                                onClick = { onSelectPresetRole?.invoke(RoleRank.GENERAL) },
                                testTag = "demo_role_cso_btn"
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Role 3: Industrial Specialist
                            PresetRoleCard(
                                title = if (isAr) "م. فيصل الشهري (أخصائي روبوتات LK-W)" else "Eng. Faisal Al-Shehri (Robotics Specialist)",
                                subtitle = if (isAr) "إصدار أوامر التوريد، ربط خطوط SCADA" else "Issue Supply Orders, SCADA Integration",
                                rankBadge = "⚙️ RANK 3",
                                badgeColor = GreenSuccess,
                                isAr = isAr,
                                onClick = { onSelectPresetRole?.invoke(RoleRank.SPECIALIST) },
                                testTag = "demo_role_spec_btn"
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bottom Guest Sandbox Access Row
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Navy800.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, Slate700.copy(alpha = 0.6f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onContinueAsGuest() }
                    .testTag("auth_guest_access_btn")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        tint = Slate400,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAr) "المتابعة كزائر واستكشاف المنظومة (Guest Mode)" else "Continue & Explore as Guest (Observer)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Slate300,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Footer Security Statement
            Text(
                text = if (isAr)
                    "🔒 جميع البيانات مشفرة محلياً وسحابياً وفق بروتوكول Zero-Trust © 2026 شركة الشرق والغرب"
                else
                    "🔒 All communications encrypted locally and in cloud via Zero-Trust Protocol © 2026 East-West Global",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Slate400.copy(alpha = 0.7f),
                    fontSize = 9.5.sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Forgot Password Dialog
        if (showForgotPasswordDialog) {
            Dialog(onDismissRequest = { showForgotPasswordDialog = false }) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Navy800,
                    border = BorderStroke(1.5.dp, Gold400),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Navy900,
                            border = BorderStroke(1.dp, Gold400),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.LockReset, contentDescription = null, tint = Gold400, modifier = Modifier.size(24.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (isAr) "إعادة تعيين كلمة المرور" else "Reset Password",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Gold300,
                                fontSize = 16.sp
                            )
                        )

                        Text(
                            text = if (isAr)
                                "أدخل بريدك الإلكتروني المعتمد لاستلام رابط إعادة التعيين الآمن"
                            else
                                "Enter your registered email address to receive secure reset instructions",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Slate300,
                                fontSize = 11.5.sp,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = forgotPasswordEmail,
                            onValueChange = { forgotPasswordEmail = it },
                            label = { Text(if (isAr) "البريد الإلكتروني" else "Email Address") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Cyan400) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Cyan400,
                                unfocusedBorderColor = Slate700,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showForgotPasswordDialog = false },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate300),
                                border = BorderStroke(1.dp, Slate700),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(if (isAr) "إلغاء" else "Cancel", fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    onSendPasswordReset?.invoke(forgotPasswordEmail)
                                    showForgotPasswordDialog = false
                                },
                                enabled = forgotPasswordEmail.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Gold500,
                                    contentColor = Navy900
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(if (isAr) "إرسال الرابط" else "Send Link", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PresetRoleCard(
    title: String,
    subtitle: String,
    rankBadge: String,
    badgeColor: Color,
    isAr: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Navy900,
        border = BorderStroke(1.dp, Slate700),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 13.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Slate400,
                        fontSize = 11.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = badgeColor.copy(alpha = 0.15f),
                border = BorderStroke(0.5.dp, badgeColor)
            ) {
                Text(
                    text = rankBadge,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = badgeColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.5.sp
                    ),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
        }
    }
}
