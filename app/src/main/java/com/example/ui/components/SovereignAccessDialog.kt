package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AppLanguage
import com.example.data.model.RoleRank
import com.example.data.model.UserAccount
import com.example.ui.theme.*

const val SOVEREIGN_CODE_VALUE = "1073781088@0503026675#8054$8051%"

/**
 * SovereignAccessDialog
 * Allows users to enter the Sovereign Code (الصحيك كود).
 * When matched with '1073781088@0503026675#8054$8051%', grants the user Root Access (Rank 6: Supreme Commander)
 * and enables all administrative capabilities within the enterprise dashboard.
 */
@Composable
fun SovereignAccessDialog(
    sovereignCodeInput: String,
    errorMessage: String? = null,
    language: AppLanguage = AppLanguage.ARABIC,
    onInputChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    val isAr = language == AppLanguage.ARABIC

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Navy800,
            border = androidx.compose.foundation.BorderStroke(2.dp, Gold500),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("sovereign_access_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Sovereign Crown Emblem
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(Gold500.copy(alpha = 0.18f))
                        .border(1.5.dp, Gold400, RoundedCornerShape(28.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "👑",
                        fontSize = 28.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isAr) "كود السيادة والصلاحية المطلقة" else "Sovereign Access Verification",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Gold400,
                        fontSize = 17.sp
                    )
                )

                Text(
                    text = if (isAr) "الرتبة ٦: ياسر الرشيدي (الرئيس التنفيذي ومؤسس المجموعة - Group CEO)" else "Rank 6: Yasser Al-Rashidi (Group CEO & Founder)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Cyan400,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Navy900,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate700),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isAr)
                            "إدخال كود السيادة يمنحك صلاحية الحساب الجذري (Root Rank 6)، لتفعيل جميع الإجراءات الإدارية، إدارة المستخدمين، تعديل الطلبات، وتخطي أي حظر."
                        else
                            "Entering the Sovereign Code grants Root Access (Rank 6), unlocking all dashboard administrative actions, user management, and system operations.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Slate200,
                            fontSize = 11.5.sp,
                            lineHeight = 17.sp
                        ),
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = sovereignCodeInput,
                    onValueChange = onInputChanged,
                    label = { Text(if (isAr) "أدخل كود السيادة المعتمد" else "Enter Sovereign Code") },
                    placeholder = { Text("1073781088@0503026675#8054$8051%") },
                    singleLine = true,
                    isError = errorMessage != null,
                    supportingText = {
                        if (errorMessage != null) {
                            Text(
                                text = errorMessage,
                                color = RedDanger,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        } else {
                            Text(
                                text = if (isAr) "كود الصلاحية العليا لشركة الشرق والغرب" else "East-West Master Key required",
                                color = Slate300,
                                fontSize = 11.sp
                            )
                        }
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = { onInputChanged(SOVEREIGN_CODE_VALUE) },
                            modifier = Modifier.testTag("autofill_sovereign_code_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoFixHigh,
                                contentDescription = "Autofill Sovereign Code",
                                tint = Gold400
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Gold400,
                        unfocusedBorderColor = Slate700,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sovereign_code_input")
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("cancel_sovereign_access_btn")
                    ) {
                        Text(if (isAr) "إلغاء" else "Cancel", color = Slate200)
                    }

                    Button(
                        onClick = onSubmit,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Gold500,
                            contentColor = Navy900
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("submit_sovereign_code_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.VpnKey,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAr) "تفعيل رتبة السيادة" else "Grant Root Access",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Self-contained overload of SovereignAccessDialog for direct composable integration
 */
@Composable
fun SovereignAccessDialog(
    language: AppLanguage = AppLanguage.ARABIC,
    onGrantedRootAccess: (UserAccount) -> Unit,
    onDismiss: () -> Unit
) {
    var codeInput by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }
    val isAr = language == AppLanguage.ARABIC

    SovereignAccessDialog(
        sovereignCodeInput = codeInput,
        errorMessage = errorText,
        language = language,
        onInputChanged = {
            codeInput = it
            errorText = null
        },
        onSubmit = {
            if (codeInput.trim() == SOVEREIGN_CODE_VALUE) {
                val supremeRootUser = UserAccount(
                    id = "usr-supreme-root",
                    username = "yasser_alrashidi_ceo_root",
                    fullName = "ياسر الرشيدي (Group CEO • الرئيس التنفيذي)",
                    roleRank = RoleRank.SUPREME_COMMANDER,
                    departmentAr = "الرئاسة التنفيذية وحوكمة المجموعة والشركات التابعة",
                    departmentEn = "Executive Leadership & Subsidiaries Governance",
                    assignedCode = SOVEREIGN_CODE_VALUE,
                    canRead = true,
                    canWrite = true,
                    canExecute = true,
                    canAdminister = true,
                    canPurge = true,
                    isMasterOverride = true
                )
                onGrantedRootAccess(supremeRootUser)
            } else {
                errorText = if (isAr) "❌ كود السيادة غير صحيح. يرجى التحقق من المفتاح المعتمد."
                else "❌ Invalid Sovereign Code. Please check authorized key."
            }
        },
        onDismiss = onDismiss
    )
}
