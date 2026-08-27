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
 * Grand Welcome Gateway Screen (الشاشة الترحيبية الرئيسية - قبل المنظومة)
 * Contains the grand greeting for "تطبيق ياسر الرشيدي الجديد",
 * followed by "دخول الضيوف" (Guest Portal), "استعراض الشركات التابعة", "دخول المنظم",
 * "إنشاء حساب جديد", and "مشاركة وتحميل التطبيق".
 */
@Composable
fun WelcomeGatewayScreen(
    language: AppLanguage,
    activeUser: UserAccount,
    isMasterUnlocked: Boolean,
    onEnterGuestPortal: () -> Unit,
    onEnterSubsidiariesPortal: () -> Unit = {},
    onEnterOrganizerEnterprise: () -> Unit,
    onCreateNewUserAccount: (String, String, RoleRank, String, String) -> Unit = { _, _, _, _, _ -> },
    onSwitchUser: (UserAccount) -> Unit = {},
    onToggleLanguage: () -> Unit,
    onOpenMasterKeyDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isAr = language == AppLanguage.ARABIC

    var showDigitalProductsDialog by remember { mutableStateOf(false) }
    var showRegisterUserDialog by remember { mutableStateOf(false) }
    var showShareAppDialog by remember { mutableStateOf(false) }

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
        // Decorative background geometric grid lines & glow
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Gold500.copy(alpha = 0.12f * glowAlpha),
                        Color.Transparent
                    ),
                    center = center.copy(y = size.height * 0.28f),
                    radius = size.width * 0.85f
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar: Language switcher and Digital Products Showcase
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
                        containerColor = Navy800.copy(alpha = 0.8f),
                        contentColor = Gold400
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("gateway_lang_toggle")
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Language",
                        tint = Cyan400,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAr) "English" else "العربية",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                    )
                }

                // Company Digital Products Button
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Navy800.copy(alpha = 0.85f),
                    border = BorderStroke(1.dp, Brush.horizontalGradient(listOf(Cyan400, Gold400))),
                    modifier = Modifier
                        .clickable { showDigitalProductsDialog = true }
                        .testTag("gateway_digital_products_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Devices,
                            contentDescription = "Digital Products",
                            tint = Cyan300,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAr) "منتجات الشركة الرقمية" else "Digital Products",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Gold300,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Central Grand Emblem & Grand Title Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Imperial Golden Emblem
                Box(
                    modifier = Modifier
                        .size(100.dp)
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
                            .size(76.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Prestigious Status Badge
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = Gold500.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, Gold400.copy(alpha = 0.5f)),
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(Gold400)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAr) "المنظومة السيادية للتحكم والأتمتة الذكية • الإصدار المعتمد" else "Supreme Sovereign Automation & Control System",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Gold300,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Grand Greeting Header (Explicit User Directive)
                Text(
                    text = if (isAr) "أهلاً بكم في تطبيق ياسر الرشيدي الجديد" else "Welcome to the New Yasser Al-Rashidi Application",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 22.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 32.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .testTag("welcome_gateway_title")
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Action Portals Section: (1) دخول الضيوف  (2) دخول المنظم
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ==========================================
                // BUTTON 1: دخول الضيوف (Guest Access - Active)
                // ==========================================
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Navy800,
                    border = BorderStroke(1.5.dp, Cyan500.copy(alpha = 0.65f)),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onEnterGuestPortal() }
                        .testTag("btn_guest_access_gateway")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Guest Icon Badge
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Cyan500.copy(alpha = 0.18f))
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

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isAr) "دخول الضيوف" else "Guest Access",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 17.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = GreenSuccess.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = if (isAr) "متاح الآن" else "ACTIVE",
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
                                    "دخول مخصص للزوار والضيوف لاستعراض الخدمات ومساحة العمل الخاصة"
                                else
                                    "Dedicated public workspace for guests and visitors to explore features",
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
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // ==========================================
                // BUTTON 1.5: استعراض الشركات التابعة (Subsidiaries Showcase)
                // ==========================================
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Navy900,
                    border = BorderStroke(
                        1.5.dp,
                        Brush.horizontalGradient(
                            listOf(
                                Gold400,
                                Cyan400,
                                Gold500
                            )
                        )
                    ),
                    shadowElevation = 10.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onEnterSubsidiariesPortal() }
                        .testTag("btn_subsidiaries_showcase_gateway")
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Navy800,
                                        Navy900.copy(alpha = 0.95f),
                                        Navy800
                                    )
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Icon Badge
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                Gold500.copy(alpha = 0.25f),
                                                Cyan400.copy(alpha = 0.2f)
                                            )
                                        )
                                    )
                                    .border(1.5.dp, Gold400, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Apartment,
                                    contentDescription = "Subsidiaries",
                                    tint = Gold400,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (isAr) "استعراض الشركات التابعة" else "Browse Subsidiaries",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Gold300,
                                            fontSize = 16.5.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Gold500.copy(alpha = 0.2f),
                                        border = BorderStroke(0.5.dp, Gold400)
                                    ) {
                                        Text(
                                            text = if (isAr) "✨ شركاء النجاح" else "PARTNERS",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Gold300,
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
                                        "فروع ومصانع المجموعة، بوابات الموظفين والعملاء، والتواصل المباشر مع القيادة"
                                    else
                                        "Group subsidiaries, staff & client portals, and direct executive contact",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Slate200,
                                        fontSize = 11.5.sp,
                                        lineHeight = 16.sp
                                    )
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Gold500.copy(alpha = 0.15f))
                                    .border(1.dp, Gold400.copy(alpha = 0.6f), CircleShape),
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

                // ==========================================
                // BUTTON 2: دخول المنظم (Supreme Sovereign / Organizer Access - Majestic & High Prestige)
                // ==========================================
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Navy900,
                    border = BorderStroke(
                        2.dp,
                        Brush.linearGradient(
                            listOf(
                                Gold300,
                                Gold500,
                                Gold600,
                                Gold400
                            )
                        )
                    ),
                    shadowElevation = 14.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onEnterOrganizerEnterprise() }
                        .testTag("btn_organizer_access_gateway")
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Gold500.copy(alpha = 0.12f),
                                        Navy900,
                                        Gold500.copy(alpha = 0.08f)
                                    )
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Organizer Sovereign Icon Badge
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
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
                                    contentDescription = "Organizer",
                                    tint = Gold400,
                                    modifier = Modifier.size(30.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (isAr) "دخول المنظم والقيادة" else "Organizer & Command Access",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            color = Gold300,
                                            fontSize = 17.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Gold500.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = if (isAr) "👑 سيادي" else "SOVEREIGN",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = Gold300,
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
                                        "لوحة التحكم الإدارية الشاملة، المنظومات الذكية، التوريد LK-W، وإدارة الصلاحيات"
                                    else
                                        "Executive dashboard, 7-tab command suite, LK-W logistics & role governance",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Slate200,
                                        fontSize = 11.5.sp,
                                        lineHeight = 16.sp
                                    )
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Gold500.copy(alpha = 0.2f))
                                    .border(1.dp, Gold400, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isAr) Icons.AutoMirrored.Filled.ArrowBack else Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = Gold400,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // ==========================================
                // DUAL QUICK ACTIONS: (1) إنشاء حساب جديد  (2) مشاركة وتحميل التطبيق
                // ==========================================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Create New Account Button
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Navy800,
                        border = BorderStroke(1.2.dp, Cyan400),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { showRegisterUserDialog = true }
                            .testTag("btn_register_new_account_gateway")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = "Create Account",
                                tint = Cyan300,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAr) "إنشاء حساب جديد" else "Create Account",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp
                                )
                            )
                        }
                    }

                    // Share and Download App Button
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Navy800,
                        border = BorderStroke(1.2.dp, Gold400),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { showShareAppDialog = true }
                            .testTag("btn_share_download_app_gateway")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share App",
                                tint = Gold400,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAr) "مشاركة وتحميل" else "Share & Download",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    color = Gold300,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp)
            ) {
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

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (isAr)
                                "البوابة الرقمية الشاملة لإدارة المنظومات في الشركات"
                            else
                                "The Comprehensive Digital Gateway for Enterprise Systems Management",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Cyan300,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 16.sp
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
        }

        // Digital Products Showcase Modal Dialog
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

        // Register Account Dialog for Guests & Staff
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

        // Share and Download App Dialog
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
                        "تستعرض مجموعة الشرق والغرب العالمية حزمة المنظومات والحلول الرقمية المتكاملة لحوكمة وإدارة وتشغيل الشركات:"
                    else
                        "East & West Global presents its sovereign suite of digital platforms and operational governance solutions:",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                )

                // Product Item 1: Sovereign Core
                DigitalProductItemCard(
                    title = if (isAr) "منظومة السيادة والحوكمة الرقمية (Sovereign Core)" else "Sovereign Governance Core Engine",
                    description = if (isAr)
                        "محرك الرقابة التنفيذية المباشرة، الصلاحيات المشفرة، وإدارة تدقيق الشركات التابعة بالكامل."
                    else
                        "Executive direct governance, encrypted hierarchy, and full audit control across all subsidiaries.",
                    badge = if (isAr) "سيادي • Root" else "Sovereign • Root",
                    icon = Icons.Default.Security,
                    badgeColor = Gold400,
                    isAr = isAr
                )

                // Product Item 2: Smart Cloud ERP
                DigitalProductItemCard(
                    title = if (isAr) "منصة الحوسبة وإدارة المنظومات الذكية (Cloud ERP Suite)" else "Enterprise Cloud ERP & Automation Suite",
                    description = if (isAr)
                        "أتمتة العمليات، إدارة الحسابات المتزامنة، وإصدار التقارير اللحظية وإدارة الفرق الميدانية."
                    else
                        "Operational workflow automation, synchronized accounts, real-time analytics, and field roster management.",
                    badge = if (isAr) "سحابي • Cloud" else "Cloud ERP",
                    icon = Icons.Default.CloudQueue,
                    badgeColor = Cyan400,
                    isAr = isAr
                )

                // Product Item 3: LK-W Industrial Hub
                DigitalProductItemCard(
                    title = if (isAr) "بوابة التوريد الصناعي الذكية LK-W (Industrial Supply Hub)" else "LK-W Industrial Supply & Equipment Hub",
                    description = if (isAr)
                        "منصة رقمية لطلب وتتبع خطوط الإنتاج والعتاد الصناعي المتخصص وقطع الغيار اللوجستية."
                    else
                        "Digital platform for industrial machinery procurement, lifecycle tracking, and logistics supply lines.",
                    badge = if (isAr) "صناعي • Hardware" else "Industrial LK-W",
                    icon = Icons.Default.PrecisionManufacturing,
                    badgeColor = GreenSuccess,
                    isAr = isAr
                )

                // Product Item 4: Kashef Sentinel
                DigitalProductItemCard(
                    title = if (isAr) "نظام كاشف والقارات الأمني (Kashef Sentinel Security)" else "Kashef Sentinel & Continental Security",
                    description = if (isAr)
                        "منظومة كشف التهديدات السيبرانية، تحصين مسارات البيانات، والربط الجغرافي الموحد."
                    else
                        "Cyber threat detection, data path firewall isolation, and multi-continental unified routing.",
                    badge = if (isAr) "حماية • Sentinel" else "Sentinel Security",
                    icon = Icons.Default.Shield,
                    badgeColor = Color(0xFFA855F7),
                    isAr = isAr
                )

                // Product Item 5: AI Engine
                DigitalProductItemCard(
                    title = if (isAr) "محرك الذكاء الاصطناعي للأعمال (Enterprise AI Engine)" else "Enterprise AI Intelligence Engine",
                    description = if (isAr)
                        "تحليلات تنبؤية متقدمة، استشارات تشغيلية فورية، ومعالجة ذكية لسلاسل الإمداد."
                    else
                        "Advanced predictive intelligence, instantaneous operational advisory, and supply chain smart analytics.",
                    badge = if (isAr) "ذكاء اصطناعي • AI" else "AI Powered",
                    icon = Icons.Default.AutoAwesome,
                    badgeColor = OrangeWarning,
                    isAr = isAr
                )
            }
        },
        containerColor = Navy900,
        shape = RoundedCornerShape(18.dp)
    )
}

