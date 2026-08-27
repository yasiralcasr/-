package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.example.ui.theme.*

/**
 * Entry portal modes on the Welcome Gateway Screen:
 * 1. GUEST (دخول الضيوف)
 * 2. EMPLOYEE (تسجيل دخول الموظفين)
 * 3. CLIENT (تسجيل دخول العملاء والشركاء)
 */
enum class GatewayEntryTab {
    GUEST,
    EMPLOYEE,
    CLIENT
}

/**
 * Data model representing each subsidiary's custom design identity on the Welcome Gateway.
 */
data class SubsidiaryDesignProfile(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val tagAr: String,
    val tagEn: String,
    val icon: ImageVector,
    val primaryColor: Color,
    val secondaryColor: Color,
    val employeeDescAr: String,
    val employeeDescEn: String,
    val clientDescAr: String,
    val clientDescEn: String,
    val clientServicesAr: List<String>,
    val clientServicesEn: List<String>,
    val departmentsAr: List<String>,
    val departmentsEn: List<String>
)

/**
 * Grand Welcome Gateway Screen (الواجهة الترحيبية الاحترافية الشاملة)
 * Designed for:
 * 1. دخول الضيوف (Guest Portal)
 * 2. تسجيل دخول الموظفين (Employee Login per Subsidiary)
 * 3. تسجيل دخول العملاء والشركاء (Client & Partner Portal per Subsidiary)
 * 4. دخول المنظم والقيادة العليا (Executive Sovereign Key - أ. ياسر الرشيدي)
 */
