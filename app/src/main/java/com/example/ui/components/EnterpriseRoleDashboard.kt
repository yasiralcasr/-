package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.firestore.FirestoreAuditLog
import com.example.data.firestore.FirestoreUserProfile
import com.example.data.model.*
import com.example.ui.AppTab
import com.example.ui.theme.*

/**
 * Enterprise Dashboard Component
 * Conditionally renders tailored, high-fidelity views for:
 * 1. Administrators & Supreme Leadership (Full Governance, Audit Feeds, Key Management, Revenue Pools)
 * 2. End-Users / Clients / Observers (Approved Products, Order Placement, Tracking, Service Inquiries)
 *
 * Driven by role rankings and Firestore synchronized profiles.
 */
@Composable
fun EnterpriseRoleDashboard(
    activeUser: UserAccount,
    firestoreProfile: FirestoreUserProfile?,
    isMasterUnlocked: Boolean,
    language: AppLanguage,
    programsList: List<EnterpriseProgram>,
    industrialProducts: List<IndustrialProduct>,
    industrialOrders: List<IndustrialOrder>,
    usersList: List<UserAccount>,
    auditLogs: List<AuditLogEntry>,
    firestoreAuditLogs: List<FirestoreAuditLog>,
    charityPoolBalance: Double,
    totalRevenueProcessed: Double,
    onNavigateTab: (AppTab) -> Unit,
    onToggleApproval: (EnterpriseProgram) -> Unit,
    onOpenMasterDialog: () -> Unit,
    onOpenOrderDialog: (IndustrialProduct) -> Unit,
    onOpenLoginDialog: () -> Unit,
    onSwitchUser: (UserAccount) -> Unit,
    modifier: Modifier = Modifier
) {
    val isAr = language == AppLanguage.ARABIC
    val isAdmin = isMasterUnlocked || activeUser.canAdminister || activeUser.roleRank.level >= RoleRank.SUPERVISOR.level

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("enterprise_role_dashboard")
    ) {
        if (isAdmin) {
            AdminExecutiveDashboardView(
                activeUser = activeUser,
                firestoreProfile = firestoreProfile,
                isMasterUnlocked = isMasterUnlocked,
                language = language,
                programsList = programsList,
                industrialProducts = industrialProducts,
                industrialOrders = industrialOrders,
                usersList = usersList,
                auditLogs = auditLogs,
                firestoreAuditLogs = firestoreAuditLogs,
                charityPoolBalance = charityPoolBalance,
                totalRevenueProcessed = totalRevenueProcessed,
                onNavigateTab = onNavigateTab,
                onToggleApproval = onToggleApproval,
                onOpenMasterDialog = onOpenMasterDialog,
                onSwitchUser = onSwitchUser
            )
        } else {
            EndUserClientPortalView(
                activeUser = activeUser,
                firestoreProfile = firestoreProfile,
                language = language,
                programsList = programsList.filter { it.isApprovedForClients },
                industrialProducts = industrialProducts.filter { it.isApprovedForClients },
                industrialOrders = industrialOrders,
                onNavigateTab = onNavigateTab,
                onOpenOrderDialog = onOpenOrderDialog,
                onOpenLoginDialog = onOpenLoginDialog
            )
        }
    }
}