@Composable
private fun DigitalProductItemCard(
    title: String,
    description: String,
    badge: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    badgeColor: Color,
    isAr: Boolean
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Navy800,
        border = BorderStroke(1.dp, Slate700)
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(badgeColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = badgeColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = badgeColor.copy(alpha = 0.15f),
                    border = BorderStroke(0.5.dp, badgeColor.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = badgeColor,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Slate300,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            )
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
    var selectedOrgType by remember { mutableIntStateOf(0) } // 0: ضيف/زائر, 1: مصنع الشرق والغرب, 2: رفيق السند, 3: قمة الدرع, 4: الإدارة العامة
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
                    val finalUsername = if (username.isNotBlank()) username.trim() else "guest_${(1000..9999).random()}"
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
                        text = if (isAr) "للضيوف وموظفي الشركات التابعة والقيادة" else "For Guests, Subsidiary Staff & Leaders",
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
                    placeholder = { Text(if (isAr) "مثال: fahad@eastwest.com" else "e.g. user@domain.com") },
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
        "🌟 رابط تشغيل وتجربة تطبيق ياسر الرشيدي الجديد (منظومة شركة الشرق والغرب العالمية والشركات التابعة):\n$shareAppUrl\n\nبوابة السيادة • استعراض الشركات التابعة • بوابة الضيوف • حوكمة المنظومات"
    } else {
        "🌟 Live Web & Mobile Access to the New Yasser Al-Rashidi Enterprise App (East & West Global & Subsidiaries):\n$shareAppUrl"
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
                        text = if (isAr) "للإرسال للضيوف والشركات التابعة والإدارات" else "For Guests, Subsidiaries & Executives",
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
                        "يمكنك مشاركة هذا الرابط مع الضيوف، مديري الشركات التابعة، والموظفين لتشغيل وتجربة التطبيق مباشرة:"
                    else
                        "Share this link with guests, subsidiary executives, and teams to run the application immediately:",
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
