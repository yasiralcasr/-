package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.control.AltruismDistributionEvent
import com.example.control.KashefSecurityEngine
import com.example.control.MagicInspectionResult
import com.example.data.api.model.ApiDelegationServiceDto
import com.example.data.api.model.ApiWathqRecordDto
import com.example.data.model.AppLanguage
import com.example.data.model.ContinentKey
import com.example.data.model.UserAccount
import com.example.ui.theme.*

@Composable
fun ContinentsScreen(
    selectedContinent: ContinentKey,
    magicQuery: String,
    magicDetectedUrl: String,
    magicResult: MagicInspectionResult?,
    quarantinedThreats: List<MagicInspectionResult>,
    altruismHistory: List<AltruismDistributionEvent>,
    charityPoolBalance: Double,
    totalRevenueProcessed: Double,
    language: AppLanguage,
    activeUser: UserAccount,
    remoteDelegations: List<ApiDelegationServiceDto> = emptyList(),
    isRemoteApiSyncing: Boolean = false,
    queriedWathqRecord: ApiWathqRecordDto? = null,
    onSelectContinent: (ContinentKey) -> Unit,
    onQueryChange: (String) -> Unit,
    onDetectedUrlChange: (String) -> Unit,
    onProcessMagicWindow: (query: String, url: String) -> Unit,
    onInjectAltruismRevenue: (amount: Double, currency: String) -> Unit,
    onClearQuarantine: () -> Unit,
    onSyncDelegations: () -> Unit = {},
    onQueryWathq: (serviceCode: String, queryNumber: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val isAr = language == AppLanguage.ARABIC
    val context = LocalContext.current

    var injectAmountText by remember { mutableStateOf("150000") }
    var selectedCurrency by remember { mutableStateOf("USD") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Header: Kashef Al-Mastoor & Continents System
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Navy800,
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Gold400),
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
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Gold500.copy(alpha = 0.2f))
                                    .border(1.dp, Gold400, RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Public,
                                    contentDescription = "Continents",
                                    tint = Gold400,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isAr) "قارات وفضاءات كاشف المستور" else "Kashef Ultimate Spatial Continents",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Gold400
                                    )
                                )
                                Text(
                                    text = if (isAr) "فضاءات لامتناهية مستقلة ومحصنة مع النافذة السحرية"
                                    else "Infinite independent secure spaces with Magic Window",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Slate300, fontSize = 11.sp)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Cyan500.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = if (isAr) "٦ قارات مستقلة" else "6 Continents",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Cyan400,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Continents Navigation Bar
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 2.dp)
            ) {
                items(ContinentKey.values()) { continent ->
                    val isSelected = selectedContinent == continent
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) Color(continent.colorHex).copy(alpha = 0.25f) else Navy800,
                        border = androidx.compose.foundation.BorderStroke(
                            if (isSelected) 1.5.dp else 1.dp,
                            if (isSelected) Color(continent.colorHex) else Slate700
                        ),
                        modifier = Modifier
                            .clickable { onSelectContinent(continent) }
                            .testTag("continent_tab_${continent.name}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = continent.icon, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAr) continent.titleAr else continent.titleEn,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color(continent.colorHex) else Slate200,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // Magic Window Inspector (النافذة السحرية والرقيب الحسيب)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Navy800),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Cyan400)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoFixHigh,
                                contentDescription = "Magic Window",
                                tint = Cyan400,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAr) "🔮 النافذة السحرية والرقيب الحسيب" else "🔮 Magic Window & Sovereign Inspector",
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
                                text = if (isAr) "فصل اليمين عن اليسار" else "Right vs Left",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Gold400,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = if (isAr)
                            "افحص أي خدمة أو رابط: الحصون القديمة الراسخة (الراجحي، أبشر، قوى، مدد) تُفتح فوراً في اليمين وتُثبت جلستها، والروابط الحديثة الخبيثة تُقذف فوراً لليسار في السلة السوداء مع الموعظة."
                        else
                            "Test any query or URL: Ancient trusted platforms open permanently to the Right; fraudulent phishing traps are quarantined to the Left.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate300, fontSize = 11.sp)
                    )

                    OutlinedTextField(
                        value = magicQuery,
                        onValueChange = onQueryChange,
                        label = { Text(if (isAr) "اسم الخدمة أو البوابة (مثال: مصرف الراجحي افراد، قوى، أبشر...)" else "Service Query or Portal Name") },
                        placeholder = { Text("مثال: مصرف الراجحي افراد أو رابط مشبوه") },
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { onQueryChange("مصرف الراجحي افراد") }) {
                                Icon(Icons.Default.AccountBalance, contentDescription = "Al Rajhi", tint = Gold400)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Cyan400,
                            unfocusedBorderColor = Slate700,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("magic_window_query_input")
                    )

                    OutlinedTextField(
                        value = magicDetectedUrl,
                        onValueChange = onDetectedUrlChange,
                        label = { Text(if (isAr) "الرابط المرصود (اختياري لاختبار كاشف المستور)" else "Detected URL (Optional)") },
                        placeholder = { Text("https://scam-fake-login.net") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Cyan400,
                            unfocusedBorderColor = Slate700,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val q = magicQuery.ifBlank { "مصرف الراجحي افراد" }
                                onProcessMagicWindow(q, magicDetectedUrl)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Cyan500,
                                contentColor = Navy900
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("process_magic_window_btn")
                        ) {
                            Icon(Icons.Default.FilterCenterFocus, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAr) "فحص النافذة السحرية" else "Inspect in Magic Window",
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                onQueryChange("دخول الراجحي السريع المزور")
                                onDetectedUrlChange("https://scam-alrajhi-login.net/trap")
                                onProcessMagicWindow("دخول الراجحي السريع المزور", "https://scam-alrajhi-login.net/trap")
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("test_scam_trap_btn")
                        ) {
                            Text(
                                text = if (isAr) "تجربة رصد مصيدة خبيثة" else "Test Scam Trap",
                                color = RedDanger,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Magic Inspection Result Banner
                    if (magicResult != null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (magicResult.isApprovedRightPath) Navy900 else RedDanger.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (magicResult.isApprovedRightPath) GreenSuccess else RedDanger
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isAr) magicResult.titleAr else magicResult.titleEn,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (magicResult.isApprovedRightPath) GreenSuccess else RedDanger
                                        )
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (magicResult.isApprovedRightPath) GreenSuccess.copy(alpha = 0.2f) else RedDanger.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = if (magicResult.isApprovedRightPath) {
                                                if (isAr) "➡️ اليمين (الصالحين)" else "➡️ Right (Approved)"
                                            } else {
                                                if (isAr) "⬅️ اليسار (السلة السوداء)" else "⬅️ Left (Quarantine)"
                                            },
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (magicResult.isApprovedRightPath) GreenSuccess else RedDanger,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp
                                            ),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = if (isAr) magicResult.detailsAr else magicResult.detailsEn,
                                    style = MaterialTheme.typography.bodySmall.copy(color = Slate200, fontSize = 11.sp)
                                )

                                Text(
                                    text = "🔗 " + magicResult.targetUrl,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Cyan400,
                                        fontFamily = FontFamily.Monospace
                                    )
                                )

                                // Moral Advisory Box if Quarantined
                                if (!magicResult.isApprovedRightPath && magicResult.moralAdvisoryAr != null) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Navy900,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, RedDanger),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text(
                                                text = if (isAr) "⚠️ الخطاب الوعظي الحاسم:" else "⚠️ Solemn Moral Warning:",
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = RedDanger
                                                )
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = if (isAr) magicResult.moralAdvisoryAr else (magicResult.moralAdvisoryEn ?: ""),
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = Gold300,
                                                    lineHeight = 18.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            )
                                        }
                                    }
                                }

                                if (magicResult.isApprovedRightPath) {
                                    Button(
                                        onClick = {
                                            KashefSecurityEngine.launchUri(context, magicResult.targetUrl)
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = GreenSuccess,
                                            contentColor = Navy900
                                        ),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.Launch, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isAr) "القفز المباشر وتثبيت الجلسة الحصرية" else "Launch Exclusive Session",
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Active Continent Space View
        item {
            when (selectedContinent) {
                ContinentKey.GOVERNMENT_GATE -> {
                    GovernmentGateContinent(
                        language = language,
                        remoteDelegations = remoteDelegations,
                        isRemoteApiSyncing = isRemoteApiSyncing,
                        queriedWathqRecord = queriedWathqRecord,
                        onSyncDelegations = onSyncDelegations,
                        onQueryWathq = onQueryWathq,
                        onOpenUrl = { url -> KashefSecurityEngine.launchUri(context, url) }
                    )
                }
                ContinentKey.CHILDREN_HARBOR -> {
                    ChildrenHarborContinent(language = language)
                }
                ContinentKey.FINANCIAL_MATRIX -> {
                    FinancialMatrixContinent(language = language, onOpenUrl = { url -> KashefSecurityEngine.launchUri(context, url) })
                }
                ContinentKey.KNOWLEDGE_OASIS -> {
                    KnowledgeOasisContinent(language = language)
                }
                ContinentKey.BLACK_BASKET -> {
                    BlackBasketContinent(
                        quarantinedThreats = quarantinedThreats,
                        language = language,
                        onClear = onClearQuarantine
                    )
                }
                ContinentKey.ALTRUISM_OASIS -> {
                    AltruismOasisContinent(
                        charityBalance = charityPoolBalance,
                        totalRevenue = totalRevenueProcessed,
                        history = altruismHistory,
                        language = language,
                        amountText = injectAmountText,
                        onAmountTextChange = { injectAmountText = it },
                        currency = selectedCurrency,
                        onCurrencyChange = { selectedCurrency = it },
                        onInject = { amt, cur -> onInjectAltruismRevenue(amt, cur) }
                    )
                }
            }
        }
    }
}