@Composable
fun WelcomeGatewayScreen(
    language: AppLanguage,
    activeUser: UserAccount,
    isMasterUnlocked: Boolean,
    onEnterGuestPortal: () -> Unit,
    onEnterSubsidiariesPortal: () -> Unit = {},
    onEnterOrganizerEnterprise: () -> Unit,
    onLoginStaffSubsidiary: (String, String, String, String, String, String) -> Unit = { _, _, _, _, _, _ -> },
    onSubmitClientInquiry: (String, String, String, String, String, String, String, String) -> Unit = { _, _, _, _, _, _, _, _ -> },
    onCreateNewUserAccount: (String, String, RoleRank, String, String) -> Unit = { _, _, _, _, _ -> },
    onSwitchUser: (UserAccount) -> Unit = {},
    onToggleLanguage: () -> Unit,
    onOpenMasterKeyDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isAr = language == AppLanguage.ARABIC

    // Active entry category tab
    var selectedEntryTab by remember { mutableStateOf(GatewayEntryTab.GUEST) }

    // Dialog states
    var showDigitalProductsDialog by remember { mutableStateOf(false) }
    var showRegisterUserDialog by remember { mutableStateOf(false) }
    var showShareAppDialog by remember { mutableStateOf(false) }

    // Modal states for subsidiary-specific login / client RFQ
    var activeStaffModalCompany by remember { mutableStateOf<SubsidiaryDesignProfile?>(null) }
    var activeClientModalCompany by remember { mutableStateOf<SubsidiaryDesignProfile?>(null) }

    // Subsidiary Design Profiles (مراعاة التصميم الخاص بكل شركة تابعة)
    val subsidiaryProfiles = remember {
        listOf(
            SubsidiaryDesignProfile(
                id = "sub_factory",
                nameAr = "شركة مصنع الشرق والغرب للصناعة",
                nameEn = "East & West Industrial Factory Co.",
                tagAr = "الصناعة والعتاد الثقيل LK-W",
                tagEn = "Industrial Machinery & LK-W Automation",
                icon = Icons.Default.PrecisionManufacturing,
                primaryColor = Gold400,
                secondaryColor = Color(0xFFD97706),
                employeeDescAr = "بوابة المهندسين، فنيي خطوط الإنتاج، سلاسل الإمداد، وضبط الجودة LK-W",
                employeeDescEn = "Engineering, production lines, supply chain & LK-W QA staff",
                clientDescAr = "طلب عروض أسعار العتاد الصناعي، خطوط الإنتاج الآلية، وقطع الغيار المعالجة",
                clientDescEn = "Heavy machinery quotes, automated production lines & spare parts",
                clientServicesAr = listOf("طلب عرض سعر عتاد LK-W", "استشارة هندسية وميكنة", "طلب قطع غيار سبائك فولاذ", "متابعة خط تصنيع معتمد"),
                clientServicesEn = listOf("LK-W Machinery RFQ", "Engineering Automation Consultation", "Alloy Spare Parts Order", "Production Line Tracking"),
                departmentsAr = listOf("الهندسة والإنتاج الصناعي", "سلاسل الإمداد والتوريد", "إدارة المصنع والتشغيل", "الجودة والسلامة المهنية"),
                departmentsEn = listOf("Industrial Engineering & Production", "Supply Chain & Logistics", "Factory Administration", "QA & Occupational Safety")
            ),
            SubsidiaryDesignProfile(
                id = "sub_rafiq",
                nameAr = "شركة رفيق السند لتجارة الجملة والتجزئة",
                nameEn = "Rafeeq Al-Sanad Wholesale & Retail Co.",
                tagAr = "التجارة الكبرى وسلاسل التوزيع",
                tagEn = "B2B Wholesale & Retail Distribution",
                icon = Icons.Default.Storefront,
                primaryColor = Cyan400,
                secondaryColor = Color(0xFF0284C7),
                employeeDescAr = "بوابة مسؤولي المستودعات المركزية، إدارة المبيعات، المحاسبة، وأسطول الشحن",
                employeeDescEn = "Central warehousing, sales management, accounting & freight fleet",
                clientDescAr = "عقود التوريد التجاري بالجملة للشركات، الشحن السريع، وحزم المتاجر ومنافذ البيع",
                clientDescEn = "B2B enterprise supply contracts, express logistics & retail packaging bundles",
                clientServicesAr = listOf("عقد توريد تجاري مفتوح", "طلب بضائع استهلاكية بالجملة", "خدمات المستودعات والشحن السريع", "حزم المتاجر ومنافذ البيع"),
                clientServicesEn = listOf("Open B2B Supply Contract", "Wholesale Consumer Goods Order", "Warehousing & Express Freight", "Retail Packaging Bundles"),
                departmentsAr = listOf("الإدارة التجارية والمبيعات", "المستودعات المركزية واللوجستيات", "علاقات العملاء والشركاء", "المحاسبة والتحصيل"),
                departmentsEn = listOf("Commercial Sales & Admin", "Central Warehousing & Logistics", "Client & Partner Relations", "Accounting & Billing")
            ),
            SubsidiaryDesignProfile(
                id = "sub_qimmat",
                nameAr = "شركة قمة الدرع للحلويات والمكسرات",
                nameEn = "Qimmat Al-Dir' Sweets & Nuts Co.",
                tagAr = "الحلويات الملكية والمكسرات الفاخرة",
                tagEn = "Luxury Confectionery & Premium Nuts",
                icon = Icons.Default.Cake,
                primaryColor = Color(0xFFF59E0B),
                secondaryColor = Color(0xFFD97706),
                employeeDescAr = "بوابة مسؤولي الإنتاج الغذائي، الضيافة الفندقية، ضبط المواصفات، وإدارة المعارض",
                employeeDescEn = "Gourmet food production, hospitality sales, food standards & outlets",
                clientDescAr = "عقود توريد الفنادق وصالات كبار الشخصيات، تشكيلات الضيافة، وعلب الهدايا التراثية",
                clientDescEn = "Hotel & VIP lounge catering supply, gourmet hospitality & luxury heritage gift boxes",
                clientServicesAr = listOf("عقد توريد فنادق وضيافة كبرى", "طلبية حلويات ملكية للمناسبات", "مكسرات فاخرة مفرغة من الهواء", "علب هدايا وتوزيعات مخصصة"),
                clientServicesEn = listOf("Hotel & Hospitality Contract", "Royal Confectionery Event Order", "Premium Vacuum Nuts Pack", "Customized Gourmet Gift Boxes"),
                departmentsAr = listOf("الإنتاج الغذائي والعمليات", "المبيعات والضيافة الفندقية", "ضبط الجودة والمواصفات", "التسويق والمعارض"),
                departmentsEn = listOf("Food Operations & Production", "Corporate & Hospitality Sales", "Food Quality Standards", "Marketing & Outlets")
            ),
            SubsidiaryDesignProfile(
                id = "sub_hq",
                nameAr = "الإدارة العامة والقيادة التنفيذية",
                nameEn = "Executive Leadership & Group HQ",
                tagAr = "الحوكمة والإشراف السيادي",
                tagEn = "Corporate Governance & Executive HQ",
                icon = Icons.Default.AccountBalance,
                primaryColor = Color(0xFFE2E8F0),
                secondaryColor = Gold400,
                employeeDescAr = "بوابة القيادة العليا، الشؤون القانونية والتقنية، والاستثمار الأجنبي",
                employeeDescEn = "Executive leadership, legal counsel, technology & foreign investment",
                clientDescAr = "الشراكات الاستراتيجية الدولية وعلاقات المستثمرين",
                clientDescEn = "International strategic partnerships & investor relations",
                clientServicesAr = listOf("طلب شراكة استراتيجية", "استثمار وتوسع دولي", "خدمات الاستشارات والحوكمة"),
                clientServicesEn = listOf("Strategic Partnership Request", "International Investment", "Corporate Governance Advisory"),
                departmentsAr = listOf("مكتب الرئيس التنفيذي", "الشؤون القانونية والتقنية", "إدارة الاستثمار الأجنبي", "حوكمة الشركات التابعة"),
                departmentsEn = listOf("Office of the CEO", "Legal & Tech Affairs", "Foreign Investment Directorate", "Subsidiaries Governance")
            )
        )
    }

    // Subtle pulsing animation for executive halo
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Navy900,
                        Navy800,
                        Navy700
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("welcome_gateway_screen")
    ) {
        // Decorative background glow
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Gold500.copy(alpha = 0.12f * glowAlpha),
                        Color.Transparent
                    ),
                    center = center.copy(y = size.height * 0.22f),
                    radius = size.width * 0.85f
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ==========================================
            // TOP ACTION BAR
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Language Switcher
                OutlinedButton(
                    onClick = onToggleLanguage,
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, Slate700),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Navy800.copy(alpha = 0.85f),
                        contentColor = Gold400
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("gateway_lang_toggle")
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Language",
                        tint = Cyan400,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAr) "English" else "العربية",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }

                // Top Right Utility Actions
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Digital Products Button
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Navy800.copy(alpha = 0.85f),
                        border = BorderStroke(1.dp, Brush.horizontalGradient(listOf(Cyan400, Gold400))),
                        modifier = Modifier
                            .clickable { showDigitalProductsDialog = true }
                            .testTag("gateway_digital_products_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Devices,
                                contentDescription = "Digital Products",
                                tint = Cyan300,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = if (isAr) "المنتجات الرقمية" else "Digital Portfolio",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Gold300,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    // Share App Button
                    IconButton(
                        onClick = { showShareAppDialog = true },
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Navy800)
                            .border(1.dp, Gold400.copy(alpha = 0.6f), CircleShape)
                            .testTag("btn_top_share_app")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Gold400,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // CENTRAL GRAND EMBLEM & PRESTIGIOUS HEADER
            // ==========================================
            Box(
                modifier = Modifier
                    .size(86.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Gold500.copy(alpha = 0.25f),
                                Navy900
                            )
                        )
                    )
                    .border(2.dp, Brush.linearGradient(listOf(Gold300, Gold600, Gold400)), CircleShape)
                    .shadow(16.dp, CircleShape, spotColor = Gold500),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_company_logo),
                    contentDescription = "EWG Seal",
                    modifier = Modifier
                        .size(66.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sovereign Status Badge
            Surface(
                shape = RoundedCornerShape(50.dp),
                color = Gold500.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, Gold400.copy(alpha = 0.5f)),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Gold400)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAr) "بوابة الدخول الذكية • مجموعة الشركات والمنظومات" else "Smart Gateway • Group & Subsidiaries Enterprise",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Gold300,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Grand Greeting Header (Explicit User Directive)
            Text(
                text = if (isAr) "أهلاً بكم في تطبيق ياسر الرشيدي الجديد" else "Welcome to the New Yasser Al-Rashidi Application",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 28.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .testTag("welcome_gateway_title")
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isAr)
                    "مجموعة شركة الشرق والغرب العالمية والشركات التابعة"
                else
                    "East & West Global Group & Subsidiaries",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Cyan300,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            // ==========================================
            // 3-WAY SMART ENTRY CATEGORY SELECTOR TABS
            // (1) دخول الضيوف  (2) دخول الموظفين  (3) دخول العملاء
            // ==========================================
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Navy800,
                border = BorderStroke(1.dp, Slate700),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Tab 1: Guest Entry (دخول الضيوف)
                    EntryTabButton(
                        title = if (isAr) "دخول الضيوف" else "Guest Entry",
                        icon = Icons.Outlined.PersonOutline,
                        isSelected = selectedEntryTab == GatewayEntryTab.GUEST,
                        activeColor = Cyan400,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedEntryTab = GatewayEntryTab.GUEST }
                    )

                    // Tab 2: Employee Login (تسجيل دخول الموظفين)
                    EntryTabButton(
                        title = if (isAr) "دخول الموظفين" else "Staff Login",
                        icon = Icons.Default.Badge,
                        isSelected = selectedEntryTab == GatewayEntryTab.EMPLOYEE,
                        activeColor = Gold400,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedEntryTab = GatewayEntryTab.EMPLOYEE }
                    )

                    // Tab 3: Client Login (تسجيل دخول العملاء)
                    EntryTabButton(
                        title = if (isAr) "دخول العملاء" else "Client Portal",
                        icon = Icons.Default.Handshake,
                        isSelected = selectedEntryTab == GatewayEntryTab.CLIENT,
                        activeColor = Color(0xFF38BDF8),
                        modifier = Modifier.weight(1f),
                        onClick = { selectedEntryTab = GatewayEntryTab.CLIENT }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // DYNAMIC PORTAL CONTENT BASED ON ACTIVE TAB
            // ==========================================
            AnimatedContent(
                targetState = selectedEntryTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(200))
                },
                label = "gateway_tab_content"
            ) { targetTab ->
                when (targetTab) {
                    // ----------------------------------------
                    // 1. GUEST PORTAL SECTION (دخول الضيوف)
                    // ----------------------------------------
                    GatewayEntryTab.GUEST -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Primary Guest Access Card
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = Navy800,
                                border = BorderStroke(1.5.dp, Cyan400),
                                shadowElevation = 10.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onEnterGuestPortal() }
                                    .testTag("btn_guest_access_gateway")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(Cyan500.copy(alpha = 0.2f))
                                            .border(1.5.dp, Cyan400, RoundedCornerShape(14.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.PersonOutline,
                                            contentDescription = "Guest",
                                            tint = Cyan300,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(14.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = if (isAr) "بوابة الضيوف والزوار" else "Guest & Public Portal",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White,
                                                    fontSize = 16.sp
                                                )
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = GreenSuccess.copy(alpha = 0.15f)
                                            ) {
                                                Text(
                                                    text = if (isAr) "متاح فوراً" else "ACTIVE",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = GreenSuccess,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 9.sp
                                                    ),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = if (isAr)
                                                "استعراض شامل لخدمات المجموعة، كتالوج المنتجات الصناعية، نبذة عن الشركات، وحساب تكاليف التوريد"
                                            else
                                                "Explore public features, LK-W product catalog, subsidiary overview & direct supply inquiries",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Slate300,
                                                fontSize = 11.5.sp,
                                                lineHeight = 16.sp
                                            )
                                        )
                                    }

                                    Icon(
                                        imageVector = if (isAr) Icons.AutoMirrored.Filled.ArrowBack else Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = Cyan300,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            // Quick Guest Highlights
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                GuestHighlightChip(
                                    icon = Icons.Default.PrecisionManufacturing,
                                    label = if (isAr) "كتالوج العتاد" else "Machinery Catalog",
                                    color = Gold400,
                                    modifier = Modifier.weight(1f),
                                    onClick = onEnterGuestPortal
                                )
                                GuestHighlightChip(
                                    icon = Icons.Default.Apartment,
                                    label = if (isAr) "الشركات التابعة" else "Subsidiaries",
                                    color = Cyan300,
                                    modifier = Modifier.weight(1f),
                                    onClick = onEnterSubsidiariesPortal
                                )
                                GuestHighlightChip(
                                    icon = Icons.Default.ContactPhone,
                                    label = if (isAr) "التواصل المباشر" else "Contact Us",
                                    color = Color(0xFF38BDF8),
                                    modifier = Modifier.weight(1f),
                                    onClick = onEnterGuestPortal
                                )
                            }
                        }
                    }

                    // ----------------------------------------
                    // 2. EMPLOYEE LOGIN SECTION (تسجيل دخول الموظفين)
                    // ----------------------------------------
                    GatewayEntryTab.EMPLOYEE -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = if (isAr) "اختر شركتك التابعة لتسجيل الدخول ككادر وظيفي:" else "Select your subsidiary company to log in as staff:",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = Gold300,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            )

                            // 4 Subsidiary Employee Cards
                            subsidiaryProfiles.forEach { profile ->
                                SubsidiaryEmployeeCard(
                                    profile = profile,
                                    isAr = isAr,
                                    onClick = { activeStaffModalCompany = profile }
                                )
                            }
                        }
                    }

                    // ----------------------------------------
                    // 3. CLIENT PORTAL SECTION (تسجيل دخول العملاء والشركاء)
                    // ----------------------------------------
                    GatewayEntryTab.CLIENT -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = if (isAr) "بوابات العملاء والشركاء التجاريين حسب الشركة التابعة:" else "Client & Partner Portals by Subsidiary Company:",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = Cyan300,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            )

                            // 3 Subsidiary Client Cards (Factory, Rafeeq, Qimmat)
                            subsidiaryProfiles.filter { it.id != "sub_hq" }.forEach { profile ->
                                SubsidiaryClientCard(
                                    profile = profile,
                                    isAr = isAr,
                                    onClick = { activeClientModalCompany = profile }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ==========================================
            // SOVEREIGN EXECUTIVE COMMAND ACCESS (أ. ياسر الرشيدي)
            // ==========================================
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Navy900,
                border = BorderStroke(
                    1.8.dp,
                    Brush.linearGradient(listOf(Gold300, Gold500, Gold600, Gold400))
                ),
                shadowElevation = 12.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .clickable {
                        if (isMasterUnlocked) {
                            onEnterOrganizerEnterprise()
                        } else {
                            onOpenMasterKeyDialog()
                        }
                    }
                    .testTag("btn_organizer_access_gateway")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Gold500.copy(alpha = 0.15f),
                                    Navy900,
                                    Gold500.copy(alpha = 0.10f)
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.radialGradient(
                                        listOf(
                                            Gold500.copy(alpha = 0.35f),
                                            Navy900
                                        )
                                    )
                                )
                                .border(1.8.dp, Gold400, RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Supreme Command",
                                tint = Gold400,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isAr) "دخول المنظم والقيادة العليا" else "Executive Command Access",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Gold300,
                                        fontSize = 16.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Gold500.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = if (isMasterUnlocked) (if (isAr) "👑 مفتوح" else "UNLOCKED") else (if (isAr) "🔒 سيادي" else "MASTER"),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Gold300,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = if (isAr)
                                    "صلاحية الرئيس التنفيذي أ. ياسر الرشيدي لإدارة المنظومات، الصلاحيات، والتوريد LK-W"
                                else
                                    "Supreme Commander & CEO Yasser Al-Rashidi dashboard, LK-W logistics & governance",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Slate200,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Gold500.copy(alpha = 0.2f))
                                .border(1.dp, Gold400, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isAr) Icons.AutoMirrored.Filled.ArrowBack else Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Gold400,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // DUAL ACTION BAR: (1) إنشاء حساب  (2) دليل الشركات
            // ==========================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Register Account Button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Navy800,
                    border = BorderStroke(1.2.dp, Cyan400),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showRegisterUserDialog = true }
                        .testTag("btn_register_new_account_gateway")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "Create Account",
                            tint = Cyan300,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAr) "إنشاء حساب جديد" else "Register Account",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                // Subsidiaries Directory Button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Navy800,
                    border = BorderStroke(1.2.dp, Gold400),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onEnterSubsidiariesPortal() }
                        .testTag("btn_subsidiaries_showcase_gateway")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Apartment,
                            contentDescription = "Subsidiaries",
                            tint = Gold400,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAr) "دليل الشركات التابعة" else "Subsidiaries Guide",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = Gold300,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ==========================================
            // FOOTER & EXECUTIVE LEADERSHIP ATTRIBUTION
            // ==========================================
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Navy800.copy(alpha = 0.65f),
                border = BorderStroke(0.8.dp, Gold500.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isAr)
                            "قيادة وإشراف الرئيس التنفيذي للشركة والشركات التابعة"
                        else
                            "Under the Leadership & Supervision of the CEO of the Group & Subsidiaries",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Gold400,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (isAr)
                            "الأستاذ القانوني والخبير التقني / ياسر الرشيدي\nومدير الاستثمار الأجنبي / شوكت فيتا"
                        else
                            "Legal Counsel & Tech Expert / Yasser Al-Rashidi\nForeign Investment Director / Shawkat Fita",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 18.sp
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (isAr)
                            "البوابة الرقمية الشاملة لإدارة المنظومات في الشركات التابعة"
                        else
                            "Comprehensive Digital Management Portal for Subsidiaries",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Cyan300,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isAr) "شركة الشرق والغرب العالمية • جميع الحقوق محفوظة © 2026" else "East & West Global Enterprise • All Rights Reserved © 2026",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Slate400,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                ),
                textAlign = TextAlign.Center
            )
        }

        // ==========================================
        // DIALOGS & MODAL INTERACTIONS
        // ==========================================

        // 1. Employee Login Dialog for Selected Subsidiary
        activeStaffModalCompany?.let { profile ->
            SubsidiaryStaffLoginDialog(
                profile = profile,
                isAr = isAr,
                onDismiss = { activeStaffModalCompany = null },
                onLogin = { compAr, compEn, deptAr, deptEn, name, pass ->
                    activeStaffModalCompany = null
                    onLoginStaffSubsidiary(compAr, compEn, deptAr, deptEn, name, pass)
                }
            )
        }

        // 2. Client / Partner Portal & RFQ Dialog for Selected Subsidiary
        activeClientModalCompany?.let { profile ->
            SubsidiaryClientInquiryDialog(
                profile = profile,
                isAr = isAr,
                onDismiss = { activeClientModalCompany = null },
                onSubmit = { compAr, compEn, clientName, orgName, phone, email, type, notes ->
                    activeClientModalCompany = null
                    onSubmitClientInquiry(compAr, compEn, clientName, orgName, phone, email, type, notes)
                },
                onExploreCatalog = {
                    activeClientModalCompany = null
                    onEnterSubsidiariesPortal()
                }
            )
        }

        // 3. Digital Products Showcase Dialog
        if (showDigitalProductsDialog) {
            DigitalProductsShowcaseDialog(
                isAr = isAr,
                onDismiss = { showDigitalProductsDialog = false },
                onExploreGuest = {
                    showDigitalProductsDialog = false
                    onEnterGuestPortal()
                },
                onExploreOrganizer = {
                    showDigitalProductsDialog = false
                    onEnterOrganizerEnterprise()
                }
            )
        }

        // 4. Register Account Dialog for Guests & Staff
        if (showRegisterUserDialog) {
            GatewayRegisterAccountDialog(
                isAr = isAr,
                onDismiss = { showRegisterUserDialog = false },
                onCreateUser = { username, fullName, role, deptAr, deptEn ->
                    showRegisterUserDialog = false
                    onCreateNewUserAccount(username, fullName, role, deptAr, deptEn)
                }
            )
        }

        // 5. Share App Dialog
        if (showShareAppDialog) {
            GatewayShareAppDialog(
                isAr = isAr,
                onDismiss = { showShareAppDialog = false },
                onShareLink = { shareText ->
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, if (isAr) "مشاركة رابط تطبيق ياسر الرشيدي" else "Share App Link")
                    context.startActivity(shareIntent)
                }
            )
        }
    }
}

