package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.control.MagicGatewayManager
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun ProgramsScreen(
    programsList: List<EnterpriseProgram>,
    selectedSector: SectorType?,
    searchQuery: String,
    language: AppLanguage,
    activeUser: UserAccount,
    isMasterUnlocked: Boolean,
    onSectorFilterChange: (SectorType?) -> Unit,
    onSearchChange: (String) -> Unit,
    onDeleteProgram: (EnterpriseProgram) -> Unit,
    onToggleApproval: (EnterpriseProgram) -> Unit = {},
    onNavigateToBuilder: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAr = language == AppLanguage.ARABIC
    val context = LocalContext.current
    val isClientRole = activeUser.roleRank == RoleRank.OBSERVER || activeUser.roleRank == RoleRank.SOLDIER

    val filteredPrograms = programsList.filter { program ->
        val matchesSector = selectedSector == null || program.sectorType == selectedSector
        val matchesSearch = searchQuery.isBlank() ||
                program.titleAr.contains(searchQuery, ignoreCase = true) ||
                program.titleEn.contains(searchQuery, ignoreCase = true) ||
                program.descriptionAr.contains(searchQuery, ignoreCase = true) ||
                program.generatedUsername.contains(searchQuery, ignoreCase = true)
        val matchesClientApproval = if (isClientRole && !isMasterUnlocked) program.isApprovedForClients else true
        matchesSector && matchesSearch && matchesClientApproval
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Welcome Hero Banner with Father Yasser Identity
        item {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Navy800,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Gold500),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("father_yasser_welcome_banner")
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Father Yasser Avatar
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, Gold400, CircleShape)
                                    .background(Navy900)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.img_father_yasser_avatar),
                                    contentDescription = "Father Yasser Portrait",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (isAr) "تطبيق ياسر الرشيدي (CEO)" else "Yasser Al-Rashidi App (CEO)",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Gold400,
                                            fontSize = 17.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = "👑", fontSize = 16.sp)
                                }

                                Text(
                                    text = if (isAr) "شركة الشرق والغرب العالمية للأتمتة والتوريد الصناعي"
                                    else "East & West Global Automation & Industrial Platform",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Slate200,
                                        fontSize = 11.5.sp
                                    )
                                )

                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Gold500.copy(alpha = 0.2f),
                                    border = androidx.compose.foundation.BorderStroke(0.5.dp, Gold400),
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Text(
                                        text = if (isAr) "المؤسس وصاحب الصلاحية المطلقة (Rank 6 Root)"
                                        else "Founder & Root Sovereign Authority",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Gold300,
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
            }
        }

        // Hero Stats Card
        item {
            ExecutiveStatsCard(
                totalCount = programsList.size,
                activeCount = programsList.count { it.status == ProgramStatus.ACTIVE },
                completedCount = programsList.count { it.status == ProgramStatus.COMPLETED },
                language = language,
                onNavigateToBuilder = onNavigateToBuilder
            )
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = {
                    Text(
                        if (isAr) "ابحث في البرامج، المنظومات، أو بيانات الاعتماد..."
                        else "Search programs, systems, or credentials...",
                        color = Slate300
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Gold400
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = Slate300)
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Gold400,
                    unfocusedBorderColor = Slate700,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("programs_search_field")
            )
        }

        // Magic Gateways Direct Jump Bar
        item {
            MagicGatewaysSection(
                language = language,
                onOpenGateway = { appName ->
                    MagicGatewayManager.openGateway(context, appName)
                }
            )
        }

        // Sector Filter Chips
        item {
            SectorFilterChips(
                selectedSector = selectedSector,
                language = language,
                onSelectSector = onSectorFilterChange
            )
        }

        // Section Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isAr) "المنظومات والبرامج المعتمدة (${filteredPrograms.size})"
                    else "Certified Enterprise Programs (${filteredPrograms.size})",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Gold400
                    )
                )

                Text(
                    text = if (isAr) "ربط سحابي مشفر" else "Encrypted Cloud Link",
                    style = MaterialTheme.typography.labelSmall.copy(color = Cyan400)
                )
            }
        }

        // Programs List
        if (filteredPrograms.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Navy800,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = "Empty",
                            tint = Slate300,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (isAr) "لا توجد منظومات مطابقة لمعايير البحث" else "No matching programs found",
                            color = Slate200,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        } else {
            items(filteredPrograms, key = { it.id }) { program ->
                ProgramCard(
                    program = program,
                    language = language,
                    canDelete = activeUser.canAdminister || isMasterUnlocked,
                    canManageApproval = activeUser.canAdminister || isMasterUnlocked,
                    onDelete = { onDeleteProgram(program) },
                    onToggleApproval = { onToggleApproval(program) },
                    onCopyCredentials = {
                        val credText = "EWG System: ${if (isAr) program.titleAr else program.titleEn}\nUsername: ${program.generatedUsername}\nPassword: ${program.generatedPassword}\nKey: ${program.systemKey}"
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("EWG Credentials", credText))
                    }
                )
            }
        }
    }
}

