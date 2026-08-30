package com.example.ui.screens

import android.app.Activity
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AppLanguage
import com.example.data.model.RoleRank
import com.example.ui.theme.*

@Composable
fun LoginScreen(
    language: AppLanguage,
    isLoading: Boolean,
    errorMessage: String?,
    onSignInWithEmail: (email: String, pass: String) -> Unit,
    onSignInWithGoogle: ((Activity) -> Unit)? = null,
    onSignUpWithEmail: ((email: String, pass: String, name: String) -> Unit)? = null,
    onContinueAsGuest: (() -> Unit)? = null,
    onOpenSovereignDialog: (() -> Unit)? = null,
    onSendPasswordReset: ((String) -> Unit)? = null,
    onSelectPresetRole: ((RoleRank) -> Unit)? = null,
    onToggleLanguage: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isAr = language == AppLanguage.ARABIC

    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Navy900,
                        Color(0xFF0A192F),
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
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Main Login Container
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Navy800.copy(alpha = 0.95f),
                border = BorderStroke(1.dp, Gold500.copy(alpha = 0.5f)),
                shadowElevation = 10.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo / Brand Icon
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(Gold400.copy(alpha = 0.25f), Color.Transparent)
                                )
                            )
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Navy900,
                            border = BorderStroke(2.dp, Gold400),
                            modifier = Modifier.size(62.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                contentDescription = "Logo",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // "أهلاً بكم" Welcome Header
                    Text(
                        text = if (isAr) "أهلاً بكم" else "Welcome",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Gold300,
                            fontSize = 26.sp
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Company Name
                    Text(
                        text = if (isAr) "شركة الشرق والغرب العالمية" else "East & West Global Corporation",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Slate300,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Error Message
                    AnimatedVisibility(
                        visible = errorMessage != null,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        if (errorMessage != null) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = RedDanger.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, RedDanger.copy(alpha = 0.7f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                                    .testTag("auth_error_banner")
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ErrorOutline,
                                        contentDescription = null,
                                        tint = RedDanger,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = errorMessage,
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
                                    )
                                }
                            }
                        }
                    }

                    // Email Field
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text(if (isAr) "البريد الإلكتروني" else "Email Address") },
                        placeholder = { Text(if (isAr) "name@example.com" else "name@example.com", color = Slate400.copy(alpha = 0.5f)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = Gold400
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Gold400,
                            unfocusedBorderColor = Slate700,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = Gold400,
                            unfocusedLabelColor = Slate400,
                            cursorColor = Gold400
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("email_input_field")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Field
                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        label = { Text(if (isAr) "الرقم السري" else "Password") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = Gold400
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle Password",
                                    tint = Slate400
                                )
                            }
                        },
                        singleLine = true,
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (emailInput.isNotBlank() && passwordInput.isNotBlank()) {
                                    onSignInWithEmail(emailInput.trim(), passwordInput)
                                }
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Gold400,
                            unfocusedBorderColor = Slate700,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = Gold400,
                            unfocusedLabelColor = Slate400,
                            cursorColor = Gold400
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("password_input_field")
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Login Button (تسجيل الدخول)
                    Button(
                        onClick = {
                            onSignInWithEmail(emailInput.trim(), passwordInput)
                        },
                        enabled = !isLoading && emailInput.isNotBlank() && passwordInput.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Gold500,
                            contentColor = Navy900,
                            disabledContainerColor = Slate700,
                            disabledContentColor = Slate400
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("login_submit_button")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Navy900,
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Login,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isAr) "تسجيل الدخول" else "Sign In",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Copyright / Rights Footer (الحقوق)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .testTag("copyright_footer")
            ) {
                Text(
                    text = if (isAr) "جميع الحقوق محفوظة © شركة الشرق والغرب العالمية" else "All Rights Reserved © East & West Global Corporation",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Slate400,
                        fontSize = 11.5.sp,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }
    }
}