/**
 * Top Segmented Tab Button
 */
@Composable
private fun EntryTabButton(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    activeColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) activeColor.copy(alpha = 0.18f) else Color.Transparent,
        border = BorderStroke(1.dp, if (isSelected) activeColor else Color.Transparent),
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) activeColor else Slate400,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isSelected) Color.White else Slate400,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 11.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Small highlight chip for Guest quick actions
 */
@Composable
private fun GuestHighlightChip(
    icon: ImageVector,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Navy800,
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f)),
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.5.sp
                ),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

/**
 * Subsidiary Employee Card with custom branding & direct staff login button
 */
@Composable
private fun SubsidiaryEmployeeCard(
    profile: SubsidiaryDesignProfile,
    isAr: Boolean,
    onClick: () -> Unit
) {
    val bannerRes = when (profile.id) {
        "sub_factory" -> R.drawable.img_industrial_lkw_hero_1787828609760
        "sub_rafiq" -> R.drawable.img_logistics_fleet_1787828655423
        "sub_qimmat" -> R.drawable.img_food_sweets_nuts_1787828638563
        else -> R.drawable.img_sovereign_headquarters_1787828624581
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Navy800,
        border = BorderStroke(1.2.dp, profile.primaryColor.copy(alpha = 0.65f)),
        shadowElevation = 6.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(85.dp)
            ) {
                Image(
                    painter = painterResource(id = bannerRes),
                    contentDescription = profile.nameEn,
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
                                    Navy800.copy(alpha = 0.85f),
                                    Navy800
                                )
                            )
                        )
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = profile.primaryColor.copy(alpha = 0.95f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Text(
                        text = if (isAr) profile.tagAr else profile.tagEn,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Navy900,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(profile.primaryColor.copy(alpha = 0.18f))
                        .border(1.2.dp, profile.primaryColor, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = profile.icon,
                        contentDescription = profile.nameEn,
                        tint = profile.primaryColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isAr) profile.nameAr else profile.nameEn,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 13.5.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = if (isAr) profile.employeeDescAr else profile.employeeDescEn,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Slate300,
                            fontSize = 10.5.sp,
                            lineHeight = 14.sp
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = profile.primaryColor,
                    modifier = Modifier.clip(RoundedCornerShape(8.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAr) "دخول" else "Login",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Navy900,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Subsidiary Client & Partner Card with customized RFQ/Order entry
 */
@Composable
private fun SubsidiaryClientCard(
    profile: SubsidiaryDesignProfile,
    isAr: Boolean,
    onClick: () -> Unit
) {
    val bannerRes = when (profile.id) {
        "sub_factory" -> R.drawable.img_industrial_lkw_hero_1787828609760
        "sub_rafiq" -> R.drawable.img_logistics_fleet_1787828655423
        "sub_qimmat" -> R.drawable.img_food_sweets_nuts_1787828638563
        else -> R.drawable.img_sovereign_headquarters_1787828624581
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Navy800,
        border = BorderStroke(1.2.dp, profile.primaryColor.copy(alpha = 0.65f)),
        shadowElevation = 6.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(85.dp)
            ) {
                Image(
                    painter = painterResource(id = bannerRes),
                    contentDescription = profile.nameEn,
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
                                    Navy800.copy(alpha = 0.85f),
                                    Navy800
                                )
                            )
                        )
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = profile.primaryColor.copy(alpha = 0.95f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Text(
                        text = if (isAr) "بوابة العملاء" else "Clients",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Navy900,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(profile.primaryColor.copy(alpha = 0.15f))
                            .border(1.2.dp, profile.primaryColor, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = profile.icon,
                            contentDescription = profile.nameEn,
                            tint = profile.primaryColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isAr) profile.nameAr else profile.nameEn,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 13.5.sp
                            )
                        )
                        Text(
                            text = if (isAr) "بوابة العملاء والشركاء التجاريين" else "Client & Partner Portal",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = profile.primaryColor,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp
                            )
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = profile.primaryColor.copy(alpha = 0.18f),
                        border = BorderStroke(1.dp, profile.primaryColor)
                    ) {
                        Text(
                            text = if (isAr) "طلب خدمة" else "Request",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = profile.primaryColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isAr) profile.clientDescAr else profile.clientDescEn,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Slate300,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Quick Service Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val services = if (isAr) profile.clientServicesAr.take(2) else profile.clientServicesEn.take(2)
                    services.forEach { service ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Navy900,
                            border = BorderStroke(0.8.dp, Slate700)
                        ) {
                            Text(
                                text = "• $service",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Slate200,
                                    fontSize = 9.5.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Dialog for Subsidiary Staff / Employee Login
 */
@Composable
private fun SubsidiaryStaffLoginDialog(
    profile: SubsidiaryDesignProfile,
    isAr: Boolean,
    onDismiss: () -> Unit,
    onLogin: (companyNameAr: String, companyNameEn: String, deptAr: String, deptEn: String, staffName: String, passcode: String) -> Unit
) {
    var staffName by remember { mutableStateOf("") }
    var passcode by remember { mutableStateOf("") }
    var selectedDeptIndex by remember { mutableIntStateOf(0) }

    val departments = if (isAr) profile.departmentsAr else profile.departmentsEn

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val deptAr = profile.departmentsAr.getOrElse(selectedDeptIndex) { "الإدارة العامة" }
                    val deptEn = profile.departmentsEn.getOrElse(selectedDeptIndex) { "General Admin" }
                    onLogin(profile.nameAr, profile.nameEn, deptAr, deptEn, staffName, passcode)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = profile.primaryColor,
                    contentColor = Navy900
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = if (isAr) "تسجيل الدخول ومتابعة العمل" else "Login to Workspace",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                border = BorderStroke(1.dp, Slate700),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate300),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = if (isAr) "إلغاء" else "Cancel")
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(profile.primaryColor.copy(alpha = 0.2f))
                        .border(1.dp, profile.primaryColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = profile.icon,
                        contentDescription = null,
                        tint = profile.primaryColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column {
                    Text(
                        text = if (isAr) "دخول كادر الموظفين" else "Staff Member Login",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.5.sp
                        )
                    )
                    Text(
                        text = if (isAr) profile.nameAr else profile.nameEn,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = profile.primaryColor,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Name or Staff ID
                OutlinedTextField(
                    value = staffName,
                    onValueChange = { staffName = it },
                    label = { Text(if (isAr) "اسم الموظف / الرقم الوظيفي" else "Staff Name / ID") },
                    placeholder = { Text(if (isAr) "مثال: م. أحمد أو ياسر الرشيدي" else "e.g. Eng. Ahmed") },
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = profile.primaryColor) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = profile.primaryColor,
                        unfocusedBorderColor = Slate700,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = profile.primaryColor,
                        unfocusedLabelColor = Slate400
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Passcode / Master Code
                OutlinedTextField(
                    value = passcode,
                    onValueChange = { passcode = it },
                    label = { Text(if (isAr) "كلمة المرور / الرمز السري" else "Passcode / PIN") },
                    placeholder = { Text(if (isAr) "أدخل الرمز السري المعتمد" else "Enter assigned passcode") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = profile.primaryColor) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = profile.primaryColor,
                        unfocusedBorderColor = Slate700,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = profile.primaryColor,
                        unfocusedLabelColor = Slate400
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Department Selector
                Text(
                    text = if (isAr) "القسم / الإدارة التابعة:" else "Department / Division:",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = profile.primaryColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp
                    )
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    departments.forEachIndexed { index, dept ->
                        val isSelected = selectedDeptIndex == index
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) profile.primaryColor.copy(alpha = 0.18f) else Navy800,
                            border = BorderStroke(1.dp, if (isSelected) profile.primaryColor else Slate700),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedDeptIndex = index }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedDeptIndex = index },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = profile.primaryColor,
                                        unselectedColor = Slate400
                                    )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = dept,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isSelected) Color.White else Slate300,
                                        fontSize = 11.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                            }
                        }
                    }
                }
            }
        },
        containerColor = Navy900,
        shape = RoundedCornerShape(18.dp)
    )
}

