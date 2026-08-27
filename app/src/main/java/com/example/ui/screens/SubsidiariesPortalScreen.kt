package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AppLanguage
import com.example.data.model.RoleRank
import com.example.data.model.UserAccount
import com.example.ui.components.WathqEnterpriseVerificationDialog
import com.example.ui.theme.*

/**
 * Data Model for Subsidiary Company
 */
data class SubsidiaryCompany(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val typeAr: String,
    val typeEn: String,
    val descriptionAr: String,
    val descriptionEn: String,
    val icon: ImageVector,
    val badgeColor: Color,
    val statsAr: String,
    val statsEn: String,
    val catalogItemsAr: List<String>,
    val catalogItemsEn: List<String>,
    val departmentsAr: List<String>,
    val departmentsEn: List<String>
)

/**
 * Screen: استعراض الشركات التابعة (Subsidiaries Showcase & Partners Portal)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubsidiariesPortalScreen(
    language: AppLanguage,
    onReturnToGateway: () -> Unit,
    onToggleLanguage: () -> Unit,
    onEnterOrganizerMode: () -> Unit,
    onSwitchUser: (UserAccount) -> Unit,
    modifier: Modifier = Modifier
) {
    val isAr = language == AppLanguage.ARABIC
    val context = LocalContext.current

    var selectedStaffCompany by remember { mutableStateOf<SubsidiaryCompany?>(null) }
    var selectedClientCompany by remember { mutableStateOf<SubsidiaryCompany?>(null) }
    var selectedExecutiveContact by remember { mutableStateOf<Pair<String, String>?>(null) }
    var showWathqVerificationDialog by remember { mutableStateOf(false) }

    val subsidiaries = remember {
        listOf(
            SubsidiaryCompany(
                id = "sub_factory_industrial",
                nameAr = "شركة مصنع الشرق والغرب للصناعة",
                nameEn = "East & West Industrial Factory Co.",
                typeAr = "الصناعة والتصنيع المتطور • عتاد LK-W",
                typeEn = "Advanced Industrial & Machinery • LK-W",
                descriptionAr = "تصنيع خطوط الإنتاج والعتاد الصناعي الثقيل، هندسة الميكنة والأتمتة، قطع الغيار الصناعية وسلاسل التوريد اللوجستية المتكاملة.",
                descriptionEn = "Heavy industrial machinery manufacturing, production automation, high-spec spare parts, and integrated global supply lines.",
                icon = Icons.Default.PrecisionManufacturing,
                badgeColor = Gold400,
                statsAr = "٢٤ خط إنتاج • معايير ISO-9001 • فحص آلي",
                statsEn = "24 Production Lines • ISO-9001 • Automated QA",
                catalogItemsAr = listOf(
                    "وحدة التحكم والتشغيل الآلي LK-W400 (سيرفو هيدروليكي)",
                    "محركات التوربين الصناعي عالي الكفاءة 75kW",
                    "لوحات التوزيع الكهربائي المحصنة IP67",
                    "قطع غيار سبائك الفولاذ المعالج حرارياً"
                ),
                catalogItemsEn = listOf(
                    "LK-W400 Automated Control Unit (Hydraulic Servo)",
                    "High-Efficiency 75kW Industrial Turbine Motors",
                    "IP67 Hardened Electrical Distribution Panels",
                    "Heat-Treated Alloy Steel Industrial Spare Parts"
                ),
                departmentsAr = listOf("الإدارة العامة والمصنع", "الهندسة والإنتاج", "سلاسل الإمداد والتوريد", "الجودة والسلامة المهنية"),
                departmentsEn = listOf("General Factory Admin", "Engineering & Production", "Supply Chain & Logistics", "QA & Safety")
            ),
            SubsidiaryCompany(
                id = "sub_rafiq_alsanad",
                nameAr = "شركة رفيق السند لتجارة الجملة والتجزئة",
                nameEn = "Rafeeq Al-Sanad Wholesale & Retail Co.",
                typeAr = "التجارة وسلاسل التوزيع الكبرى",
                typeEn = "Wholesale & Retail Commerce Distribution",
                descriptionAr = "توريد وتوزيع البضائع والمنتجات التجارية بالجملة للشركات والتجزئة للمستهلكين، إدارة المستودعات المركزية وسلاسل النقل السريع.",
                descriptionEn = "Large-scale commercial goods supply, B2B wholesale contracting, B2C retail distribution, and central warehousing logistics.",
                icon = Icons.Default.Storefront,
                badgeColor = Cyan400,
                statsAr = "١٥ مركز توزيع • تغطية إقليمية شاملة • أسطول شحن",
                statsEn = "15 Distribution Hubs • Regional Coverage • Freight Fleet",
                catalogItemsAr = listOf(
                    "عقود التوريد التجاري المفتوح للشركات والمؤسسات",
                    "بضائع وسلع استهلاكية معتمدة بالجملة",
                    "خدمات التخزين المبرد والجاف والشحن السريع",
                    "حزم المنتجات التجارية للمتاجر ومنافذ البيع"
                ),
                catalogItemsEn = listOf(
                    "Enterprise B2B Wholesale Supply Contracts",
                    "Certified Wholesale Consumer Goods & Merchandise",
                    "Climate-Controlled Warehousing & Express Shipping",
                    "Retail Packaging Bundles for Store Outlets"
                ),
                departmentsAr = listOf("الإدارة التجارية والمبيعات", "إدارة المستودعات المركزية", "علاقات العملاء والشركاء", "المحاسبة والتحصيل"),
                departmentsEn = listOf("Commercial Sales Admin", "Central Warehouse Logistics", "Client & Partner Relations", "Accounting & Billing")
            ),
            SubsidiaryCompany(
                id = "sub_qimmat_aldir",
                nameAr = "شركة قمة الدرع للحلويات والمكسرات",
                nameEn = "Qimmat Al-Dir' Sweets & Nuts Co.",
                typeAr = "صناعة الأغذية الفاخرة والمكسرات",
                typeEn = "Luxury Confectionery & Premium Roasted Nuts",
                descriptionAr = "إنتاج وتعبئة وتوزيع أفخر أنواع الحلويات الشرقية والغربية والمكسرات المحمصة الطازجة، وتوريد المناسبات والفنادق والمنافذ المعتمدة.",
                descriptionEn = "Production, gourmet packaging, and distribution of premium oriental and international sweets, roasted nuts, hotel hospitality supply, and luxury outlets.",
                icon = Icons.Default.Cake,
                badgeColor = Color(0xFFF59E0B),
                statsAr = "أعلى معايير الجودة الغذائية • مكونات طبيعية ١٠٠٪ • تعبئة فاخرة",
                statsEn = "Top Food Safety Standards • 100% Natural • Luxury Packaging",
                catalogItemsAr = listOf(
                    "تشكيلة الحلويات الملكية الفاخرة للضيافة والمناسبات",
                    "مكسرات مشكلة محمصة ممتازة (عبوات فاخرة ومفرغة من الهواء)",
                    "عقود توريد الفنادق وصالات كبار الشخصيات",
                    "علب الهدايا التراثية والمبتكرة المخصصة"
                ),
                catalogItemsEn = listOf(
                    "Royal Confectionery Selection for Luxury Hospitality",
                    "Premium Roasted Mixed Nuts (Vacuum-Sealed Gift Packs)",
                    "Hotel & VIP Lounge Continuous Supply Contracts",
                    "Customized Heritage Gift Boxes & Gourmet Assortments"
                ),
                departmentsAr = listOf("إدارة العمليات والإنتاج الغذائي", "المبيعات والضيافة الفندقية", "ضبط الجودة والمواصفات", "التسويق والمعارض"),
                departmentsEn = listOf("Food Operations & Production", "Hospitality & Corporate Sales", "Food Quality Standards", "Marketing & Outlets")
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isAr) "استعراض الشركات التابعة" else "Subsidiaries Showcase",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = if (isAr) "مجموعة الشرق والغرب العالمية" else "East & West Global Group",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Gold400,
                                fontSize = 11.sp
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onReturnToGateway,
                        modifier = Modifier.testTag("btn_back_to_gateway_from_subsidiaries")
                    ) {
                        Icon(
                            imageVector = if (isAr) Icons.AutoMirrored.Filled.ArrowBack else Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Gateway",
                            tint = Gold400
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onToggleLanguage,
                        modifier = Modifier.testTag("btn_toggle_lang_subsidiaries")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Toggle Language",
                            tint = Cyan300
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Navy900
                )
            )
        },
        containerColor = Navy900
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // =========================================================================
            // 1. GOLDEN WELCOME BANNER: (مرحبا بكم يا شركاء النجاح)
            // =========================================================================
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Navy900,
                    border = BorderStroke(
                        2.dp,
                        Brush.linearGradient(
                            listOf(
                                Gold300,
                                Gold500,
                                Color(0xFFFFE082),
                                Gold600
                            )
                        )
                    ),
                    shadowElevation = 12.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Gold500.copy(alpha = 0.18f),
                                        Navy900.copy(alpha = 0.95f),
                                        Gold500.copy(alpha = 0.08f)
                                    )
                                )
                            )
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Stars,
                                    contentDescription = null,
                                    tint = Gold300,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isAr) "مرحبا بكم يا شركاء النجاح" else "Welcome, Partners in Success",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Gold300,
                                        fontSize = 22.sp,
                                        letterSpacing = 0.5.sp
                                    ),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.testTag("text_welcome_partners_success")
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.Stars,
                                    contentDescription = null,
                                    tint = Gold300,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Text(
                                text = if (isAr)
                                    "بوابة فروع ومصانع مجموعة الشرق والغرب العالمية • تميز صناعي وتجاري، شراكات ريادية، وتكامل تشغيلي مستدام"
                                else
                                    "East & West Global Group Subsidiaries • Industrial Excellence, Commercial Partnerships & Operational Growth",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Slate200,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(0.92f)
                            )
                        }
                    }
                }
            }

            // =========================================================================
            // 2. DIRECT CONTACT SECTION: (للتواصل المباشر مع: ياسر الرشيدي / شوكت فيتا)
            // =========================================================================
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContactPhone,
                            contentDescription = null,
                            tint = Gold400,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (isAr) "للتواصل المباشر مع القيادة وإدارة الاستثمار:" else "Direct Contact with Leadership & Investment:",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Gold400,
                                fontSize = 15.sp
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Contact 1: ياسر الرشيدي
                        ExecutiveContactCard(
                            name = if (isAr) "ياسر الرشيدي" else "Yasser Al-Rashidi",
                            role = if (isAr) "الرئيس التنفيذي ومؤسس المجموعة • خبير قانوني وتقني" else "Group CEO & Founder • Legal & Tech",
                            phone = "0503026675",
                            email = "yasiralcasr@gmail.com",
                            badge = if (isAr) "👑 الرئيس التنفيذي" else "👑 CEO",
                            badgeColor = Gold400,
                            avatarRes = R.drawable.img_father_yasser_avatar,
                            isAr = isAr,
                            modifier = Modifier.weight(1f),
                            onCall = {
                                launchDialer(context, "0503026675")
                            },
                            onWhatsApp = {
                                launchWhatsApp(context, "966503026675", if (isAr) "السلام عليكم سعادة الأستاذ ياسر الرشيدي، نتواصل معكم بخصوص شراكات مجموعة الشرق والغرب." else "Hello Mr. Yasser Al-Rashidi, reaching out regarding East & West Global partnerships.")
                            },
                            onEmail = {
                                launchEmail(context, "yasiralcasr@gmail.com", "استفسار شراكة - مجموعة الشرق والغرب العالمية")
                            }
                        )

                        // Contact 2: شوكت فيتا
                        ExecutiveContactCard(
                            name = if (isAr) "شوكت فيتا" else "Shawkat Fita",
                            role = if (isAr) "مدير الاستثمار الأجنبي والعلاقات الدولية" else "Foreign Investment & Global Relations Director",
                            phone = "0500000000",
                            email = "investment@eastwest-global.com",
                            badge = if (isAr) "🌐 الاستثمار الأجنبي" else "🌐 Investment",
                            badgeColor = Cyan400,
                            avatarRes = null,
                            isAr = isAr,
                            modifier = Modifier.weight(1f),
                            onCall = {
                                launchDialer(context, "0500000000")
                            },
                            onWhatsApp = {
                                launchWhatsApp(context, "966500000000", if (isAr) "السلام عليكم الأستاذ شوكت فيتا، نود الاستفسار حول فرص الاستثمار الأجنبي والشراكات." else "Hello Mr. Shawkat Fita, inquiring regarding foreign investment and partnerships.")
                            },
                            onEmail = {
                                launchEmail(context, "investment@eastwest-global.com", "Foreign Investment Inquiry - East & West Global")
                            }
                        )
                    }
                }
            }

            // =========================================================================
            // 2.5 WATHQ GOVERNMENT API & ENTERPRISE INTEGRATION BANNER
            // =========================================================================
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Navy800,
                    border = BorderStroke(
                        1.5.dp,
                        Brush.horizontalGradient(listOf(Cyan400, Gold400, GreenSuccess))
                    ),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showWathqVerificationDialog = true }
                        .testTag("card_wathq_gateway_trigger")
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Cyan500.copy(alpha = 0.2f))
                                        .border(1.dp, Cyan400, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VerifiedUser,
                                        contentDescription = "Wathq",
                                        tint = Cyan300,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = if (isAr) "بوابة الربط والتحقق الحكومي المعتمد (واثق)" else "Wathq Government API Gateway",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 13.5.sp
                                        )
                                    )
                                    Text(
                                        text = if (isAr) "مفتاح أمان معتمد: Trial_App_35278 • 8 خدمات نشطة" else "Active Verified Key: Trial_App_35278 • 8 APIs",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Gold400,
                                            fontSize = 10.5.sp
                                        )
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = GreenSuccess.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, GreenSuccess)
                            ) {
                                Text(
                                    text = if (isAr) "استعلام فوري" else "Instant Query",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = GreenSuccess,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Text(
                            text = if (isAr)
                                "السجل التجاري • الغرفة التجارية • عقود الشركات • التفويض الإلكتروني ومقيم • الوكالات الشرعية • الصكوك العقارية"
                            else
                                "Commercial Registration • Chamber of Commerce • Company Contracts • E-Delegation • Power of Attorney • Real Estate",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Slate300,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        )
                    }
                }
            }

            // =========================================================================
            // 3. SUBSIDIARY COMPANIES LIST (الشركات التابعة مع أزرار الدخول)
            // =========================================================================
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Apartment,
                        contentDescription = null,
                        tint = Cyan300,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = if (isAr) "فروع ومنظومات الشركات التابعة:" else "Group Subsidiary Companies & Portals:",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                    )
                }
            }

            items(subsidiaries, key = { it.id }) { company ->
                SubsidiaryCard(
                    company = company,
                    isAr = isAr,
                    onStaffLogin = { selectedStaffCompany = company },
                    onClientEntry = { selectedClientCompany = company }
                )
            }

            // =========================================================================
            // FOOTER BRANDING
            // =========================================================================
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isAr) "شركة الشرق والغرب العالمية • جميع الحقوق محفوظة © 2026" else "East & West Global Enterprise • All Rights Reserved © 2026",
                        style = MaterialTheme.typography.labelSmall.copy(color = Slate400, fontSize = 10.sp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    // Staff & Management Login Dialog
    selectedStaffCompany?.let { company ->
        SubsidiaryStaffLoginDialog(
            company = company,
            isAr = isAr,
            onDismiss = { selectedStaffCompany = null },
            onConfirmLogin = { dept, employeeName, roleRank ->
                selectedStaffCompany = null
                val staffUser = UserAccount(
                    id = "staff-${company.id.take(8)}",
                    username = "staff_${company.id.take(6)}",
                    fullName = employeeName.ifBlank { if (isAr) "موظف معتمد - ${company.nameAr}" else "Authorized Staff - ${company.nameEn}" },
                    roleRank = roleRank,
                    departmentAr = "${company.nameAr} - $dept",
                    departmentEn = "${company.nameEn} - $dept",
                    assignedCode = "STAFF_${company.id.uppercase().take(8)}",
                    canRead = true,
                    canWrite = true,
                    canExecute = true,
                    isMasterOverride = false
                )
                onSwitchUser(staffUser)
                onEnterOrganizerMode()
            }
        )
    }

    // Client Portal Dialog
    selectedClientCompany?.let { company ->
        SubsidiaryClientPortalDialog(
            company = company,
            isAr = isAr,
            onDismiss = { selectedClientCompany = null },
            onRequestQuote = { product ->
                Toast.makeText(
                    context,
                    if (isAr) "تم إرسال طلب عرض السعر لـ: $product بنجاح!" else "Quote request submitted for: $product successfully!",
                    Toast.LENGTH_LONG
                ).show()
            },
            onContactWhatsApp = {
                launchWhatsApp(
                    context,
                    "966503026675",
                    if (isAr) "مرحباً، أود الاستفسار وطلب عروض الأسعار من: ${company.nameAr}" else "Hello, inquiring about products & catalog of ${company.nameEn}"
                )
            }
        )
    }

    // Wathq Government API Dialog
    if (showWathqVerificationDialog) {
        WathqEnterpriseVerificationDialog(
            isAr = isAr,
            onDismiss = { showWathqVerificationDialog = false }
        )
    }
}

/**
 * Executive Direct Contact Card
 */