// 🏛️ 1. Government Gate Continent
@Composable
fun GovernmentGateContinent(
    language: AppLanguage,
    remoteDelegations: List<ApiDelegationServiceDto> = emptyList(),
    isRemoteApiSyncing: Boolean = false,
    queriedWathqRecord: ApiWathqRecordDto? = null,
    onSyncDelegations: () -> Unit = {},
    onQueryWathq: (serviceCode: String, queryNumber: String) -> Unit = { _, _ -> },
    onOpenUrl: (String) -> Unit
) {
    val isAr = language == AppLanguage.ARABIC
    var wathqServiceType by remember { mutableStateOf("CR_COMMERCIAL_REG") }
    var wathqQueryInput by remember { mutableStateOf("1010789456") }

    val portals = listOf(
        Pair("قوى (Qiwa)", "https://qiwa.sa"),
        Pair("أبشر (Absher)", "https://absher.sa"),
        Pair("مدد (Mudad)", "https://mudad.com.sa"),
        Pair("التأمينات (GOSI)", "https://gosi.gov.sa"),
        Pair("ناجز (Najiz)", "https://najiz.sa"),
        Pair("بلدي (Balady)", "https://balady.gov.sa"),
        Pair("الزكاة والضريبة (ZATCA)", "https://zatca.gov.sa"),
        Pair("مقيم (Muqeem)", "https://muqeem.sa"),
        Pair("اعتماد (Etimad)", "https://etimad.sa"),
        Pair("صحة (Seha)", "https://seha.sa")
    )

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Official Portals Launcher Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Navy800),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Gold400)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (isAr) "🏛️ قارة السيادة والمنصات الحكومية الرسمية" else "🏛️ Government Sovereignty Continent",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Gold400)
                )

                Text(
                    text = if (isAr)
                        "الحصون الحكومية القديمة الراسخة المعتمدة زمانياً لخدمة البشر وإنجاز المعاملات السيادية:"
                    else
                        "Established sovereign portals for official civic and institutional governance:",
                    style = MaterialTheme.typography.bodySmall.copy(color = Slate300, fontSize = 11.sp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(portals) { (name, url) ->
                        FilledTonalButton(
                            onClick = { onOpenUrl(url) },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Navy900,
                                contentColor = Gold400
                            ),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Slate700)
                        ) {
                            Icon(Icons.Default.Launch, contentDescription = null, tint = Gold400, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = name, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // 📜 Retrofit E-Delegation & Sovereign Verification Section
        Card(
            colors = CardDefaults.cardColors(containerColor = Navy800),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Cyan400)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Cyan500.copy(alpha = 0.2f))
                                .border(1.dp, Cyan400, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Cyan400, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isAr) "📜 خدمات التفويض والوكالات الموثقة (Retrofit)" else "📜 Verified Delegation & Wathq Gateway",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Cyan400)
                            )
                            Text(
                                text = if (isAr) "مفتاح الأمان: mLj1RiAns8sP... | تم التحقق والتوقيع" else "Security Key: mLj1RiAns8sP... | Signed & Verified",
                                style = MaterialTheme.typography.bodySmall.copy(color = Slate400, fontSize = 10.sp)
                            )
                        }
                    }

                    FilledTonalButton(
                        onClick = onSyncDelegations,
                        enabled = !isRemoteApiSyncing,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Cyan500.copy(alpha = 0.2f),
                            contentColor = Cyan400
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("sync_delegations_retrofit_button")
                    ) {
                        if (isRemoteApiSyncing) {
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), color = Cyan400, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = if (isAr) "تحديث السجلات" else "Sync", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Text(
                    text = if (isAr)
                        "سجلات التفاويض الإلكترونية والوكالات المعتمدة نظامياً المسترجعة من خوادم واجهة المنصة الخارجية الموثقة:"
                    else
                        "Authoritative delegation contracts and sovereign power of attorney records fetched via Retrofit:",
                    style = MaterialTheme.typography.bodySmall.copy(color = Slate300, fontSize = 11.sp)
                )

                // List of Delegations
                if (remoteDelegations.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Navy900,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isAr) "جاري استدعاء سجلات التفويض من API..." else "Fetching delegation records from API...",
                                color = Slate400,
                                fontSize = 12.sp
                            )
                        }
                    }
                } else {
                    remoteDelegations.forEach { delegation ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Navy900,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Slate700),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Gold500.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = delegation.delegationId,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Gold400),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = GreenSuccess.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = delegation.status,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = GreenSuccess),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = if (isAr) "المفوّض: ${delegation.principalName}" else "Principal: ${delegation.principalName}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                                )

                                Text(
                                    text = if (isAr) "المفوّض له: ${delegation.authorizedPerson}" else "Authorized: ${delegation.authorizedPerson}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Cyan400, fontWeight = FontWeight.SemiBold)
                                )

                                Text(
                                    text = if (isAr) "نطاق الصلاحيات: ${delegation.scopeAr}" else "Scope: ${delegation.scopeEn}",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Slate300, fontSize = 11.sp)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (isAr) "المنصة: ${delegation.platformName}" else "Platform: ${delegation.platformName}",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Slate400, fontSize = 10.sp)
                                    )
                                    Text(
                                        text = if (isAr) "الصلاحية: حتى ${delegation.expiryDate}" else "Valid until: ${delegation.expiryDate}",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Gold400, fontSize = 10.sp)
                                    )
                                }

                                if (delegation.verifiedSecuritySignature.isNotBlank()) {
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Navy800,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "🔒 ${delegation.verifiedSecuritySignature}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 9.sp,
                                                color = Slate400
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 🔍 Live Wathq Query & Verification Sub-tool
                HorizontalDivider(color = Slate700, thickness = 0.8.dp)

                Text(
                    text = if (isAr) "🔍 استعلام وثق الفوري (Wathq Verification Service):" else "🔍 Realtime Wathq Verification Query:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Gold400)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = wathqQueryInput,
                        onValueChange = { wathqQueryInput = it },
                        label = { Text(if (isAr) "رقم السجل / الترخيص" else "CR / License Number", fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Gold400,
                            unfocusedBorderColor = Slate700,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("wathq_query_input")
                    )

                    Button(
                        onClick = { onQueryWathq(wathqServiceType, wathqQueryInput) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Gold500,
                            contentColor = Navy900
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(52.dp)
                            .testTag("wathq_query_submit_button")
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if (isAr) "تحقق" else "Verify", fontWeight = FontWeight.Bold)
                    }
                }

                // Display queried result if present
                queriedWathqRecord?.let { record ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Navy900,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GreenSuccess),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = record.titleAr,
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = GreenSuccess)
                                )
                                Text(
                                    text = record.status,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Gold400)
                                )
                            }
                            Text(
                                text = "المنشأة: ${record.entityName} | رقم القيد: ${record.queryNumber}",
                                style = MaterialTheme.typography.bodySmall.copy(color = Slate200, fontSize = 11.sp)
                            )
                            Text(
                                text = "صلاحية السجل: من ${record.issueDate} إلى ${record.expiryDate}",
                                style = MaterialTheme.typography.labelSmall.copy(color = Slate400, fontSize = 10.sp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// 👶 2. Children Harbor Continent
@Composable
fun ChildrenHarborContinent(language: AppLanguage) {
    val isAr = language == AppLanguage.ARABIC

    val pillars = listOf(
        Pair("🛡️ حفظ الفطرة النقية", "صيانة قلوب وعقول النشء من أي محتوى مشوه أو ملوث للأخلاق."),
        Pair("📖 بث القيم والفضائل", "ترسيخ بر الوالدين، الصدق، الأمانة، وعزة النفس في سن مبكرة."),
        Pair("🚀 برامج تعليمية هادفة", "تطوير مهارات الابتكار والبرمجة والعلوم الطبيعية واللغة العربية الفصحى."),
        Pair("🔒 الرقابة الأبوية الحصينة", "حظر الإعلانات المتطفلة، وحجب أي مسارات خبيثة قبل وصولها للأجيال.")
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = Navy800),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = if (isAr) "👶 قارة الأجيال والطفولة الآمنة" else "👶 Children & Generational Safe Harbor",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
            )

            Text(
                text = if (isAr)
                    "فضاء نقي مكرّس لحماية الأجيال القادمة وبث القيم النبيلة وحفظ الفطرة والتعليم النافع:"
                else
                    "Pure digital harbor dedicated to preserving innocence, noble values, and uplifting education:",
                style = MaterialTheme.typography.bodySmall.copy(color = Slate300, fontSize = 11.sp)
            )

            pillars.forEach { (title, desc) ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Navy900,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(text = title, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8)))
                        Text(text = desc, style = MaterialTheme.typography.bodySmall.copy(color = Slate200, fontSize = 11.sp))
                    }
                }
            }
        }
    }
}