/**
 * Dialog for Subsidiary Client Inquiry / RFQ / Order Submission
 */
@Composable
private fun SubsidiaryClientInquiryDialog(
    profile: SubsidiaryDesignProfile,
    isAr: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (companyNameAr: String, companyNameEn: String, clientName: String, orgName: String, phone: String, email: String, type: String, notes: String) -> Unit,
    onExploreCatalog: () -> Unit
) {
    var clientName by remember { mutableStateOf("") }
    var organizationName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var selectedServiceIndex by remember { mutableIntStateOf(0) }
    var notes by remember { mutableStateOf("") }

    val services = if (isAr) profile.clientServicesAr else profile.clientServicesEn

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val finalClientName = clientName.ifBlank { if (isAr) "عميل معتمد" else "Valued Client" }
                    val serviceType = services.getOrElse(selectedServiceIndex) { "خدمة عامة" }
                    onSubmit(profile.nameAr, profile.nameEn, finalClientName, organizationName, phone, email, serviceType, notes)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = profile.primaryColor,
                    contentColor = Navy900
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = if (isAr) "إرسال طلب التوريد / العرض" else "Submit RFQ / Order",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onExploreCatalog,
                border = BorderStroke(1.dp, Cyan400),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Cyan300),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = if (isAr) "استعراض الكتالوج" else "Browse Catalog")
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(profile.primaryColor.copy(alpha = 0.2f))
                        .border(1.dp, profile.primaryColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Handshake,
                        contentDescription = null,
                        tint = profile.primaryColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column {
                    Text(
                        text = if (isAr) "بوابة العملاء والشركاء" else "Client & Partner Portal",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.5.sp
                        )
                    )
                    Text(
                        text = if (isAr) profile.nameAr else profile.nameEn,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = profile.primaryColor,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Client Name
                OutlinedTextField(
                    value = clientName,
                    onValueChange = { clientName = it },
                    label = { Text(if (isAr) "اسم العميل / مسؤول المشتريات" else "Client / Procurement Lead") },
                    placeholder = { Text(if (isAr) "مثال: م. خالد المنصور" else "e.g. Khalid") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = profile.primaryColor) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = profile.primaryColor,
                        unfocusedBorderColor = Slate700,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = profile.primaryColor,
                        unfocusedLabelColor = Slate400
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Organization / Company Name
                OutlinedTextField(
                    value = organizationName,
                    onValueChange = { organizationName = it },
                    label = { Text(if (isAr) "اسم الشركة أو الجهة الطالبة" else "Enterprise / Company Name") },
                    placeholder = { Text(if (isAr) "مثال: شركة المقاولات الحديثة" else "e.g. Modern Contracting Co.") },
                    leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, tint = profile.primaryColor) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = profile.primaryColor,
                        unfocusedBorderColor = Slate700,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = profile.primaryColor,
                        unfocusedLabelColor = Slate400
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Phone
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(if (isAr) "رقم الهاتف / الواتساب للتواصل" else "Phone / WhatsApp Contact") },
                    placeholder = { Text(if (isAr) "مثال: 0503026675" else "+966 50 302 6675") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = profile.primaryColor) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = profile.primaryColor,
                        unfocusedBorderColor = Slate700,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = profile.primaryColor,
                        unfocusedLabelColor = Slate400
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Service Requested
                Text(
                    text = if (isAr) "نوع الخدمة أو الطلب المطلوب:" else "Requested Service / RFQ:",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = profile.primaryColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp
                    )
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    services.forEachIndexed { index, service ->
                        val isSelected = selectedServiceIndex == index
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) profile.primaryColor.copy(alpha = 0.18f) else Navy800,
                            border = BorderStroke(1.dp, if (isSelected) profile.primaryColor else Slate700),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedServiceIndex = index }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedServiceIndex = index },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = profile.primaryColor,
                                        unselectedColor = Slate400
                                    )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = service,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isSelected) Color.White else Slate300,
                                        fontSize = 11.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                            }
                        }
                    }
                }

                // Additional Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(if (isAr) "ملاحظات إضافية أو مواصفات خاصة" else "Additional Specifications / Notes") },
                    placeholder = { Text(if (isAr) "الكميات المطلوبة، الموقع، وقت التسليم..." else "Quantities, delivery timeframe...") },
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = profile.primaryColor,
                        unfocusedBorderColor = Slate700,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = profile.primaryColor,
                        unfocusedLabelColor = Slate400
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        containerColor = Navy900,
        shape = RoundedCornerShape(18.dp)
    )
}

