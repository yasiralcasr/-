package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun CredentialsDialog(
    program: EnterpriseProgram,
    language: AppLanguage,
    onDismiss: () -> Unit
) {
    val isAr = language == AppLanguage.ARABIC
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Navy800,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Gold500),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("credentials_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header badge
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(Gold500.copy(alpha = 0.15f))
                        .border(1.5.dp, Gold500, RoundedCornerShape(28.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Success",
                        tint = Gold400,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isAr) "تم الانتهاء بنجاح وإصدار بيانات الدخول" else "Program Creation Certified",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Gold400
                    ),
                    fontSize = 17.sp
                )

                Text(
                    text = if (isAr) program.titleAr else program.titleEn,
                    style = MaterialTheme.typography.bodySmall.copy(color = Slate200),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Credentials Box
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Navy900,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate700),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CredentialRow(
                            label = if (isAr) "اسم المستخدم:" else "Username:",
                            value = program.generatedUsername,
                            onCopy = { copyToClipboard(context, program.generatedUsername) }
                        )

                        HorizontalDivider(color = Slate800)

                        CredentialRow(
                            label = if (isAr) "الرقم السري الآمن:" else "Password:",
                            value = program.generatedPassword,
                            onCopy = { copyToClipboard(context, program.generatedPassword) }
                        )

                        HorizontalDivider(color = Slate800)

                        CredentialRow(
                            label = if (isAr) "رمز المنظومة المشفر:" else "System Key:",
                            value = program.systemKey,
                            onCopy = { copyToClipboard(context, program.systemKey) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Certificate footer
                Text(
                    text = if (isAr)
                        "تم توثيق المنظومة وربطها بخوادم شركة الشرق والغرب العالمية تحت بروتوكول الأمان الشامل."
                    else
                        "Certified & Linked to East-West Global Network Infrastructure.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Slate300,
                        fontSize = 11.sp
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Gold500,
                        contentColor = Navy900
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("close_credentials_button")
                ) {
                    Text(
                        text = if (isAr) "حفظ وإغلاق" else "Save & Close",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun CredentialRow(
    label: String,
    value: String,
    onCopy: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(color = Slate300, fontSize = 11.sp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Cyan400,
                    fontFamily = FontFamily.Monospace
                )
            )
        }

        IconButton(
            onClick = onCopy,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy",
                tint = Gold400,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun MasterKeyDialog(
    masterKeyInput: String,
    masterKeyError: String?,
    language: AppLanguage,
    onInputChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onDismiss: () -> Unit
) {
    SovereignAccessDialog(
        sovereignCodeInput = masterKeyInput,
        errorMessage = masterKeyError,
        language = language,
        onInputChanged = onInputChanged,
        onSubmit = onSubmit,
        onDismiss = onDismiss
    )
}

@Composable
fun OrderDialog(
    product: IndustrialProduct,
    language: AppLanguage,
    onSubmitOrder: (clientName: String, sector: SectorType, qty: Int, priority: OrderPriority, location: String, email: String, phone: String, notes: String) -> Unit,
    onDismiss: () -> Unit
) {
    val isAr = language == AppLanguage.ARABIC
    var clientName by remember { mutableStateOf("") }
    var selectedSector by remember { mutableStateOf(SectorType.INDUSTRIAL) }
    var quantityText by remember { mutableStateOf("1") }
    var selectedPriority by remember { mutableStateOf(OrderPriority.HIGH) }
    var location by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Navy800,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Cyan400),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(8.dp)
                .testTag("industrial_order_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Order",
                        tint = Cyan400,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAr) "طلب توريد معدات صناعية LK-W" else "LK-W Industrial Supply Request",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Cyan400
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Navy900,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = if (isAr) product.nameAr else product.nameEn,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Gold400
                            )
                        )
                        Text(
                            text = "${product.modelCode} | ${product.certStandards}",
                            style = MaterialTheme.typography.labelSmall.copy(color = Slate300)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = clientName,
                    onValueChange = { clientName = it },
                    label = { Text(if (isAr) "اسم الجهة / الشركة الطالبة" else "Client / Enterprise Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { quantityText = it.filter { ch -> ch.isDigit() } },
                        label = { Text(if (isAr) "الكمية" else "Quantity") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text(if (isAr) "رقم التواصل" else "Phone") },
                        singleLine = true,
                        modifier = Modifier.weight(1.5f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(if (isAr) "البريد الإلكتروني للجهة" else "Official Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text(if (isAr) "موقع التسليم / المستودع" else "Delivery Location / Port") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(if (isAr) "ملاحظات فنية أو متطلبات خاصة" else "Technical Notes / Specs") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (isAr) "إلغاء" else "Cancel", color = Slate200)
                    }

                    Button(
                        onClick = {
                            val qty = quantityText.toIntOrNull() ?: 1
                            onSubmitOrder(
                                clientName,
                                selectedSector,
                                qty,
                                selectedPriority,
                                location,
                                email,
                                phone,
                                notes
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Cyan500,
                            contentColor = Navy900
                        ),
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("submit_order_button")
                    ) {
                        Text(if (isAr) "تأكيد وإرسال الطلب" else "Submit Order", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun PurgeConfirmDialog(
    language: AppLanguage,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val isAr = language == AppLanguage.ARABIC

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Alert",
                    tint = RedDanger
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isAr) "أمر التدمير وإعادة التهيئة الشاملة" else "Root System Purge & Reset",
                    color = RedDanger,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Text(
                text = if (isAr)
                    "تحذير سيادي: هذا الأمر من صلاحيات كود السيادة فقط، ويقوم بمسح كافة السجلات وإعادة ضبط قواعد البيانات للشركة كأنها لم تُنشأ من قبل. هل تؤكد التنفيذ؟"
                else
                    "Critical Directive: This command uses root authority to purge and re-initialize all enterprise database records. Confirm execution?",
                color = Slate200
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = RedDanger,
                    contentColor = Color.White
                ),
                modifier = Modifier.testTag("confirm_purge_button")
            ) {
                Text(if (isAr) "تأكيد التنفيذ الفوري" else "Execute Purge")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(if (isAr) "تراجع" else "Cancel", color = Slate300)
            }
        },
        containerColor = Navy800
    )
}

@Composable
fun UserSwitchDialog(
    usersList: List<UserAccount>,
    activeUser: UserAccount,
    language: AppLanguage,
    onSelectUser: (UserAccount) -> Unit,
    onDismiss: () -> Unit
) {
    val isAr = language == AppLanguage.ARABIC

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Navy800,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, Gold500),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
                .padding(8.dp)
                .testTag("user_switch_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isAr) "اختر الحساب والرتبة للتجربة" else "Select User / Rank Perspective",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Gold400
                        )
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate300)
                    }
                }

                Text(
                    text = if (isAr)
                        "يمكنك اختبار تجربة النظام بحساب مشاهد (قراءة فقط) أو جندي أو مشرف أو الرئيس الأعلى."
                    else
                        "Test system perspective as Observer (read-only), Operator, Supervisor, or Supreme Admin.",
                    style = MaterialTheme.typography.bodySmall.copy(color = Slate300, fontSize = 11.sp),
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    usersList.forEach { user ->
                        val isSelected = user.id == activeUser.id
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) Navy700 else Navy900,
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 1.5.dp else 1.dp,
                                if (isSelected) Gold400 else Slate800
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectUser(user)
                                    onDismiss()
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = user.roleRank.badgeIcon,
                                    fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = user.fullName,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    )
                                    Text(
                                        text = if (isAr) user.roleRank.titleAr else user.roleRank.titleEn,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (user.roleRank.level == 6) Gold300 else Cyan400
                                        )
                                    )
                                    Text(
                                        text = if (isAr) user.departmentAr else user.departmentEn,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Slate300,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Active",
                                        tint = Gold400,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Copied Text", text)
    clipboard.setPrimaryClip(clip)
}