@Composable
fun ExecutiveStatsCard(
    totalCount: Int,
    activeCount: Int,
    completedCount: Int,
    language: AppLanguage,
    onNavigateToBuilder: () -> Unit
) {
    val isAr = language == AppLanguage.ARABIC

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Navy800,
        border = androidx.compose.foundation.BorderStroke(1.dp, Gold500.copy(alpha = 0.6f)),
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
                Column {
                    Text(
                        text = if (isAr) "بوابة الحلول الرقمية والربط المؤسسي" else "Enterprise Automation Hub",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = if (isAr) "مواءمة شاملة للقطاع التجاري والحكومي وغير الربحي" else "Government, Commercial, Non-Profit & Industry",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate300, fontSize = 11.sp)
                    )
                }

                FilledTonalButton(
                    onClick = onNavigateToBuilder,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Gold500,
                        contentColor = Navy900
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("create_new_program_header_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "Add",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isAr) "إنشاء منظومة" else "Build",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatMetricBox(
                    label = if (isAr) "إجمالي المنظومات" else "Total Systems",
                    value = "$totalCount",
                    color = Gold400,
                    modifier = Modifier.weight(1f)
                )
                StatMetricBox(
                    label = if (isAr) "نشط ومتصل" else "Active Online",
                    value = "$activeCount",
                    color = GreenSuccess,
                    modifier = Modifier.weight(1f)
                )
                StatMetricBox(
                    label = if (isAr) "معتمد وموثق" else "Certified",
                    value = "$completedCount",
                    color = Cyan400,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun StatMetricBox(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Navy900,
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate700),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Slate300,
                    fontSize = 10.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun SectorFilterChips(
    selectedSector: SectorType?,
    language: AppLanguage,
    onSelectSector: (SectorType?) -> Unit
) {
    val isAr = language == AppLanguage.ARABIC

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        item {
            FilterChip(
                selected = selectedSector == null,
                onClick = { onSelectSector(null) },
                label = { Text(if (isAr) "الكل" else "All Sectors") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Gold500,
                    selectedLabelColor = Navy900,
                    containerColor = Navy800,
                    labelColor = Slate200
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selectedSector == null,
                    borderColor = Slate700,
                    selectedBorderColor = Gold400
                )
            )
        }

        items(SectorType.values()) { sector ->
            val isSelected = selectedSector == sector
            FilterChip(
                selected = isSelected,
                onClick = { onSelectSector(if (isSelected) null else sector) },
                label = {
                    Text("${sector.icon} " + if (isAr) sector.labelAr else sector.labelEn)
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Cyan500,
                    selectedLabelColor = Navy900,
                    containerColor = Navy800,
                    labelColor = Slate200
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = Slate700,
                    selectedBorderColor = Cyan400
                )
            )
        }
    }
}

@Composable
fun ProgramCard(
    program: EnterpriseProgram,
    language: AppLanguage,
    canDelete: Boolean,
    canManageApproval: Boolean = false,
    onDelete: () -> Unit,
    onToggleApproval: () -> Unit = {},
    onCopyCredentials: () -> Unit
) {
    val isAr = language == AppLanguage.ARABIC
    var expanded by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Navy800,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (program.isApprovedForClients) Slate700 else Gold500.copy(alpha = 0.6f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("program_card_${program.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Sector Badge & Status & Client Visibility Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = program.sectorType.icon, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAr) program.sectorAr else program.sectorEn,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Cyan400,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Client Visibility Indicator
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (program.isApprovedForClients) GreenSuccess.copy(alpha = 0.15f) else Gold500.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (program.isApprovedForClients) GreenSuccess.copy(alpha = 0.4f) else Gold500.copy(alpha = 0.4f)
                        )
                    ) {
                        Text(
                            text = if (program.isApprovedForClients) {
                                if (isAr) "🌐 معتمد للعميل" else "🌐 Client Visible"
                            } else {
                                if (isAr) "🔒 محجوب (إداري فقط)" else "🔒 Admin Only"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (program.isApprovedForClients) GreenSuccess else Gold400,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.5.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.5.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(program.status.colorHex).copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Color(program.status.colorHex).copy(alpha = 0.5f)
                        )
                    ) {
                        Text(
                            text = if (isAr) program.status.labelAr else program.status.labelEn,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(program.status.colorHex),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = if (isAr) program.titleAr else program.titleEn,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            // Description
            Text(
                text = if (isAr) program.descriptionAr else program.descriptionEn,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Slate300,
                    lineHeight = 18.sp
                ),
                maxLines = if (expanded) 10 else 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(vertical = 6.dp)
            )

            // Automation & Target pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Navy900,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = if (isAr) "مستوى الأتمتة والربط:" else "Automation Level:",
                            style = MaterialTheme.typography.labelSmall.copy(color = Slate300, fontSize = 10.sp)
                        )
                        Text(
                            text = program.automationLevel,
                            style = MaterialTheme.typography.labelSmall.copy(color = Gold400, fontWeight = FontWeight.Bold)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Navy900,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = if (isAr) "الجهة المستهدفة:" else "Target Entity:",
                            style = MaterialTheme.typography.labelSmall.copy(color = Slate300, fontSize = 10.sp)
                        )
                        Text(
                            text = if (isAr) program.targetAudienceAr else program.targetAudienceEn,
                            style = MaterialTheme.typography.labelSmall.copy(color = Slate200, fontWeight = FontWeight.SemiBold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Credentials snippet
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Navy900,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = if (isAr) "بيانات الدخول المعتمدة للمنظومة:" else "Generated Access Credentials:",
                                style = MaterialTheme.typography.labelSmall.copy(color = Gold400, fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "👤 " + (if (isAr) "المستخدم: " else "User: ") + program.generatedUsername,
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.White, fontFamily = FontFamily.Monospace)
                            )
                            Text(
                                text = "🔑 " + (if (isAr) "الرقم السري: " else "Password: ") + program.generatedPassword,
                                style = MaterialTheme.typography.bodySmall.copy(color = Cyan400, fontFamily = FontFamily.Monospace)
                            )
                            Text(
                                text = "🛡️ " + (if (isAr) "المعرف: " else "Key: ") + program.systemKey,
                                style = MaterialTheme.typography.bodySmall.copy(color = Slate300, fontFamily = FontFamily.Monospace)
                            )
                        }
                    }

                    // Integration Endpoints
                    if (program.integrationEndpoints.isNotEmpty()) {
                        Text(
                            text = if (isAr) "قنوات الربط والتكامل المتاحة:" else "Integration Endpoints:",
                            style = MaterialTheme.typography.labelSmall.copy(color = Slate300, fontSize = 11.sp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            program.integrationEndpoints.forEach { ep ->
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Navy700
                                ) {
                                    Text(
                                        text = ep,
                                        style = MaterialTheme.typography.labelSmall.copy(color = Cyan400, fontSize = 10.sp),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { expanded = !expanded },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = if (expanded) {
                            if (isAr) "إخفاء التفاصيل ▲" else "Hide Details ▲"
                        } else {
                            if (isAr) "عرض التفاصيل وبيانات الدخول ▼" else "Show Details & Login ▼"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(color = Gold400)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (canManageApproval) {
                        IconButton(
                            onClick = onToggleApproval,
                            modifier = Modifier.size(32.dp).testTag("toggle_approval_${program.id}")
                        ) {
                            Icon(
                                imageVector = if (program.isApprovedForClients) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (program.isApprovedForClients) "Hide from Clients" else "Approve for Clients",
                                tint = if (program.isApprovedForClients) GreenSuccess else Gold400,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onCopyCredentials,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Credentials",
                            tint = Cyan400,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    if (canDelete) {
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(32.dp)
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
}

@Composable
fun MagicGatewaysSection(
    language: AppLanguage,
    onOpenGateway: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isAr = language == AppLanguage.ARABIC

    val popularGateways = listOf(
        Pair("قوى", "Qiwa Platform"),
        Pair("أبشر", "Absher Portal"),
        Pair("مدد", "Mudad Wages"),
        Pair("التأمينات", "GOSI Insurance"),
        Pair("الزكاة والضريبة", "ZATCA Tax"),
        Pair("ناجز", "Najiz Justice"),
        Pair("بلدي", "Balady Municipal"),
        Pair("مقيم", "Muqeem Portal")
    )

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Navy800,
        border = androidx.compose.foundation.BorderStroke(1.dp, Cyan400.copy(alpha = 0.4f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("magic_gateways_section")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.OpenInBrowser,
                        contentDescription = "Gateways",
                        tint = Cyan400,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAr) "بوابات القفز والربط الذكي السريع" else "Magic Direct Access Gateways",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Cyan400
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Gold500.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = if (isAr) "تخطي العوائق" else "Fast Route",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Gold400,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isAr)
                    "القفز المباشر وتخطي النوافذ للوصول لصفحات الخدمات ومنصات الأعمال (قوى، أبشر، مدد، التأمينات...):"
                else
                    "Instant direct link navigation to government & enterprise business portals:",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Slate300,
                    fontSize = 11.sp
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 2.dp)
            ) {
                items(popularGateways) { (nameAr, nameEn) ->
                    FilledTonalButton(
                        onClick = { onOpenGateway(nameAr) },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Navy900,
                            contentColor = Gold400
                        ),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate700),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("gateway_btn_${nameAr}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Launch,
                            contentDescription = "Jump",
                            tint = Cyan400,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isAr) nameAr else nameEn,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