/**
 * High-Prestige Digital Products Showcase Dialog for East & West Global
 */
@Composable
private fun DigitalProductsShowcaseDialog(
    isAr: Boolean,
    onDismiss: () -> Unit,
    onExploreGuest: () -> Unit,
    onExploreOrganizer: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold500,
                    contentColor = Navy900
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = if (isAr) "إغلاق" else "Close",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onExploreGuest,
                border = BorderStroke(1.dp, Cyan400),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Cyan300
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = if (isAr) "استعراض كزائر" else "Browse as Guest",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Gold500.copy(alpha = 0.2f))
                        .border(1.dp, Gold400, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Devices,
                        contentDescription = null,
                        tint = Gold400,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = if (isAr) "منتجات ومنظومات الشركة الرقمية" else "EWG Digital Products & Platforms",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Gold400,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                    Text(
                        text = if (isAr) "شركة الشرق والغرب العالمية • المحفظة التقنية" else "East & West Global • Technology Portfolio",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Slate300,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (isAr)
                        "تضم منظومة الشرق والغرب العالمية محفظة تقنية متكاملة صُممت بأعلى المعايير السيادية لإدارة التوريد والإنتاج الصناعي والرقابة الذكية:"
                    else
                        "East & West Global encompasses an advanced technology suite engineered for industrial supply automation & sovereign governance:",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                )

                // Product 1: LK-W SCADA Engine
                ProductShowcaseCard(
                    titleAr = "منظومة التحكم والتشغيل الآلي LK-W SCADA",
                    titleEn = "LK-W SCADA Industrial Automation Engine",
                    descAr = "برمجيات مدمجة لإدارة خطوط الإنتاج، المراقبة الحرارية اللحظية، ومطابقة معايير ISO-9001.",
                    descEn = "Embedded SCADA software for heavy industrial lines, thermal telemetry, and ISO-9001 compliance.",
                    badgeAr = "صناعي • هيدروليكي",
                    badgeEn = "Industrial Servo",
                    icon = Icons.Default.PrecisionManufacturing,
                    accentColor = Gold400,
                    isAr = isAr
                )

                // Product 2: Kashef AI Guard
                ProductShowcaseCard(
                    titleAr = "درع كاشف للأمن السيبراني والرقابة",
                    titleEn = "Kashef AI Cybersecurity Shield",
                    descAr = "نظام متقدم للكشف عن التهديدات والأنشطة المشبوهة، تشفير البروتوكولات، وحماية البيانات السيادية.",
                    descEn = "AI security shield for threat isolation, cryptographic TLS 1.3 verification, and audit trail protection.",
                    badgeAr = "سيبراني • حماية متقدمة",
                    badgeEn = "Active Defense",
                    icon = Icons.Default.Shield,
                    accentColor = Cyan400,
                    isAr = isAr
                )

                // Product 3: Altruism Pool
                ProductShowcaseCard(
                    titleAr = "محرك تحلية العوائد والعطاء الإنساني 33%",
                    titleEn = "Altruism 33% Revenue Desalination Engine",
                    descAr = "خوارزمية ذكية لاقتطاع 33% من عوائد العقود الصناعية تلقائياً وتوجيهها للمشاريع الخيرية والتنموية.",
                    descEn = "Automated smart contract engine allocating 33% of industrial revenue directly to humanitarian relief.",
                    badgeAr = "عطاء وإحسان",
                    badgeEn = "Charity Pool",
                    icon = Icons.Default.VolunteerActivism,
                    accentColor = Color(0xFF10B981),
                    isAr = isAr
                )

                // Product 4: Multi-Subsidiary Command Suite
                ProductShowcaseCard(
                    titleAr = "لوحة قيادة الشركات التابعة وحوكمة العقود",
                    titleEn = "Multi-Subsidiary Governance & B2B Hub",
                    descAr = "منصة مركزية لإدارة مصنع الشرق والغرب، رفيق السند، وقمة الدرع تحت إشراف الرئيس التنفيذي.",
                    descEn = "Unified hub governing Factory, Rafeeq Al-Sanad, and Qimmat Al-Dir' under CEO executive supervision.",
                    badgeAr = "حوكمة وإدارة",
                    badgeEn = "Multi-Tenant",
                    icon = Icons.Default.Apartment,
                    accentColor = Gold500,
                    isAr = isAr
                )
            }
        },
        containerColor = Navy900,
        shape = RoundedCornerShape(18.dp)
    )
}

