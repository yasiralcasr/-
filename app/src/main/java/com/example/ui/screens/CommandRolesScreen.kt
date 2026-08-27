package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.firestore.FirestoreAuditLog
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun CommandRolesScreen(
    usersList: List<UserAccount>,
    auditLogs: List<AuditLogEntry>,
    firestoreAuditLogs: List<FirestoreAuditLog> = emptyList(),
    isFirestoreAuditSyncing: Boolean = false,
    activeUser: UserAccount,
    isMasterUnlocked: Boolean,
    masterKeyInput: String,
    masterKeyError: String?,
    language: AppLanguage,
    firebaseUserEmail: String? = null,
    searchUserQuery: String = "",
    onSearchUserQueryChanged: (String) -> Unit = {},
    selectedUserRankFilter: RoleRank? = null,
    onSelectedUserRankFilterChanged: (RoleRank?) -> Unit = {},
    onOpenLoginScreen: () -> Unit = {},
    onSignOut: () -> Unit = {},
    onMasterKeyInputChanged: (String) -> Unit,
    onSubmitMasterKey: () -> Unit,
    onSwitchUser: (UserAccount) -> Unit,
    onEditUser: (UserAccount) -> Unit = {},
    onCreateUser: (username: String, fullName: String, role: RoleRank, deptAr: String, deptEn: String) -> Unit,
    onDeleteUser: (UserAccount) -> Unit,
    onShowPurgeDialog: () -> Unit,
    onSyncFirestoreAudit: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isAr = language == AppLanguage.ARABIC

    var showCreateUserSection by remember { mutableStateOf(false) }
    var newUsername by remember { mutableStateOf("") }
    var newFullName by remember { mutableStateOf("") }
    var selectedRank by remember { mutableStateOf(RoleRank.OBSERVER) }
    var newDeptAr by remember { mutableStateOf("") }

    val hasRootOrAdmin = isMasterUnlocked || activeUser.canAdminister || activeUser.roleRank == RoleRank.SUPREME_COMMANDER

    // Filter users list based on search and rank
    val filteredUsers = remember(usersList, searchUserQuery, selectedUserRankFilter) {
        usersList.filter { user ->
            val matchesRank = selectedUserRankFilter == null || user.roleRank == selectedUserRankFilter
            val matchesQuery = if (searchUserQuery.isBlank()) {
                true
            } else {
                user.fullName.contains(searchUserQuery, ignoreCase = true) ||
                        user.username.contains(searchUserQuery, ignoreCase = true) ||
                        user.departmentAr.contains(searchUserQuery, ignoreCase = true) ||
                        user.departmentEn.contains(searchUserQuery, ignoreCase = true) ||
                        user.assignedCode.contains(searchUserQuery, ignoreCase = true)
            }
            matchesRank && matchesQuery
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Father Yasser Founder & Supreme Leader Hero Card
        item {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Navy800,
                border = androidx.compose.foundation.BorderStroke(2.dp, Gold500),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("father_yasser_leader_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Portrait with Glowing Gold Border
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .border(2.5.dp, Gold400, CircleShape)
                                .background(Navy900)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_father_yasser_avatar),
                                contentDescription = "الأستاذ ياسر الرشيدي - الرئيس التنفيذي والمؤسس",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isAr) "ياسر الرشيدي (CEO)" else "Yasser Al-Rashidi (CEO)",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Gold400,
                                        fontSize = 19.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "👑", fontSize = 18.sp)
                            }

                            Text(
                                text = if (isAr) "الرئيس التنفيذي للشركة والشركات التابعة ومؤسس المجموعة"
                                else "Group CEO & President of Parent & Subsidiary Companies",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            )

                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Gold500.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(0.5.dp, Gold400),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Text(
                                    text = if (isAr) "الرتبة ٦: الصلاحية المطلقة والسيادة الجذرية (Group CEO / Root)"
                                    else "Rank 6: Group CEO, Chairman & Supreme Authority",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Gold300,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.5.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Navy900,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate700),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isAr)
                                "منظومة القيادة التنفيذية العليا: تطبيقك المخصص لإدارة المنظومات الذكية، حوكمة الشركات التابعة، سلاسل التوريد الصناعي LK-W، وإدارة الصلاحيات تحت إشراف الرئيس التنفيذي."
                            else
                                "Executive Command Suite: Direct governance and orchestration of subsidiaries, intelligent systems, LK-W industrial hardware, and sovereign authorizations under the Group CEO.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Slate200,
                                fontSize = 11.5.sp,
                                lineHeight = 17.sp
                            ),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }

        // Master Key Override Console Card
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Navy800,
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    if (isMasterUnlocked) Gold400 else Slate700
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Gold500.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VpnKey,
                                    contentDescription = "Master Key",
                                    tint = Gold400
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isAr) "كود الصلاحية المطلقة والسيادة" else "Root Sovereignty Key Console",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Gold400
                                    )
                                )
                                Text(
                                    text = if (isAr) "الصحيك كود - القيادة العليا لشركة الشرق والغرب" else "Master Command Key for Root Governance",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Slate300, fontSize = 11.sp)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isMasterUnlocked) GreenSuccess.copy(alpha = 0.2f) else Slate700
                        ) {
                            Text(
                                text = if (isMasterUnlocked) {
                                    if (isAr) "👑 مفعل: الصلاحية المطلقة" else "👑 ROOT ACTIVE"
                                } else {
                                    if (isAr) "مغلق" else "LOCKED"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isMasterUnlocked) Gold300 else Slate300,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (isAr)
                            "عند إدخال هذا الكود في أي نافذة، تنحني كافة القيود وتُمنح الصلاحية المطلقة للرئيس الأعلى للتحكم الكامل، تدمير السجلات، وتحديد رؤية الحسابات."
                        else
                            "Entering the Master Key unlocks unconditional supreme admin privileges, allowing destructive resets and full role delegation.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Slate200,
                            lineHeight = 18.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = masterKeyInput,
                        onValueChange = onMasterKeyInputChanged,
                        label = { Text(if (isAr) "أدخل كود السيادة المعتمد" else "Enter Master Code") },
                        placeholder = { Text("1073781088@0503026675#8054$8051%") },
                        singleLine = true,
                        isError = masterKeyError != null,
                        supportingText = {
                            if (masterKeyError != null) {
                                Text(masterKeyError, color = RedDanger)
                            }
                        },
                        trailingIcon = {
                            IconButton(onClick = {
                                onMasterKeyInputChanged("1073781088@0503026675#8054$8051%")
                            }) {
                                Icon(
                                    imageVector = Icons.Default.AutoFixHigh,
                                    contentDescription = "Autofill Master Key",
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
                            .testTag("command_master_key_field")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onSubmitMasterKey,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Gold500,
                            contentColor = Navy900
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("command_activate_key_btn")
                    ) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAr) "تأكيد وتفعيل كود الصلاحية المطلقة" else "Authenticate Root Sovereignty",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Firebase Auth & Credential Manager Management Card
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Navy800,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Cyan500),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_credentials_status_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Cyan500.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudSync,
                                    contentDescription = "Firebase Auth",
                                    tint = Cyan400
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isAr) "مصادقة Firebase & Credential Manager" else "Firebase Auth & Credential Manager",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Cyan300
                                    )
                                )
                                Text(
                                    text = if (isAr) "المصادقة السحابية المعتمدة لحماية المنظومة" else "Cloud Identity & Access Management",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Slate300, fontSize = 11.sp)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (firebaseUserEmail != null) GreenSuccess.copy(alpha = 0.2f) else Slate700
                        ) {
                            Text(
                                text = if (firebaseUserEmail != null) {
                                    if (isAr) "مُوثق سحابياً" else "AUTHENTICATED"
                                } else {
                                    if (isAr) "جلسة محلية / ضيف" else "LOCAL / GUEST"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (firebaseUserEmail != null) GreenSuccess else Slate300,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (firebaseUserEmail != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Gold400, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAr) "الحساب السحابي النشط: $firebaseUserEmail" else "Active Cloud Account: $firebaseUserEmail",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.White, fontWeight = FontWeight.SemiBold)
                            )
                        }
                    } else {
                        Text(
                            text = if (isAr)
                                "يتيح نظام Credential Manager تسجيل الدخول بنقرة واحدة باستخدام حساب Google أو البريد الإلكتروني للوصول إلى صلاحيات المنظومة وإدارتها."
                            else
                                "Credential Manager allows seamless 1-tap Google Sign-In and email authentication to securely manage organizational roles.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate200, fontSize = 11.5.sp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onOpenLoginScreen,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Cyan500,
                                contentColor = Navy900
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_open_login_screen")
                        ) {
                            Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAr) "نافذة تسجيل الدخول (Auth)" else "Sign In / Register",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        if (firebaseUserEmail != null || activeUser.id != "usr-guest") {
                            OutlinedButton(
                                onClick = onSignOut,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = RedDanger),
                                border = androidx.compose.foundation.BorderStroke(1.dp, RedDanger),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("btn_auth_sign_out")
                            ) {
                                Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isAr) "تسجيل الخروج" else "Sign Out",
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Rank Hierarchy Visualizer
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Navy800),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate700)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = if (isAr) "الهيكل الهرمي للرتب والصلاحيات" else "Role & Rank Permission Hierarchy",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Cyan400
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    RoleRank.values().forEach { rank ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = rank.badgeIcon, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isAr) rank.titleAr else rank.titleEn,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (rank.level == 6) Gold400 else Color.White
                                    )
                                )
                                Text(
                                    text = if (isAr) rank.descriptionAr else rank.descriptionEn,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Slate300,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                        if (rank != RoleRank.OBSERVER) {
                            HorizontalDivider(color = Slate800, modifier = Modifier.padding(vertical = 2.dp))
                        }
                    }
                }
            }
        }

        // Accounts Management Header & Provisioning with Root Admin Controls
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Main Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.SupervisorAccount,
                                contentDescription = null,
                                tint = Gold400,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAr) "إدارة وتعديل صلاحيات المستخدمين (${filteredUsers.size}/${usersList.size})" else "User Governance & Root Access (${filteredUsers.size}/${usersList.size})",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Gold400
                                )
                            )
                        }
                        Text(
                            text = if (isAr) {
                                if (hasRootOrAdmin) "صلاحية الجذر (Root Access) مفعلة - يمكنك تعديل رتب وصلاحيات أي مستخدم"
                                else "صلاحية التعديل مقفلة - أدخل كود السيادة لتفعيل صلاحية الجذر"
                            } else {
                                if (hasRootOrAdmin) "Root Access Active - You can modify any user's rank and privileges"
                                else "Editing Locked - Enter Master Key for Root Access"
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (hasRootOrAdmin) GreenSuccess else Slate400,
                                fontSize = 11.sp
                            )
                        )
                    }

                    FilledTonalButton(
                        onClick = { showCreateUserSection = !showCreateUserSection },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Cyan500,
                            contentColor = Navy900
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("toggle_create_user_btn")
                    ) {
                        Icon(
                            imageVector = if (showCreateUserSection) Icons.Default.Close else Icons.Default.PersonAdd,
                            contentDescription = "Add User",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (showCreateUserSection) {
                                if (isAr) "إغلاق" else "Close"
                            } else {
                                if (isAr) "إصدار حساب جديد" else "Create User"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                // Search Bar
                OutlinedTextField(
                    value = searchUserQuery,
                    onValueChange = onSearchUserQueryChanged,
                    placeholder = {
                        Text(
                            text = if (isAr) "بحث بالاسم، المعرف، القسم أو الكود..." else "Search by name, username, dept or code...",
                            color = Slate400,
                            fontSize = 12.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Cyan400,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchUserQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchUserQueryChanged("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = Slate400,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Cyan400,
                        unfocusedBorderColor = Slate700,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Navy900,
                        unfocusedContainerColor = Navy900
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("user_search_input")
                )

                // Role Filter Chips Row
                androidx.compose.foundation.lazy.LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedUserRankFilter == null,
                            onClick = { onSelectedUserRankFilterChanged(null) },
                            label = { Text(if (isAr) "الكل (${usersList.size})" else "All (${usersList.size})", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Gold500,
                                selectedLabelColor = Navy900,
                                containerColor = Navy800,
                                labelColor = Slate300
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedUserRankFilter == null,
                                borderColor = if (selectedUserRankFilter == null) Gold500 else Slate700
                            )
                        )
                    }

                    items(RoleRank.values()) { rank ->
                        val count = usersList.count { it.roleRank == rank }
                        val isSelected = selectedUserRankFilter == rank
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                onSelectedUserRankFilterChanged(if (isSelected) null else rank)
                            },
                            label = {
                                Text(
                                    text = "${rank.badgeIcon} " + (if (isAr) rank.titleAr else rank.titleEn) + " ($count)",
                                    fontSize = 11.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Cyan500,
                                selectedLabelColor = Navy900,
                                containerColor = Navy800,
                                labelColor = Slate300
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) Cyan400 else Slate700
                            )
                        )
                    }
                }
            }
        }

        // New User Form (Expandable)
        if (showCreateUserSection) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Navy900,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Cyan400),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = if (isAr) "إصدار حساب وتحديد رتبته وصلاحياته:" else "Provision Account & Define Permissions:",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Cyan400
                            )
                        )

                        OutlinedTextField(
                            value = newFullName,
                            onValueChange = { newFullName = it },
                            label = { Text(if (isAr) "الاسم الكامل للمستخدم" else "Full Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = newUsername,
                            onValueChange = { newUsername = it },
                            label = { Text(if (isAr) "اسم المستخدم للدخول" else "Username") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = newDeptAr,
                            onValueChange = { newDeptAr = it },
                            label = { Text(if (isAr) "القسم أو الإدارة" else "Department") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = if (isAr) "الرتبة الممنوحة للحساب:" else "Assign Rank / Privilege:",
                            style = MaterialTheme.typography.labelSmall.copy(color = Slate300)
                        )

                        RoleRank.values().forEach { rank ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (selectedRank == rank) Navy700 else Navy800)
                                    .clickable { selectedRank = rank }
                                    .padding(8.dp)
                            ) {
                                RadioButton(
                                    selected = selectedRank == rank,
                                    onClick = { selectedRank = rank },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = Gold400,
                                        unselectedColor = Slate300
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${rank.badgeIcon} " + if (isAr) rank.titleAr else rank.titleEn,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (selectedRank == rank) Gold400 else Color.White,
                                        fontWeight = if (selectedRank == rank) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                            }
                        }

                        Button(
                            onClick = {
                                onCreateUser(
                                    newUsername,
                                    newFullName,
                                    selectedRank,
                                    newDeptAr,
                                    newDeptAr
                                )
                                showCreateUserSection = false
                                newUsername = ""
                                newFullName = ""
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Cyan500,
                                contentColor = Navy900
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("submit_create_user_btn")
                        ) {
                            Text(
                                text = if (isAr) "اعتماد وإنشاء الحساب" else "Certify & Create Account",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Users List
        if (filteredUsers.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Navy800,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate700),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = Slate400,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isAr) "لا توجد حسابات مطابقة لمعايير البحث أو الفلتر" else "No user accounts matched your search/filter criteria",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Slate300)
                        )
                    }
                }
            }
        } else {
            items(filteredUsers, key = { it.id }) { user ->
                UserAccountCard(
                    user = user,
                    isActive = user.id == activeUser.id,
                    language = language,
                    canEdit = true,
                    canDelete = (activeUser.canPurge || isMasterUnlocked) && user.roleRank != RoleRank.SUPREME_COMMANDER,
                    onSwitchToUser = { onSwitchUser(user) },
                    onEdit = { onEditUser(user) },
                    onDelete = { onDeleteUser(user) }
                )
            }
        }

        // Supreme Commander Emergency Action Center
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Navy800,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, RedDanger),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Dangerous,
                            contentDescription = "Purge",
                            tint = RedDanger,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAr) "أمر التدمير وإعادة التهيئة السيادية" else "Root Destruction & System Purge",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = RedDanger
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isAr)
                            "من قوة هذا الكود يستطيع تدمير أو حذف سجلات التطبيق بالكامل كأنه لم يُنشأ أبداً وإعادة تهيئة النظام وفق بروتوكول الأمان الشامل."
                        else
                            "Supreme Commander root command to purge all stored records and reset to pristine initialization state.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Slate300,
                            lineHeight = 18.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onShowPurgeDialog,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RedDanger,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("root_purge_trigger_button")
                    ) {
                        Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAr) "تنفيذ أمر تدمير وإعادة ضبط السجلات" else "Execute System Purge & Reset",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Firestore Cloud Live Audit Logs & Executive Operations Center
        item {
            var auditSourceTab by remember { mutableStateOf(0) } // 0: Firestore Cloud, 1: Local Cache
            var auditFilterLevel by remember { mutableStateOf<String?>("ALL") }

            Card(
                colors = CardDefaults.cardColors(containerColor = Navy800),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, Cyan500.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Header with Cloud status and Sync Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CloudSync,
                                    contentDescription = "Cloud Audit",
                                    tint = Cyan400,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isAr) "سجل الأنشطة الإدارية السحابي (Firestore)" else "Cloud Audit Trail (Firestore)",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Gold400
                                    )
                                )
                            }
                            Text(
                                text = if (isAr)
                                    "توثيق عمليات أصحاب الصلاحيات العالية وكود السيادة في سحابة Google Cloud"
                                else
                                    "Immutable audit log documenting executive actions by root & privileged officers",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Slate400,
                                    fontSize = 10.5.sp
                                )
                            )
                        }

                        // Cloud Sync Button
                        FilledTonalButton(
                            onClick = onSyncFirestoreAudit,
                            enabled = !isFirestoreAuditSyncing,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Cyan600.copy(alpha = 0.2f),
                                contentColor = Cyan300
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("btn_sync_firestore_audit")
                        ) {
                            if (isFirestoreAuditSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = Cyan400
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = "Sync",
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isAr) "مزامنة السحابة" else "Cloud Sync",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Source Selector: Firestore Live vs Local Room Cache
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Navy900)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (auditSourceTab == 0) Cyan600 else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { auditSourceTab = 0 }
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudQueue,
                                    contentDescription = null,
                                    tint = if (auditSourceTab == 0) Navy900 else Cyan400,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = (if (isAr) "سجلات Firestore الحية" else "Firestore Live") + " (${firestoreAuditLogs.size})",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (auditSourceTab == 0) Navy900 else Slate300
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (auditSourceTab == 1) Gold500 else Color.Transparent,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { auditSourceTab = 1 }
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Storage,
                                    contentDescription = null,
                                    tint = if (auditSourceTab == 1) Navy900 else Gold400,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = (if (isAr) "السجل المحلي (Room)" else "Local Room") + " (${auditLogs.size})",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (auditSourceTab == 1) Navy900 else Slate300
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Audit Filters
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "ALL" to (if (isAr) "الكل" else "All"),
                            "ROOT" to (if (isAr) "👑 كود السيادة والجذر" else "👑 Root Sovereign"),
                            "COMMAND" to (if (isAr) "⚡ أوامر تنفيذية" else "⚡ Commands"),
                            "WARNING" to (if (isAr) "⚠️ تنبيهات أمنية" else "⚠️ Security")
                        ).forEach { (filterKey, label) ->
                            val isSelected = auditFilterLevel == filterKey
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSelected) Navy700 else Navy900,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) Cyan400 else Slate800
                                ),
                                modifier = Modifier
                                    .clickable { auditFilterLevel = filterKey }
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = if (isSelected) Cyan300 else Slate400,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Content Rendering: Firestore stream or Local logs
                    if (auditSourceTab == 0) {
                        // Firestore Logs
                        val filteredCloud = remember(firestoreAuditLogs, auditFilterLevel) {
                            firestoreAuditLogs.filter { log ->
                                when (auditFilterLevel) {
                                    "ROOT" -> log.isRootAction || log.isMasterOverride || log.level == "MASTER_OVERRIDE"
                                    "COMMAND" -> log.level == "COMMAND" || log.level == "INFO"
                                    "WARNING" -> log.level == "WARNING" || log.level == "CRITICAL"
                                    else -> true
                                }
                            }
                        }

                        if (filteredCloud.isEmpty()) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Navy900,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.CloudDone, contentDescription = null, tint = Cyan400, modifier = Modifier.size(28.dp))
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = if (isAr) "سجل Firestore جاهز للتوثيق المباشر لأي عملية إدارية" else "Firestore audit log stream active and ready",
                                        style = MaterialTheme.typography.bodySmall.copy(color = Slate300, fontSize = 11.sp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (isAr) "اضغط 'مزامنة السحابة' لرفع العمليات المحلية الحالية" else "Tap 'Cloud Sync' to synchronize current local actions",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Slate400, fontSize = 10.sp)
                                    )
                                }
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                filteredCloud.take(12).forEach { cloudLog ->
                                    FirestoreAuditLogItem(log = cloudLog, isAr = isAr)
                                }
                            }
                        }
                    } else {
                        // Local Room Logs
                        val filteredLocal = remember(auditLogs, auditFilterLevel) {
                            auditLogs.filter { log ->
                                when (auditFilterLevel) {
                                    "ROOT" -> log.level == LogSeverity.MASTER_OVERRIDE
                                    "COMMAND" -> log.level == LogSeverity.COMMAND || log.level == LogSeverity.INFO
                                    "WARNING" -> log.level == LogSeverity.WARNING || log.level == LogSeverity.CRITICAL
                                    else -> true
                                }
                            }
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            filteredLocal.take(10).forEach { log ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Navy900,
                                    border = androidx.compose.foundation.BorderStroke(
                                        0.5.dp,
                                        if (log.level == LogSeverity.MASTER_OVERRIDE) Gold500.copy(alpha = 0.5f) else Slate800
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = if (log.level == LogSeverity.MASTER_OVERRIDE) "👑" else "🛡️",
                                                    fontSize = 12.sp
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = log.actorName,
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = if (log.level == LogSeverity.MASTER_OVERRIDE) Gold400 else Cyan300,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                )
                                            }
                                            Text(
                                                text = log.timestamp,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = Slate400,
                                                    fontSize = 9.sp,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = if (isAr) log.actionAr else log.actionEn,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Color.White,
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        )

                                        if (log.details.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Text(
                                                text = log.details,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = Slate400,
                                                    fontSize = 10.sp
                                                )
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
    }
}

@Composable
fun FirestoreAuditLogItem(
    log: FirestoreAuditLog,
    isAr: Boolean
) {
    var isExpanded by remember { mutableStateOf(false) }

    val levelColor = when (log.level) {
        "MASTER_OVERRIDE" -> Gold400
        "COMMAND" -> GreenSuccess
        "WARNING" -> Gold500
        "CRITICAL" -> RedDanger
        else -> Cyan400
    }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Navy900,
        border = androidx.compose.foundation.BorderStroke(
            if (log.isRootAction || log.isMasterOverride) 1.2.dp else 0.8.dp,
            if (log.isRootAction || log.isMasterOverride) Gold400.copy(alpha = 0.7f) else Slate800
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Header Row: Actor + Level Badge + Timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (log.isRootAction || log.isMasterOverride) "👑" else "🛡️",
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = log.actorName,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (log.isRootAction) Gold400 else Cyan300,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(3.dp),
                        color = levelColor.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = log.level,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = levelColor,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Cloud,
                        contentDescription = "Firestore Document",
                        tint = Cyan400,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = log.timestamp.ifEmpty { "Live" },
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Slate400,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Action Description
            Text(
                text = if (isAr && log.actionAr.isNotEmpty()) log.actionAr else log.actionEn,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )

            // Target user or short info
            if (log.targetUserName.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isAr) "المستخدم المستهدف: " else "Target User: ",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate400, fontSize = 10.sp)
                    )
                    Text(
                        text = log.targetUserName,
                        style = MaterialTheme.typography.bodySmall.copy(color = Gold300, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    )
                }
            }

            // Expanded Technical Forensics
            if (isExpanded) {
                Spacer(modifier = Modifier.height(6.dp))
                HorizontalDivider(color = Slate800, modifier = Modifier.padding(vertical = 4.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Navy800)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = "Document ID: ${log.id}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Cyan300,
                            fontSize = 9.5.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                    Text(
                        text = "Actor Role: ${log.actorRole} (Rank ${log.actorRankLevel})",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Slate300,
                            fontSize = 9.5.sp
                        )
                    )
                    if (log.actorEmail.isNotEmpty()) {
                        Text(
                            text = "Actor Email: ${log.actorEmail}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Slate400,
                                fontSize = 9.5.sp
                            )
                        )
                    }
                    if (log.details.isNotEmpty()) {
                        Text(
                            text = "Details: ${log.details}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Slate200,
                                fontSize = 10.sp
                            )
                        )
                    }
                    Text(
                        text = "Client Platform: ${log.clientPlatform} | Root Sovereign: ${log.isRootAction}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Slate400,
                            fontSize = 9.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun UserAccountCard(
    user: UserAccount,
    isActive: Boolean,
    language: AppLanguage,
    canEdit: Boolean = true,
    canDelete: Boolean,
    onSwitchToUser: () -> Unit,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit
) {
    val isAr = language == AppLanguage.ARABIC

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isActive) Navy700 else Navy800,
        border = androidx.compose.foundation.BorderStroke(
            if (user.roleRank == RoleRank.SUPREME_COMMANDER || user.isMasterOverride) 2.dp else if (isActive) 1.5.dp else 1.dp,
            if (user.roleRank == RoleRank.SUPREME_COMMANDER || user.isMasterOverride) Gold400 else if (isActive) Cyan400 else Slate700
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("user_card_${user.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rank Icon Box / Avatar
                val userPhoto = user.photoUrl
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (user.roleRank == RoleRank.SUPREME_COMMANDER) Gold500.copy(alpha = 0.2f) else Navy900)
                        .border(1.dp, if (user.roleRank == RoleRank.SUPREME_COMMANDER) Gold400 else Slate700, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (userPhoto.isNotEmpty() && (userPhoto.startsWith("/") || userPhoto.startsWith("file:") || userPhoto.startsWith("http") || userPhoto.startsWith("content:"))) {
                        coil.compose.AsyncImage(
                            model = coil.request.ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                                .data(if (userPhoto.startsWith("/")) java.io.File(userPhoto) else userPhoto)
                                .crossfade(true)
                                .build(),
                            contentDescription = "User Avatar",
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(10.dp))
                        )
                    } else if (user.roleRank == RoleRank.SUPREME_COMMANDER || user.fullName.contains("ياسر") || user.fullName.contains("Yasser")) {
                        Image(
                            painter = painterResource(id = R.drawable.img_father_yasser_avatar),
                            contentDescription = "Portrait",
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(10.dp))
                        )
                    } else {
                        Text(text = user.roleRank.badgeIcon, fontSize = 24.sp)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user.fullName,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        if (isActive) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Gold500
                            ) {
                                Text(
                                    text = if (isAr) "الحساب النشط" else "Active",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Navy900,
                                        fontSize = 9.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (isAr) user.roleRank.titleAr else user.roleRank.titleEn,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (user.roleRank.level == 6) Gold300 else Cyan400,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Slate800
                        ) {
                            Text(
                                text = "Rank ${user.roleRank.level}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Slate300,
                                    fontSize = 9.sp
                                ),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }

                    Text(
                        text = "@${user.username} | ${if (isAr) user.departmentAr else user.departmentEn}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Slate300,
                            fontSize = 10.sp
                        )
                    )
                }

                // Quick Login Switch Button
                if (!isActive) {
                    FilledTonalButton(
                        onClick = onSwitchToUser,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Cyan600.copy(alpha = 0.25f),
                            contentColor = Cyan300
                        ),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("btn_login_as_${user.id}")
                    ) {
                        Text(
                            text = if (isAr) "دخول" else "Login",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Permissions Badges Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                PermissionBadge(label = if (isAr) "قراءة" else "Read", enabled = user.canRead)
                PermissionBadge(label = if (isAr) "كتابة" else "Write", enabled = user.canWrite)
                PermissionBadge(label = if (isAr) "تنفيذ" else "Exec", enabled = user.canExecute)
                PermissionBadge(label = if (isAr) "إدارة" else "Admin", enabled = user.canAdminister)
                PermissionBadge(label = if (isAr) "تطهير" else "Purge", enabled = user.canPurge, activeColor = RedDanger)
                if (user.isMasterOverride) {
                    PermissionBadge(label = if (isAr) "كود السيادة" else "Master", enabled = true, activeColor = Gold400)
                }
            }

            HorizontalDivider(
                color = Slate700,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Action Buttons Footer: Edit User Permissions & Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Edit Privileges & Rank Button (Root Access Feature)
                Button(
                    onClick = onEdit,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Gold500.copy(alpha = 0.2f),
                        contentColor = Gold300
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Gold400.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("btn_edit_user_${user.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Role & Privileges",
                        tint = Gold400,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAr) "تعديل الرتبة والصلاحيات" else "Edit Privileges & Rank",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Gold300
                        )
                    )
                }

                if (canDelete) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("btn_delete_user_${user.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete",
                            tint = RedDanger,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionBadge(
    label: String,
    enabled: Boolean,
    activeColor: Color = GreenSuccess
) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = if (enabled) activeColor.copy(alpha = 0.2f) else Slate800,
        border = androidx.compose.foundation.BorderStroke(
            0.5.dp,
            if (enabled) activeColor.copy(alpha = 0.6f) else Slate700
        )
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = if (enabled) activeColor else Slate400,
                fontSize = 9.5.sp,
                fontWeight = if (enabled) FontWeight.Bold else FontWeight.Normal
            ),
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
        )
    }
}