// 💼 3. Financial Matrix Continent
@Composable
fun FinancialMatrixContinent(
    language: AppLanguage,
    onOpenUrl: (String) -> Unit
) {
    val isAr = language == AppLanguage.ARABIC

    Card(
        colors = CardDefaults.cardColors(containerColor = Navy800),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = if (isAr) "💼 المجرة المالية والمصرفية الحصرية" else "💼 Financial & Banking Matrix",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
            )

            Text(
                text = if (isAr)
                    "المنافذ المصرفية المعتمدة لحسابات الأفراد والمؤسسات مع القفز السريع المباشر لمصرف الراجحي أفراد والاعتمادات المالية:"
                else
                    "Exclusive direct banking routing to Al Rajhi Individual banking & enterprise settlement:",
                style = MaterialTheme.typography.bodySmall.copy(color = Slate300, fontSize = 11.sp)
            )

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Navy900,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isAr) "🏛️ مصرف الراجحي (حسابات الأفراد)" else "Al Rajhi Bank (Individual Login)",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                        Text(
                            text = "https://alrajhibank.com.sa | منفذ حصري دائم",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF10B981), fontSize = 10.sp)
                        )
                    }

                    Button(
                        onClick = { onOpenUrl("https://alrajhibank.com.sa") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF10B981),
                            contentColor = Navy900
                        ),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(if (isAr) "دخول فوري" else "Launch", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

// 🎬 4. Knowledge Oasis Continent
@Composable
fun KnowledgeOasisContinent(language: AppLanguage) {
    val isAr = language == AppLanguage.ARABIC

    val sections = listOf(
        Pair("📜 وثائقيات التاريخ وعبر الأمم", "استعراض صعود وحضارات الأمم واستخلاص العبر في بناء النهضات الإنسانية."),
        Pair("🔬 سلاسل العلوم الطبيعية والهندسة الصناعية", "أحدث ابتكارات الطاقة المتجددة، تحلية المياه، وتقنيات LK-W المؤتمتة."),
        Pair("🎥 أعمال درامية وتوعوية هادفة", "إنتاج مسلسلات وبرامج تلفزيونية تنشر الوعي وتبني الهوية الأخلاقية.")
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = Navy800),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA855F7))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = if (isAr) "🎬 واحة المعرفة والوثائقيات الهادفة" else "🎬 Knowledge Oasis & Meaningful Media",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFFA855F7))
            )

            sections.forEach { (title, desc) ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Navy900,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(text = title, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFFA855F7)))
                        Text(text = desc, style = MaterialTheme.typography.bodySmall.copy(color = Slate200, fontSize = 11.sp))
                    }
                }
            }
        }
    }
}