@Composable
private fun ExecutiveContactCard(
    name: String,
    role: String,
    phone: String,
    email: String,
    badge: String,
    badgeColor: Color,
    avatarRes: Int?,
    isAr: Boolean,
    modifier: Modifier = Modifier,
    onCall: () -> Unit,
    onWhatsApp: () -> Unit,
    onEmail: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Navy900,
        border = BorderStroke(1.2.dp, badgeColor.copy(alpha = 0.5f)),
        shadowElevation = 6.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Navy800)
                    .border(1.5.dp, badgeColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (avatarRes != null) {
                    Image(
                        painter = painterResource(id = avatarRes),
                        contentDescription = name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = name,
                        tint = badgeColor,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            // Name & Badge
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = badgeColor.copy(alpha = 0.15f),
                    border = BorderStroke(0.5.dp, badgeColor.copy(alpha = 0.4f)),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = badgeColor,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = role,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Slate300,
                    fontSize = 10.sp,
                    lineHeight = 13.sp
                ),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Direct Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Call
                IconButton(
                    onClick = onCall,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(GreenSuccess.copy(alpha = 0.2f))
                        .border(0.8.dp, GreenSuccess, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Call",
                        tint = GreenSuccess,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // WhatsApp
                IconButton(
                    onClick = onWhatsApp,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF25D366).copy(alpha = 0.2f))
                        .border(0.8.dp, Color(0xFF25D366), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "WhatsApp",
                        tint = Color(0xFF25D366),
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Email
                IconButton(
                    onClick = onEmail,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Cyan400.copy(alpha = 0.2f))
                        .border(0.8.dp, Cyan400, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email",
                        tint = Cyan300,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Subsidiary Company Card with the 2 requested action buttons:
 * 1. دخول الموظفين والإدارة
 * 2. دخول العملاء
 */
@Composable
private fun SubsidiaryCard(
    company: SubsidiaryCompany,
    isAr: Boolean,
    onStaffLogin: () -> Unit,
    onClientEntry: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Navy900,
        border = BorderStroke(1.2.dp, company.badgeColor.copy(alpha = 0.6f)),
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // High-Resolution Subsidiary Visual Banner
            val bannerRes = when (company.id) {
                "sub_factory_industrial" -> R.drawable.img_industrial_lkw_hero_1787828609760
                "sub_rafiq_alsanad" -> R.drawable.img_logistics_fleet_1787828655423
                "sub_qimmat_aldir" -> R.drawable.img_food_sweets_nuts_1787828638563
                else -> R.drawable.img_sovereign_headquarters_1787828624581
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
            ) {
                Image(
                    painter = painterResource(id = bannerRes),
                    contentDescription = company.nameEn,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    Navy900.copy(alpha = 0.85f),
                                    Navy900
                                )
                            )
                        )
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = company.badgeColor.copy(alpha = 0.95f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                ) {
                    Text(
                        text = if (isAr) company.typeAr else company.typeEn,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Navy900,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.5.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Navy900,
                                Navy800.copy(alpha = 0.8f)
                            )
                        )
                    )
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
            // Top: Icon + Name + Sector Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(company.badgeColor.copy(alpha = 0.2f))
                        .border(1.2.dp, company.badgeColor, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = company.icon,
                        contentDescription = null,
                        tint = company.badgeColor,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isAr) company.nameAr else company.nameEn,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.5.sp
                        )
                    )
                    Text(
                        text = if (isAr) company.typeAr else company.typeEn,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = company.badgeColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            // Description
            Text(
                text = if (isAr) company.descriptionAr else company.descriptionEn,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Slate300,
                    fontSize = 11.5.sp,
                    lineHeight = 16.sp
                )
            )

            // Operational stats badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Navy800,
                border = BorderStroke(0.5.dp, Slate700)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = GreenSuccess,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAr) company.statsAr else company.statsEn,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Slate200,
                            fontSize = 10.5.sp
                        )
                    )
                }
            }

            HorizontalDivider(color = Slate800, thickness = 0.8.dp)

            // The Two Required Buttons:
            // 1. دخول الموظفين والإدارة
            // 2. دخول العملاء
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Button 1: دخول الموظفين والإدارة
                Button(
                    onClick = onStaffLogin,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Navy800,
                        contentColor = Color.White
                    ),
                    border = BorderStroke(1.dp, Gold400),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_staff_login_${company.id}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Badge,
                            contentDescription = null,
                            tint = Gold400,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAr) "دخول الموظفين والإدارة" else "Staff & Admin Login",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Gold300,
                                fontSize = 11.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Button 2: دخول العملاء
                Button(
                    onClick = onClientEntry,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = company.badgeColor,
                        contentColor = Navy900
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_client_entry_${company.id}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = Navy900,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAr) "دخول العملاء" else "Client Entry",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Navy900,
                                fontSize = 11.5.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
}