/**
 * Reusable Product Card inside the Digital Showcase Dialog
 */
@Composable
private fun ProductShowcaseCard(
    titleAr: String,
    titleEn: String,
    descAr: String,
    descEn: String,
    badgeAr: String,
    badgeEn: String,
    icon: ImageVector,
    accentColor: Color,
    isAr: Boolean
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Navy800,
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(accentColor.copy(alpha = 0.15f))
                    .border(1.dp, accentColor, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isAr) titleAr else titleEn,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = accentColor.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = if (isAr) badgeAr else badgeEn,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = accentColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            ),
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isAr) descAr else descEn,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Slate300,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                )
            }
        }
    }
}

/**
 * Account Creation Modal Dialog for Guests, Companies Staff, and Executives
 */
@Composable
fun GatewayRegisterAccountDialog(
    isAr: Boolean,
    onDismiss: () -> Unit,
    onCreateUser: (username: String, fullName: String, role: RoleRank, deptAr: String, deptEn: String) -> Unit
) {
    var username by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var selectedOrgType by remember { mutableIntStateOf(0) }
    var selectedRole by remember { mutableStateOf<RoleRank>(RoleRank.OBSERVER) }

    val orgOptions = if (isAr) listOf(
        "حساب ضيف / زائر (مستكشف خدمات)",
        "شركة مصنع الشرق والغرب للصناعة",
        "شركة رفيق السند لتجارة الجملة والتجزئة",
        "شركة قمة الدرع للحلويات والمكسرات",
        "الإدارة العامة وحوكمة المجموعة"
    ) else listOf(
        "Guest / Public Explorer",
        "East & West Industrial Factory Co.",
        "Rafeeq Al-Sanad Wholesale & Retail Co.",
        "Qimmat Al-Diraa Confectionery Co.",
        "Group Executive & Governance HQ"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val finalUsername = if (username.isNotBlank()) username.trim() else "user_${(1000..9999).random()}"
                    val finalFullName = if (fullName.isNotBlank()) fullName.trim() else if (isAr) "مستخدم جديد" else "New Member"
                    val (deptAr, deptEn) = when (selectedOrgType) {
                        0 -> "بوابة الضيوف والزوار" to "Guest & Visitor Portal"
                        1 -> "شركة مصنع الشرق والغرب للصناعة" to "East & West Industrial Factory Co."
                        2 -> "شركة رفيق السند لتجارة الجملة والتجزئة" to "Rafeeq Al-Sanad Wholesale & Retail Co."
                        3 -> "شركة قمة الدرع للحلويات والمكسرات" to "Qimmat Al-Diraa Confectionery Co."
                        else -> "الإدارة العامة والقيادة التنفيذية" to "Executive Leadership & Group HQ"
                    }
                    onCreateUser(finalUsername, finalFullName, selectedRole, deptAr, deptEn)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold500,
                    contentColor = Navy900
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("btn_confirm_register_account")
            ) {
                Text(
                    text = if (isAr) "تأكيد وإنشاء الحساب" else "Confirm & Register",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                border = BorderStroke(1.dp, Slate700),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate300),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = if (isAr) "إلغاء" else "Cancel")
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Cyan500.copy(alpha = 0.2f))
                        .border(1.dp, Cyan400, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = null,
                        tint = Cyan300,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column {
                    Text(
                        text = if (isAr) "إنشاء حساب في المنظومة" else "Register System Account",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                    Text(
                        text = if (isAr) "للضيوف والعملاء وموظفي الشركات التابعة" else "For Guests, Clients & Subsidiary Staff",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Gold400,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Info Banner
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Navy800,
                    border = BorderStroke(0.8.dp, Gold400.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = Gold400,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAr)
                                "تخضع الحسابات المنشأة لإشراف ومتابعة الرئيس التنفيذي أ. ياسر الرشيدي ومسؤولي النظام."
                            else
                                "Accounts created are supervised by CEO Yasser Al-Rashidi & system administrators.",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Slate200,
                                fontSize = 10.5.sp,
                                lineHeight = 14.sp
                            )
                        )
                    }
                }

                // Full Name Input
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text(if (isAr) "الاسم الكامل / المسمى الوظيفي" else "Full Name / Title") },
                    placeholder = { Text(if (isAr) "مثال: م. فهد السبيعي" else "e.g. Eng. Fahad") },
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = Cyan400) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Cyan400,
                        unfocusedBorderColor = Slate700,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Cyan300,
                        unfocusedLabelColor = Slate400
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Username / Email Input
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text(if (isAr) "اسم المستخدم أو البريد الإلكتروني" else "Username or Email") },
                    placeholder = { Text(if (isAr) "مثال: user@eastwest.com" else "e.g. user@domain.com") },
                    leadingIcon = { Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = Gold400) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Gold400,
                        unfocusedBorderColor = Slate700,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Gold300,
                        unfocusedLabelColor = Slate400
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Organization / Affiliation Picker
                Text(
                    text = if (isAr) "الجهة / الشركة التابعة:" else "Organization / Subsidiary:",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Gold300,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    orgOptions.forEachIndexed { index, option ->
                        val isSelected = selectedOrgType == index
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Gold500.copy(alpha = 0.18f) else Navy800,
                            border = BorderStroke(1.dp, if (isSelected) Gold400 else Slate700),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedOrgType = index
                                    selectedRole = when (index) {
                                        0 -> RoleRank.OBSERVER
                                        1, 2, 3 -> RoleRank.SUPERVISOR
                                        else -> RoleRank.GENERAL
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = {
                                        selectedOrgType = index
                                        selectedRole = when (index) {
                                            0 -> RoleRank.OBSERVER
                                            1, 2, 3 -> RoleRank.SUPERVISOR
                                            else -> RoleRank.GENERAL
                                        }
                                    },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = Gold400,
                                        unselectedColor = Slate400
                                    )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = option,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = if (isSelected) Color.White else Slate300,
                                        fontSize = 11.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                )
                            }
                        }
                    }
                }
            }
        },
        containerColor = Navy900,
        shape = RoundedCornerShape(18.dp)
    )
}