// -------------------------------------------------------------------------------------
// 1. ADMIN & LEADERSHIP DASHBOARD VIEW
// -------------------------------------------------------------------------------------
@Composable
private fun AdminExecutiveDashboardView(
    activeUser: UserAccount,
    firestoreProfile: FirestoreUserProfile?,
    isMasterUnlocked: Boolean,
    language: AppLanguage,
    programsList: List<EnterpriseProgram>,
    industrialProducts: List<IndustrialProduct>,
    industrialOrders: List<IndustrialOrder>,
    usersList: List<UserAccount>,
    auditLogs: List<AuditLogEntry>,
    firestoreAuditLogs: List<FirestoreAuditLog>,
    charityPoolBalance: Double,
    totalRevenueProcessed: Double,
    onNavigateTab: (AppTab) -> Unit,
    onToggleApproval: (EnterpriseProgram) -> Unit,
    onOpenMasterDialog: () -> Unit,
    onSwitchUser: (UserAccount) -> Unit,
    modifier: Modifier = Modifier
) {
    val isAr = language == AppLanguage.ARABIC
    val pendingApprovalCount = programsList.count { !it.isApprovedForClients }
    val activeOrdersCount = industrialOrders.count { it.status != OrderStatus.DELIVERED && it.status != OrderStatus.CANCELLED }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Admin Banner
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Navy800,
                border = BorderStroke(1.5.dp, Gold500),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_dashboard_banner")
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
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Gold500.copy(alpha = 0.2f))
                                    .border(1.5.dp, Gold400, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = "Admin",
                                    tint = Gold400
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isAr) "لوحة القيادة الإدارية والسيادية" else "Executive Admin Command Dashboard",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Gold400
                                    )
                                )
                                Text(
                                    text = "${if (isAr) "المسؤول النشط: " else "Active Officer: "}${activeUser.fullName} (${activeUser.roleRank.badgeIcon} ${if (isAr) activeUser.roleRank.titleAr else activeUser.roleRank.titleEn})",
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
                                    if (isAr) "👑 كود السيادة مفعل" else "👑 ROOT UNLOCKED"
                                } else {
                                    if (isAr) "مستوى المشرف" else "ADMIN LEVEL"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isMasterUnlocked) GreenSuccess else Gold400,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // KPIs Metrics Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AdminStatCard(
                            title = if (isAr) "إجمالي المنظومات" else "Total Systems",
                            value = "${programsList.size}",
                            subtitle = if (isAr) "$pendingApprovalCount محجوبة للتدقيق" else "$pendingApprovalCount In Review",
                            icon = Icons.Default.Apps,
                            accentColor = Cyan400,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigateTab(AppTab.PROGRAMS) }
                        )

                        AdminStatCard(
                            title = if (isAr) "المستخدمين المسجلين" else "Total Users",
                            value = "${usersList.size}",
                            subtitle = if (isAr) "منظومة الحوكمة" else "Governance Roster",
                            icon = Icons.Default.SupervisorAccount,
                            accentColor = Gold400,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigateTab(AppTab.COMMAND_ROLES) }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AdminStatCard(
                            title = if (isAr) "طلبات التوريد LK-W" else "LK-W Orders",
                            value = "$activeOrdersCount نشطة",
                            subtitle = if (isAr) "${industrialProducts.size} معدة صناعية" else "${industrialProducts.size} Hardware Units",
                            icon = Icons.Default.PrecisionManufacturing,
                            accentColor = GreenSuccess,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigateTab(AppTab.INDUSTRIAL_LKW) }
                        )

                        AdminStatCard(
                            title = if (isAr) "صندوق العطاء والإيثار" else "Altruism Pool",
                            value = "%,.0f SAR".format(charityPoolBalance),
                            subtitle = if (isAr) "33% تحلية مباركة" else "33% Desalinated",
                            icon = Icons.Default.VolunteerActivism,
                            accentColor = Cyan300,
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigateTab(AppTab.CONTINENTS_KASHEF) }
                        )
                    }
                }
            }
        }

        // Quick Administrative Actions Row
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (isAr) "⚡ إجراءات الإدارة السريعة" else "⚡ Quick Executive Actions",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Gold400)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalButton(
                        onClick = { onNavigateTab(AppTab.BUILDER) },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Cyan600.copy(alpha = 0.25f),
                            contentColor = Cyan300
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.AddBox, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = if (isAr) "إنشاء منظومة" else "Build System", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }

                    FilledTonalButton(
                        onClick = onOpenMasterDialog,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Gold500.copy(alpha = 0.25f),
                            contentColor = Gold300
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.VpnKey, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = if (isAr) "كود السيادة" else "Master Key", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Program Approval Governance Queue
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Navy800,
                border = BorderStroke(1.dp, Slate700),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PublishedWithChanges, contentDescription = null, tint = Cyan400, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isAr) "حوكمة النشر والاعتماد للعملاء" else "Client Publishing Governance",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                            )
                        }
                        Text(
                            text = "${programsList.size} ${if (isAr) "منظومة" else "Programs"}",
                            style = MaterialTheme.typography.labelSmall.copy(color = Slate300)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    programsList.take(4).forEach { program ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isAr) program.titleAr else program.titleEn,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color.White),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${if (isAr) program.sectorType.labelAr else program.sectorType.labelEn} • @${program.generatedUsername}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Slate400, fontSize = 11.sp)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (program.isApprovedForClients) GreenSuccess.copy(alpha = 0.15f) else Gold500.copy(alpha = 0.15f),
                                    border = BorderStroke(0.5.dp, if (program.isApprovedForClients) GreenSuccess else Gold400),
                                    modifier = Modifier.clickable { onToggleApproval(program) }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (program.isApprovedForClients) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = null,
                                            tint = if (program.isApprovedForClients) GreenSuccess else Gold400,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (program.isApprovedForClients) (if (isAr) "معتمد" else "Visible") else (if (isAr) "محجوب" else "Hidden"),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (program.isApprovedForClients) GreenSuccess else Gold400,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                        HorizontalDivider(color = Slate800, modifier = Modifier.padding(vertical = 2.dp))
                    }
                }
            }
        }

        // Live Real-Time Audit Log Summary
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Navy800,
                border = BorderStroke(1.dp, Slate700),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SecurityUpdateGood, contentDescription = null, tint = GreenSuccess, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isAr) "سجل الرقابة والأمن السحابي (Firestore)" else "Security Audit Feed",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                            )
                        }

                        TextButton(onClick = { onNavigateTab(AppTab.COMMAND_ROLES) }) {
                            Text(text = if (isAr) "عرض الكل" else "View All", color = Cyan400, fontSize = 11.5.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    val displayLogs = if (firestoreAuditLogs.isNotEmpty()) {
                        firestoreAuditLogs.take(4).map { it.actionAr to it.actorName }
                    } else {
                        auditLogs.take(4).map { it.actionAr to it.actorName }
                    }

                    if (displayLogs.isEmpty()) {
                        Text(
                            text = if (isAr) "لا توجد عمليات مسجلة حتى الآن" else "No recent audit events",
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate400)
                        )
                    } else {
                        displayLogs.forEach { (action, actor) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(GreenSuccess)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = action,
                                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White, fontSize = 11.5.sp)
                                    )
                                    Text(
                                        text = "${if (isAr) "المنفذ: " else "By: "}$actor",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Slate400, fontSize = 9.5.sp)
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

// -------------------------------------------------------------------------------------
// 2. END-USER / CLIENT PORTAL VIEW
// -------------------------------------------------------------------------------------
@Composable
private fun EndUserClientPortalView(
    activeUser: UserAccount,
    firestoreProfile: FirestoreUserProfile?,
    language: AppLanguage,
    programsList: List<EnterpriseProgram>,
    industrialProducts: List<IndustrialProduct>,
    industrialOrders: List<IndustrialOrder>,
    onNavigateTab: (AppTab) -> Unit,
    onOpenOrderDialog: (IndustrialProduct) -> Unit,
    onOpenLoginDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAr = language == AppLanguage.ARABIC

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Welcome Client Hero Card
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Navy800,
                border = BorderStroke(1.5.dp, Cyan500),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("client_portal_banner")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Cyan500.copy(alpha = 0.2f))
                                    .border(1.5.dp, Cyan400, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BusinessCenter,
                                    contentDescription = "Client Portal",
                                    tint = Cyan300
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isAr) "بوابة العملاء والمستفيدين" else "Client & Partner Portal",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Cyan300
                                    )
                                )
                                Text(
                                    text = "${if (isAr) "مرحباً بك: " else "Welcome, "}${activeUser.fullName}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Slate300, fontSize = 11.5.sp)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = GreenSuccess.copy(alpha = 0.15f),
                            border = BorderStroke(0.5.dp, GreenSuccess.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = if (isAr) "عميل معتمد" else "VERIFIED CLIENT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = GreenSuccess,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (isAr)
                            "أهلاً بك في البوابة الإلكترونية لشركة الشرق والغرب العالمية. يمكنك استعراض المنظومات المعتمدة، طلب المعدات الصناعية الألمانية LK-W، وتتبع مسار التوريد بأعلى معايير الدقة."
                        else
                            "Welcome to the East & West Global enterprise portal. Explore approved digital automation programs, request genuine LK-W industrial hardware, and track logistics in real-time.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Slate200,
                            lineHeight = 18.sp,
                            fontSize = 12.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onNavigateTab(AppTab.INDUSTRIAL_LKW) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Cyan500,
                                contentColor = Navy900
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = if (isAr) "طلب توريد صناعي" else "Order LK-W Hardware", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { onNavigateTab(AppTab.PROGRAMS) },
                            border = BorderStroke(1.dp, Slate700),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = if (isAr) "دليل المنظومات" else "Browse Catalog", fontSize = 11.5.sp)
                        }
                    }
                }
            }
        }

        // Approved Industrial Hardware LK-W Showcase
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PrecisionManufacturing, contentDescription = null, tint = Gold400, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAr) "المعدات والأنظمة الصناعية المعتمدة (LK-W)" else "Approved LK-W Industrial Systems",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Gold400)
                        )
                    }

                    TextButton(onClick = { onNavigateTab(AppTab.INDUSTRIAL_LKW) }) {
                        Text(text = if (isAr) "الكتالوج الكامل" else "View All", color = Cyan400, fontSize = 11.5.sp)
                    }
                }

                if (industrialProducts.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Navy800,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isAr) "لا توجد منتجات معروضة حالياً" else "No products published at the moment",
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate400, textAlign = TextAlign.Center),
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        items(industrialProducts, key = { it.id }) { product ->
                            ClientProductMiniCard(
                                product = product,
                                language = language,
                                onOrder = { onOpenOrderDialog(product) }
                            )
                        }
                    }
                }
            }
        }

        // Active Orders & Logistics Tracking
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Navy800,
                border = BorderStroke(1.dp, Slate700),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = GreenSuccess, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isAr) "تتبع الشحنات وطلبات التوريد" else "Logistics & Order Tracking",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Slate800
                        ) {
                            Text(
                                text = "${industrialOrders.size} ${if (isAr) "طلبات" else "Orders"}",
                                style = MaterialTheme.typography.labelSmall.copy(color = Slate300),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (industrialOrders.isEmpty()) {
                        Text(
                            text = if (isAr) "لم تقم بتقديم أي طلبات توريد بعد. يمكنك النقر على 'طلب توريد صناعي' للبدء."
                            else "No active orders submitted yet. Click 'Order LK-W Hardware' to submit a supply request.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate400, fontSize = 11.5.sp),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        industrialOrders.take(3).forEach { order ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${order.productCode} (${if (isAr) order.productNameAr else order.productNameEn}) x${order.quantity}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                                    )
                                    Text(
                                        text = "${if (isAr) "وجهة التسليم: " else "Destination: "}${order.deliveryLocation} • ${order.estimatedDeliveryDate}",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Slate400, fontSize = 10.sp)
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(order.status.colorHex).copy(alpha = 0.15f),
                                    border = BorderStroke(0.5.dp, Color(order.status.colorHex).copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = if (isAr) order.status.labelAr else order.status.labelEn,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(order.status.colorHex),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.5.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.5.dp)
                                    )
                                }
                            }
                            HorizontalDivider(color = Slate800, modifier = Modifier.padding(vertical = 2.dp))
                        }
                    }
                }
            }
        }

        // Certified Enterprise Programs Catalog
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Navy800,
                border = BorderStroke(1.dp, Slate700),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = Cyan400, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isAr) "المنظومات الذكية المعتمدة للعميل" else "Client Certified Enterprise Systems",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                            )
                        }

                        TextButton(onClick = { onNavigateTab(AppTab.PROGRAMS) }) {
                            Text(text = if (isAr) "استعراض الكل" else "Explore", color = Cyan400, fontSize = 11.5.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    programsList.take(3).forEach { program ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Cyan500.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.SmartToy, contentDescription = null, tint = Cyan300, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isAr) program.titleAr else program.titleEn,
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                                )
                                Text(
                                    text = if (isAr) program.descriptionAr else program.descriptionEn,
                                    style = MaterialTheme.typography.labelSmall.copy(color = Slate400, fontSize = 10.sp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        HorizontalDivider(color = Slate800, modifier = Modifier.padding(vertical = 2.dp))
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------
// HELPER SUB-COMPONENTS
// -------------------------------------------------------------------------------------
@Composable
private fun AdminStatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Navy900,
        border = BorderStroke(1.dp, Slate700),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(color = Slate300, fontSize = 10.5.sp)
                )
                Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(color = accentColor, fontSize = 9.5.sp)
            )
        }
    }
}

@Composable
private fun ClientProductMiniCard(
    product: IndustrialProduct,
    language: AppLanguage,
    onOrder: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAr = language == AppLanguage.ARABIC

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Navy900,
        border = BorderStroke(1.dp, Slate700),
        modifier = modifier.width(200.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = product.modelCode,
                    style = MaterialTheme.typography.labelSmall.copy(color = Gold400, fontWeight = FontWeight.Bold)
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = GreenSuccess.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${product.inStockQuantity} ${if (isAr) "متوفر" else "Units"}",
                        style = MaterialTheme.typography.labelSmall.copy(color = GreenSuccess, fontSize = 8.5.sp),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isAr) product.nameAr else product.nameEn,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Color.White),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "$%,.0f USD".format(product.estimatedPriceUsd),
                style = MaterialTheme.typography.titleSmall.copy(color = Cyan300, fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onOrder,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold500,
                    contentColor = Navy900
                ),
                shape = RoundedCornerShape(6.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (isAr) "طلب توريد" else "Request Order", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
