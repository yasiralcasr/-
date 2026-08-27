package com.example.ui.components

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AppLanguage
import com.example.data.model.RoleRank
import com.example.data.model.UserAccount
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserPrivilegesDialog(
    user: UserAccount,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (UserAccount) -> Unit
) {
    val isAr = language == AppLanguage.ARABIC

    var fullName by remember(user) { mutableStateOf(user.fullName) }
    var departmentAr by remember(user) { mutableStateOf(user.departmentAr) }
    var departmentEn by remember(user) { mutableStateOf(user.departmentEn) }
    var assignedCode by remember(user) { mutableStateOf(user.assignedCode) }
    var selectedRank by remember(user) { mutableStateOf(user.roleRank) }

    var canRead by remember(user) { mutableStateOf(user.canRead) }
    var canWrite by remember(user) { mutableStateOf(user.canWrite) }
    var canExecute by remember(user) { mutableStateOf(user.canExecute) }
    var canAdminister by remember(user) { mutableStateOf(user.canAdminister) }
    var canPurge by remember(user) { mutableStateOf(user.canPurge) }
    var isMasterOverride by remember(user) { mutableStateOf(user.isMasterOverride) }

    val scrollState = rememberScrollState()

    fun applyRankDefaults(rank: RoleRank) {
        selectedRank = rank
        when (rank) {
            RoleRank.SUPREME_COMMANDER -> {
                canRead = true
                canWrite = true
                canExecute = true
                canAdminister = true
                canPurge = true
                isMasterOverride = true
            }
            RoleRank.GENERAL -> {
                canRead = true
                canWrite = true
                canExecute = true
                canAdminister = true
                canPurge = false
                isMasterOverride = false
            }
            RoleRank.SUPERVISOR -> {
                canRead = true
                canWrite = true
                canExecute = true
                canAdminister = false
                canPurge = false
                isMasterOverride = false
            }
            RoleRank.SPECIALIST -> {
                canRead = true
                canWrite = true
                canExecute = false
                canAdminister = false
                canPurge = false
                isMasterOverride = false
            }
            RoleRank.SOLDIER -> {
                canRead = true
                canWrite = false
                canExecute = false
                canAdminister = false
                canPurge = false
                isMasterOverride = false
            }
            RoleRank.OBSERVER -> {
                canRead = true
                canWrite = false
                canExecute = false
                canAdminister = false
                canPurge = false
                isMasterOverride = false
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Navy900,
            border = androidx.compose.foundation.BorderStroke(2.dp, Gold400),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .padding(vertical = 12.dp)
                .testTag("edit_user_privileges_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Gold500.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Root Governance",
                                tint = Gold400,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isAr) "تعديل رتبة وصلاحيات الحساب (Root Access)" else "Root User Privileges & Rank Governance",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Gold400
                                )
                            )
                            Text(
                                text = if (isAr) "منظومة الحوكمة والرئاسة التنفيذية (Executive CEO Governance)" else "Executive Group Governance - CEO Yasser Al-Rashidi",
                                style = MaterialTheme.typography.bodySmall.copy(color = Slate300, fontSize = 11.sp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("cancel_edit_user_btn")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate300)
                    }
                }

                HorizontalDivider(
                    color = Slate700,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Current User Identifier Box
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Navy800,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Cyan500.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (isAr) "معرف الحساب المستهدف: ${user.id}" else "Target User ID: ${user.id}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Cyan300,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "@${user.username}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Cyan600.copy(alpha = 0.25f)
                            ) {
                                Text(
                                    text = if (isAr) "تاريخ الإنشاء: ${user.createdAt}" else "Created: ${user.createdAt}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Cyan300, fontSize = 10.sp),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    // Editable Personal & Dept info
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text(if (isAr) "الاسم الكامل للمستخدم" else "Full Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Gold400) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Gold400,
                            unfocusedBorderColor = Slate700,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("edit_user_fullname_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = departmentAr,
                            onValueChange = { departmentAr = it },
                            label = { Text(if (isAr) "القسم / الإدارة (عربي)" else "Department (Arabic)") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Cyan400,
                                unfocusedBorderColor = Slate700,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("edit_user_dept_ar_input")
                        )

                        OutlinedTextField(
                            value = assignedCode,
                            onValueChange = { assignedCode = it },
                            label = { Text(if (isAr) "كود الأمان المعين" else "Assigned Security Code") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Cyan400,
                                unfocusedBorderColor = Slate700,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("edit_user_assigned_code_input")
                        )
                    }

                    // Rank Selection Section
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isAr) "تعيين الرتبة والمنصب القيادي:" else "Assign Rank & Command Level:",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Gold400
                                )
                            )

                            TextButton(
                                onClick = { applyRankDefaults(selectedRank) },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.testTag("apply_rank_defaults_btn")
                            ) {
                                Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = Cyan400, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isAr) "تطبيق الصلاحيات الافتراضية للرتبة" else "Apply Rank Defaults",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Cyan400, fontWeight = FontWeight.Bold)
                                )
                            }
                        }

                        // Grid of 6 RoleRanks
                        RoleRank.values().forEach { rank ->
                            val isSelected = selectedRank == rank
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) Navy700 else Navy800,
                                border = androidx.compose.foundation.BorderStroke(
                                    if (isSelected) 1.5.dp else 1.dp,
                                    if (isSelected) Gold400 else Slate700
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { applyRankDefaults(rank) }
                                    .testTag("rank_option_${rank.name}")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { applyRankDefaults(rank) },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = Gold400,
                                            unselectedColor = Slate300
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = rank.badgeIcon, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isAr) rank.titleAr else rank.titleEn,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Gold400 else Color.White
                                            )
                                        )
                                        Text(
                                            text = if (isAr) rank.descriptionAr else rank.descriptionEn,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Slate300,
                                                fontSize = 10.5.sp
                                            ),
                                            maxLines = 2
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (isSelected) Gold500.copy(alpha = 0.2f) else Slate800
                                    ) {
                                        Text(
                                            text = "Rank ${rank.level}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (isSelected) Gold400 else Slate300,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            ),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Granular Permissions Switches
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Navy800)
                            .border(1.dp, Slate700, RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = if (isAr) "تخصيص الصلاحيات التفصيلية (Granular Permissions):" else "Granular Access Permissions:",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Cyan400
                            )
                        )

                        PermissionSwitchItem(
                            title = if (isAr) "صلاحية القراءة والاستعراض (Can Read)" else "Read & View Systems (Can Read)",
                            description = if (isAr) "تصفح واستعراض المنظومات وعقود التوريد وسجلات العمليات." else "Browse enterprise systems, orders, and audit logs.",
                            checked = canRead,
                            icon = Icons.Default.Visibility,
                            accentColor = Cyan400,
                            testTag = "switch_can_read",
                            onCheckedChange = { canRead = it }
                        )

                        HorizontalDivider(color = Slate700)

                        PermissionSwitchItem(
                            title = if (isAr) "صلاحية الكتابة والإنشاء (Can Write)" else "Write & Create Systems (Can Write)",
                            description = if (isAr) "إنشاء برامج رقمية جديدة وإصدار مفاتيح الربط وتوليد الحسابات." else "Create digital programs, generate system keys & credentials.",
                            checked = canWrite,
                            icon = Icons.Default.Edit,
                            accentColor = GreenSuccess,
                            testTag = "switch_can_write",
                            onCheckedChange = { canWrite = it }
                        )

                        HorizontalDivider(color = Slate700)

                        PermissionSwitchItem(
                            title = if (isAr) "صلاحية التنفيذ والأتمتة (Can Execute)" else "Execute Operations (Can Execute)",
                            description = if (isAr) "تشغيل خطوط الإنتاج، إدارة معدات LK-W وتحديث مسارات العمل." else "Trigger production pipelines, control LK-W hardware & orders.",
                            checked = canExecute,
                            icon = Icons.Default.PlayArrow,
                            accentColor = Cyan500,
                            testTag = "switch_can_execute",
                            onCheckedChange = { canExecute = it }
                        )

                        HorizontalDivider(color = Slate700)

                        PermissionSwitchItem(
                            title = if (isAr) "صلاحية الإدارة والحوكمة (Can Administer)" else "Administration Authority (Can Administer)",
                            description = if (isAr) "تعديل حسابات المستخدمين، إدارة الرتب، واعتماد البرامج الرقمية." else "Manage user accounts, assign roles, and certify programs.",
                            checked = canAdminister,
                            icon = Icons.Default.AdminPanelSettings,
                            accentColor = Gold400,
                            testTag = "switch_can_administer",
                            onCheckedChange = { canAdminister = it }
                        )

                        HorizontalDivider(color = Slate700)

                        PermissionSwitchItem(
                            title = if (isAr) "صلاحية الحذف والتطهير الجذري (Can Purge)" else "Purge & Destruction Access (Can Purge)",
                            description = if (isAr) "إلغاء وحذف الحسابات، وتنفيذ أمر تدمير وإعادة ضبط السجلات بالكامل." else "Revoke user accounts and trigger comprehensive system purge.",
                            checked = canPurge,
                            icon = Icons.Default.DeleteForever,
                            accentColor = RedDanger,
                            testTag = "switch_can_purge",
                            onCheckedChange = { canPurge = it }
                        )

                        HorizontalDivider(color = Slate700)

                        PermissionSwitchItem(
                            title = if (isAr) "كود السيادة والصلاحية المطلقة (Master Override)" else "Sovereign Master Override (Root Level)",
                            description = if (isAr) "امتلاك كود السيادة وتجاوز كافة قيود الأمان وإلغاء القيود الأمنية." else "Bypasses all authorization gates with unconstrained root access.",
                            checked = isMasterOverride,
                            icon = Icons.Default.Shield,
                            accentColor = Gold400,
                            testTag = "switch_master_override",
                            onCheckedChange = { isMasterOverride = it }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate300),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate700),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text(text = if (isAr) "إلغاء" else "Cancel", fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            val updated = user.copy(
                                fullName = fullName.ifBlank { user.fullName },
                                departmentAr = departmentAr.ifBlank { user.departmentAr },
                                departmentEn = departmentEn.ifBlank { user.departmentEn },
                                assignedCode = assignedCode.ifBlank { user.assignedCode },
                                roleRank = selectedRank,
                                canRead = canRead,
                                canWrite = canWrite,
                                canExecute = canExecute,
                                canAdminister = canAdminister,
                                canPurge = canPurge,
                                isMasterOverride = isMasterOverride
                            )
                            onSave(updated)
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Gold500,
                            contentColor = Navy900
                        ),
                        modifier = Modifier
                            .weight(1.6f)
                            .height(48.dp)
                            .testTag("save_user_privileges_btn")
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAr) "اعتماد وحفظ الصلاحيات" else "Certify & Save Privileges",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionSwitchItem(
    title: String,
    description: String,
    checked: Boolean,
    icon: ImageVector,
    accentColor: Color,
    testTag: String,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (checked) accentColor else Slate400,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (checked) Color.White else Slate300
                    )
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Slate400,
                        fontSize = 10.sp
                    )
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Navy900,
                checkedTrackColor = accentColor,
                uncheckedThumbColor = Slate400,
                uncheckedTrackColor = Slate800
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}