/**
 * Share and Download Link Modal Dialog
 */
@Composable
fun GatewayShareAppDialog(
    isAr: Boolean,
    onDismiss: () -> Unit,
    onShareLink: (String) -> Unit
) {
    val shareAppUrl = "https://ais-pre-zj2gz34qlbwfg6pmdvjkzu-672470930952.europe-west2.run.app"
    val defaultShareText = if (isAr) {
        "🌟 تطبيق ياسر الرشيدي الجديد (منظومة شركة الشرق والغرب العالمية والشركات التابعة):\n$shareAppUrl\n\nبوابة السيادة • استعراض الشركات التابعة • بوابة الضيوف والعملاء والموظفين"
    } else {
        "🌟 Yasser Al-Rashidi Enterprise App (East & West Global & Subsidiaries):\n$shareAppUrl"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = { onShareLink(defaultShareText) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold500,
                    contentColor = Navy900
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("btn_confirm_share_app_link")
            ) {
                Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isAr) "إرسال ومشاركة الرابط" else "Share App Link",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                border = BorderStroke(1.dp, Slate700),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate300),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = if (isAr) "إغلاق" else "Close")
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Gold500.copy(alpha = 0.2f))
                        .border(1.dp, Gold400, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = Gold400,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = if (isAr) "رابط مشاركة وتحميل التطبيق" else "Share & Download App Link",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Gold300,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                    Text(
                        text = if (isAr) "للإرسال للضيوف والعملاء والشركات التابعة" else "For Guests, Clients & Subsidiaries",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Slate300,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (isAr)
                        "يمكنك مشاركة هذا الرابط مع الضيوف، العملاء، ومديري وموظفي الشركات التابعة لتشغيل وتجربة التطبيق مباشرة:"
                    else
                        "Share this link with guests, clients, subsidiary executives, and teams to run the application immediately:",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                )

                // URL Display Card
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Navy800,
                    border = BorderStroke(1.dp, Cyan400.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isAr) "الرابط التجريبي المعتمد (Live URL)" else "Official Live URL",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Cyan300,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.5.sp
                                )
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = GreenSuccess.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = if (isAr) "فعال • شغال" else "LIVE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = GreenSuccess,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = shareAppUrl,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                // Super User / Executive Master Access Notice
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Navy800,
                    border = BorderStroke(1.dp, Gold500.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Gold400, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAr) "حساب السيادة للرئيس التنفيذي المعتمد" else "Executive Master Account",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Gold300,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isAr)
                                "البريد الإلكتروني: yasiralcasr@gmail.com\nالرقم السري / كود السيادة: 123456 (أو كود السيادة الخاص)"
                            else
                                "Email: yasiralcasr@gmail.com\nMaster Passcode: 123456",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Slate200,
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp
                            )
                        )
                    }
                }
            }
        },
        containerColor = Navy900,
        shape = RoundedCornerShape(18.dp)
    )
}