// 🕳️ 5. Black Basket Continent (السلة السوداء)
@Composable
fun BlackBasketContinent(
    quarantinedThreats: List<MagicInspectionResult>,
    language: AppLanguage,
    onClear: () -> Unit
) {
    val isAr = language == AppLanguage.ARABIC

    Card(
        colors = CardDefaults.cardColors(containerColor = Navy800),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, RedDanger)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Dangerous, contentDescription = "Quarantine", tint = RedDanger)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAr) "🕳️ السلة السوداء (حجر كاشف المستور)" else "🕳️ Black Basket Quarantine",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = RedDanger)
                    )
                }

                if (quarantinedThreats.isNotEmpty()) {
                    TextButton(onClick = onClear) {
                        Text(if (isAr) "تطهير السلة" else "Clear", color = RedDanger, fontSize = 11.sp)
                    }
                }
            }

            Text(
                text = if (isAr)
                    "هنا يُعزل كل مخادع خبيث وموقع تصيد منشأ حديثاً بهدف سلب حقوق البشر دون وجه حق، ليبقى شره معزولاً عن الصالحين مع توجيه الخطاب الوعظي:"
                else
                    "Quarantine isolation repository for fraudulent and phishing websites targeting user credentials:",
                style = MaterialTheme.typography.bodySmall.copy(color = Slate300, fontSize = 11.sp)
            )

            if (quarantinedThreats.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Navy900,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isAr) "السلة السوداء نظيفة ولا توجد تهديدات معزولة حالياً." else "Black basket is clear. No active threats.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate300, textAlign = TextAlign.Center),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                quarantinedThreats.forEach { threat ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Navy900,
                        border = androidx.compose.foundation.BorderStroke(1.dp, RedDanger.copy(alpha = 0.6f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "🚨 " + if (isAr) threat.titleAr else threat.titleEn,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = RedDanger)
                            )
                            Text(
                                text = threat.targetUrl,
                                style = MaterialTheme.typography.labelSmall.copy(color = Cyan400, fontFamily = FontFamily.Monospace)
                            )
                            Text(
                                text = if (isAr) threat.detailsAr else threat.detailsEn,
                                style = MaterialTheme.typography.bodySmall.copy(color = Slate200, fontSize = 11.sp)
                            )

                            if (threat.moralAdvisoryAr != null) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = RedDanger.copy(alpha = 0.15f),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp)
                                ) {
                                    Text(
                                        text = "📖 " + (if (isAr) threat.moralAdvisoryAr else (threat.moralAdvisoryEn ?: "")),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Gold300,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        modifier = Modifier.padding(8.dp)
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

// 🌊 6. Altruism Oasis Continent (صندوق العطاء والتحلية)
@Composable
fun AltruismOasisContinent(
    charityBalance: Double,
    totalRevenue: Double,
    history: List<AltruismDistributionEvent>,
    language: AppLanguage,
    amountText: String,
    onAmountTextChange: (String) -> Unit,
    currency: String,
    onCurrencyChange: (String) -> Unit,
    onInject: (Double, String) -> Unit
) {
    val isAr = language == AppLanguage.ARABIC

    Card(
        colors = CardDefaults.cardColors(containerColor = Navy800),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF06B6D4))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🌊", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAr) "صندوق العطاء والتحلية التلقائي" else "The Altruism & Desalination Pool",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF06B6D4))
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFF06B6D4).copy(alpha = 0.2f)
                ) {
                    Text(
                        text = "33% Auto-Desalination",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF06B6D4),
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = if (isAr)
                    "نظام اقتطاع ثلث الأرباح وعوائد التوريد الصناعي لـ LK-W تلقائياً وتوزيعها وتسييلها لإخواننا والمحتاجين ليفيض بالخير كبحر ماء محلى."
                else
                    "Autonomous 33% revenue charity allocation channeling clean water and relief distribution.",
                style = MaterialTheme.typography.bodySmall.copy(color = Slate300, fontSize = 11.sp)
            )

            // Balances Dashboard
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Navy900,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF06B6D4)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$%,.0f".format(charityBalance),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF06B6D4))
                        )
                        Text(
                            text = if (isAr) "رصيد العطاء والتحلية" else "Charity Pool",
                            style = MaterialTheme.typography.labelSmall.copy(color = Slate300, fontSize = 10.sp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Navy900,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Gold400),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$%,.0f".format(totalRevenue - charityBalance),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Gold400)
                        )
                        Text(
                            text = if (isAr) "الرصيد المستدام (67%)" else "Retained Pool (67%)",
                            style = MaterialTheme.typography.labelSmall.copy(color = Slate300, fontSize = 10.sp)
                        )
                    }
                }
            }

            // Injection Simulator Form
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Navy900,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isAr) "محاكي تدفق العوائد واقتطاع الثلث للتحلية:" else "Revenue Flow & 33% Desalination Injection:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF06B6D4))
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = amountText,
                            onValueChange = onAmountTextChange,
                            label = { Text(if (isAr) "مبلغ الإيراد أو العقد" else "Contract Amount") },
                            singleLine = true,
                            modifier = Modifier.weight(1.5f)
                        )

                        OutlinedTextField(
                            value = currency,
                            onValueChange = onCurrencyChange,
                            label = { Text(if (isAr) "العملة" else "Currency") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Button(
                        onClick = {
                            val amt = amountText.toDoubleOrNull() ?: 150000.0
                            onInject(amt, currency)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF06B6D4),
                            contentColor = Navy900
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isAr) "تحلية وتوزيع ثلث المبلغ آلياً" else "Process 33% Altruism Injection",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // History
            Text(
                text = if (isAr) "سجل عمليات العطاء والتحلية الأخيرة:" else "Recent Altruism & Desalination Events:",
                style = MaterialTheme.typography.labelMedium.copy(color = Slate300)
            )

            history.take(4).forEach { event ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Navy900,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "🌊 +%,.0f %s (عطاء)".format(event.charityShare, event.currency),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF06B6D4))
                            )
                            Text(
                                text = event.timestamp,
                                style = MaterialTheme.typography.labelSmall.copy(color = Slate300, fontSize = 9.sp)
                            )
                        }
                        Text(
                            text = if (isAr) event.sourceDescriptionAr else event.sourceDescriptionEn,
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate200, fontSize = 10.sp)
                        )
                    }
                }
            }
        }
    }
}