/**
 * Modal Dialog: Staff & Management Login for Subsidiary
 */
@Composable
private fun SubsidiaryStaffLoginDialog(
    company: SubsidiaryCompany,
    isAr: Boolean,
    onDismiss: () -> Unit,
    onConfirmLogin: (department: String, employeeName: String, role: RoleRank) -> Unit
) {
    var selectedDept by remember { mutableStateOf(if (isAr) company.departmentsAr.first() else company.departmentsEn.first()) }
    var employeeName by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(RoleRank.SUPERVISOR) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(company.badgeColor.copy(alpha = 0.2f))
                        .border(1.dp, company.badgeColor, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Badge,
                        contentDescription = null,
                        tint = company.badgeColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column {
                    Text(
                        text = if (isAr) "دخول الموظفين والإدارة" else "Staff & Management Entry",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                    )
                    Text(
                        text = if (isAr) company.nameAr else company.nameEn,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = company.badgeColor,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (isAr) "اختر القسم الإداري أو التشغيلي:" else "Select Department:",
                    style = MaterialTheme.typography.bodySmall.copy(color = Slate300, fontWeight = FontWeight.SemiBold)
                )

                // Departments
                val departments = if (isAr) company.departmentsAr else company.departmentsEn
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    departments.forEach { dept ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedDept == dept) company.badgeColor.copy(alpha = 0.2f) else Navy800,
                            border = BorderStroke(
                                1.dp,
                                if (selectedDept == dept) company.badgeColor else Slate700
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedDept = dept }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedDept == dept,
                                    onClick = { selectedDept = dept },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = company.badgeColor,
                                        unselectedColor = Slate400
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = dept,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (selectedDept == dept) Color.White else Slate300,
                                        fontWeight = if (selectedDept == dept) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                            }
                        }
                    }
                }

                // Employee Name input
                OutlinedTextField(
                    value = employeeName,
                    onValueChange = { employeeName = it },
                    label = { Text(if (isAr) "اسم الموظف / المسؤول (اختياري)" else "Employee / Officer Name (Optional)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = company.badgeColor,
                        unfocusedBorderColor = Slate700
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmLogin(selectedDept, employeeName, selectedRole) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold500,
                    contentColor = Navy900
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = if (isAr) "تأكيد الدخول للنظام" else "Confirm Login",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = if (isAr) "إلغاء" else "Cancel",
                    color = Slate400
                )
            }
        },
        containerColor = Navy900,
        shape = RoundedCornerShape(18.dp)
    )
}

