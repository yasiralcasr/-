package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.SectorType
import com.example.data.model.UserAccount
import com.example.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProgramBuilderScreen(
    language: AppLanguage,
    activeUser: UserAccount,
    isMasterUnlocked: Boolean,
    onCreateProgram: (
        titleAr: String,
        titleEn: String,
        sectorType: SectorType,
        descAr: String,
        descEn: String,
        automationType: String,
        targetAudienceAr: String,
        targetAudienceEn: String,
        integrationHooks: List<String>
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    val isAr = language == AppLanguage.ARABIC

    var titleAr by remember { mutableStateOf("") }
    var titleEn by remember { mutableStateOf("") }
    var selectedSector by remember { mutableStateOf(SectorType.COMMERCIAL) }
    var descAr by remember { mutableStateOf("") }
    var descEn by remember { mutableStateOf("") }
    var automationType by remember { mutableStateOf("أتمتة كاملة 100% (Full Automation)") }
    var targetAudienceAr by remember { mutableStateOf("") }
    var targetAudienceEn by remember { mutableStateOf("") }

    val availableHooks = listOf(
        "REST API v3 TLS 1.3",
        "gRPC Gateway",
        "Modbus TCP / SCADA",
        "MQTT IoT Broker",
        "ISO 20022 Finance",
        "AI Sovereign Core"
    )
    val selectedHooks = remember { mutableStateListOf("REST API v3 TLS 1.3", "gRPC Gateway") }

    val canCreate = activeUser.canWrite || isMasterUnlocked

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Builder Header Banner
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Navy800,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Gold500),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
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
                                imageVector = Icons.Default.Engineering,
                                contentDescription = "Builder",
                                tint = Gold400
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isAr) "منشئ البرامج والأتمتة والربط الرقمي" else "Enterprise Automation & System Builder",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Gold400
                                )
                            )
                            Text(
                                text = if (isAr) "تصميم وبناء أي منظومة لأي قطاع مع توليد فوري لبيانات الدخول"
                                else "Build & deploy software with automated credential generation",
                                style = MaterialTheme.typography.bodySmall.copy(color = Slate300, fontSize = 11.sp)
                            )
                        }
                    }

                    if (!canCreate) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = OrangeWarning.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, OrangeWarning)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Read Only",
                                    tint = OrangeWarning,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isAr)
                                        "تنبيه: أنت مسجل بحساب قراءة فقط (${activeUser.roleRank.titleAr}). يمكنك استعراض الحقول لكن يلزم رتبة أعلى للإنشاء."
                                    else
                                        "Notice: You are logged in with Read-Only rank (${activeUser.roleRank.titleEn}).",
                                    style = MaterialTheme.typography.labelSmall.copy(color = OrangeWarning, fontSize = 11.sp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Step 1: Target Sector Selection
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Navy800),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate700)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = if (isAr) "١. تحديد القطاع المستفيد والجهة" else "1. Select Target Sector",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Cyan400
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SectorType.values().take(2).forEach { sector ->
                            SectorSelectCard(
                                sector = sector,
                                isSelected = selectedSector == sector,
                                language = language,
                                onClick = { selectedSector = sector },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SectorType.values().drop(2).take(2).forEach { sector ->
                            SectorSelectCard(
                                sector = sector,
                                isSelected = selectedSector == sector,
                                language = language,
                                onClick = { selectedSector = sector },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Step 2: Program Identity & Scope
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Navy800),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate700)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (isAr) "٢. هوية البرنامج وتفاصيل المنظومة" else "2. System Scope & Identity",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Cyan400
                        )
                    )

                    OutlinedTextField(
                        value = titleAr,
                        onValueChange = { titleAr = it },
                        label = { Text(if (isAr) "اسم المنظومة أو البرنامج (بالعربية)" else "System Title (Arabic)") },
                        placeholder = { Text("مثال: منظومة الربط المالي والأتمتة الذكية") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Gold400,
                            unfocusedBorderColor = Slate700,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("program_title_ar_input")
                    )

                    OutlinedTextField(
                        value = titleEn,
                        onValueChange = { titleEn = it },
                        label = { Text(if (isAr) "اسم البرنامج (بالإنجليزية)" else "System Title (English)") },
                        placeholder = { Text("e.g. Smart Enterprise Automation Matrix") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Gold400,
                            unfocusedBorderColor = Slate700,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = descAr,
                        onValueChange = { descAr = it },
                        label = { Text(if (isAr) "وصف المنظومة وأهداف الخدمة" else "Scope Description (Arabic)") },
                        placeholder = { Text("صف مهام البرنامج ومسارات الأتمتة المدمجة...") },
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Gold400,
                            unfocusedBorderColor = Slate700,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = targetAudienceAr,
                        onValueChange = { targetAudienceAr = it },
                        label = { Text(if (isAr) "الجهة المستفيدة المحددة" else "Target Beneficiary Entity") },
                        placeholder = { Text("مثال: الهيئة الملكية / شركة سابك / جمعية البر") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Gold400,
                            unfocusedBorderColor = Slate700,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Step 3: Automation Level & Protocols
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Navy800),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate700)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (isAr) "٣. بروتوكولات الأتمتة والربط التقني" else "3. Automation & Integration Protocols",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Cyan400
                        )
                    )

                    val automationOptions = listOf(
                        "أتمتة كاملة 100% (Full Autonomous Engine)",
                        "أتمتة متقدمة وتحكم SCADA وتيليمتري",
                        "ربط هجين متعدد الفروع (Multi-Node Hybrid)",
                        "معالجة ذكية مؤمّنة بالتشفير الكمومي"
                    )

                    automationOptions.forEach { option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (automationType == option) Navy700 else Navy900)
                                .clickable { automationType = option }
                                .padding(10.dp)
                        ) {
                            RadioButton(
                                selected = automationType == option,
                                onClick = { automationType = option },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Gold400,
                                    unselectedColor = Slate300
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = option,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (automationType == option) Gold400 else Slate200,
                                    fontWeight = if (automationType == option) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (isAr) "قنوات الربط والتكامل البرمجي:" else "Integration Connectors:",
                        style = MaterialTheme.typography.labelSmall.copy(color = Slate300)
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        availableHooks.forEach { hook ->
                            val isChecked = selectedHooks.contains(hook)
                            FilterChip(
                                selected = isChecked,
                                onClick = {
                                    if (isChecked) selectedHooks.remove(hook) else selectedHooks.add(hook)
                                },
                                label = { Text(hook, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Cyan500,
                                    selectedLabelColor = Navy900,
                                    containerColor = Navy900,
                                    labelColor = Slate200
                                )
                            )
                        }
                    }
                }
            }
        }

        // Action Button
        item {
            Button(
                onClick = {
                    val finalTitleAr = titleAr.ifBlank { "منظومة الشرق والغرب لـ ${selectedSector.labelAr}" }
                    val finalTitleEn = titleEn.ifBlank { "East-West Global ${selectedSector.labelEn} Engine" }
                    val finalDescAr = descAr.ifBlank { "برنامج مؤتمت ومتكامل لإدارة خدمات ${selectedSector.labelAr} وربط الأنظمة." }
                    val finalDescEn = descEn.ifBlank { "Integrated automated system for ${selectedSector.labelEn}." }

                    onCreateProgram(
                        finalTitleAr,
                        finalTitleEn,
                        selectedSector,
                        finalDescAr,
                        finalDescEn,
                        automationType,
                        targetAudienceAr,
                        targetAudienceEn,
                        selectedHooks.toList()
                    )
                },
                enabled = canCreate,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold500,
                    contentColor = Navy900,
                    disabledContainerColor = Slate700,
                    disabledContentColor = Slate300
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("complete_creation_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Complete",
                        tint = Navy900,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAr)
                            "إتمام الإنشاء وإصدار اسم المستخدم والرقم السري"
                        else
                            "Complete Creation & Generate Credentials",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Navy900,
                            fontSize = 14.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun SectorSelectCard(
    sector: SectorType,
    isSelected: Boolean,
    language: AppLanguage,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAr = language == AppLanguage.ARABIC

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) Navy700 else Navy900,
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected) 1.5.dp else 1.dp,
            if (isSelected) Gold400 else Slate800
        ),
        modifier = modifier
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = sector.icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isAr) sector.labelAr else sector.labelEn,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) Gold400 else Slate200,
                    fontSize = 11.sp
                ),
                maxLines = 1
            )
        }
    }
}