/**
 * Modal Dialog: Client Portal for Subsidiary (Catalog, Quotes & Orders)
 */
@Composable
private fun SubsidiaryClientPortalDialog(
    company: SubsidiaryCompany,
    isAr: Boolean,
    onDismiss: () -> Unit,
    onRequestQuote: (String) -> Unit,
    onContactWhatsApp: () -> Unit
) {
    val catalog = if (isAr) company.catalogItemsAr else company.catalogItemsEn

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(company.badgeColor.copy(alpha = 0.2f))
                        .border(1.dp, company.badgeColor, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = company.badgeColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column {
                    Text(
                        text = if (isAr) "بوابة العملاء والطلبات" else "Client & Orders Portal",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                    )
                    Text(
                        text = if (isAr) company.nameAr else company.nameEn,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = company.badgeColor,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (isAr)
                        "استعرض منتجات وخدمات الشركة المتاحة للعملاء، واطلب عرض السعر أو التوريد الفوري:"
                    else
                        "Explore available products & services, request a quote or direct supply contract:",
                    style = MaterialTheme.typography.bodySmall.copy(color = Slate300, fontSize = 11.5.sp)
                )

                // Catalog Items
                catalog.forEach { item ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Navy800,
                        border = BorderStroke(0.8.dp, Slate700),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.5.sp
                                    )
                                )
                                Text(
                                    text = if (isAr) "متوفر للتوريد والطلب المباشر" else "Available for Direct Order",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = GreenSuccess,
                                        fontSize = 9.5.sp
                                    )
                                )
                            }

                            Button(
                                onClick = { onRequestQuote(item) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = company.badgeColor.copy(alpha = 0.2f),
                                    contentColor = company.badgeColor
                                ),
                                border = BorderStroke(0.8.dp, company.badgeColor),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text(
                                    text = if (isAr) "طلب سعر" else "Quote",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onContactWhatsApp,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF25D366),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAr) "محادثة مبيعات فورية (واتساب)" else "Instant Sales WhatsApp",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = if (isAr) "إغلاق" else "Close",
                    color = Slate400
                )
            }
        },
        containerColor = Navy900,
        shape = RoundedCornerShape(18.dp)
    )
}

// =========================================================================
// HELPER INTENT LAUNCHERS
// =========================================================================

private fun launchDialer(context: Context, phoneNumber: String) {
    try {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "الهاتف: $phoneNumber", Toast.LENGTH_SHORT).show()
    }
}

private fun launchWhatsApp(context: Context, phoneWithCountryCode: String, message: String) {
    try {
        val uri = Uri.parse("https://api.whatsapp.com/send?phone=$phoneWithCountryCode&text=${Uri.encode(message)}")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "WhatsApp: $phoneWithCountryCode", Toast.LENGTH_SHORT).show()
    }
}

private fun launchEmail(context: Context, email: String, subject: String) {
    try {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "البريد: $email", Toast.LENGTH_SHORT).show()
    }
}
