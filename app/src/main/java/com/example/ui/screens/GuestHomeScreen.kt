package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.ui.theme.*

/**
 * Dedicated Guest Screen (صفحة الضيوف المستقلة)
 * Clean, elegant and empty workspace for guests as requested.
 * Equipped with active navigation to return to the Welcome Gateway or transition to Organizer access.
 */
@Composable
fun GuestHomeScreen(
    language: AppLanguage,
    onReturnToGateway: () -> Unit,
    onEnterOrganizerMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAr = language == AppLanguage.ARABIC

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("guest_home_screen"),
        topBar = {
            Surface(
                color = Navy800,
                shadowElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Return to Welcome Gateway Button
                        OutlinedButton(
                            onClick = onReturnToGateway,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Slate700),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Navy900,
                                contentColor = Slate200
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("btn_guest_back_gateway")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAr) "الشاشة الترحيبية" else "Welcome Screen",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            )
                        }

                        // Guest Profile Pill
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Cyan500.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, Cyan400.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Cyan400)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isAr) "حساب ضيف (زائر)" else "Guest Account",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Cyan300,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        // Switch to Organizer
                        TextButton(
                            onClick = onEnterOrganizerMode,
                            colors = ButtonDefaults.textButtonColors(contentColor = Gold400),
                            modifier = Modifier.testTag("btn_guest_to_organizer")
                        ) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isAr) "دخول المنظم" else "Organizer",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
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
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Elegant Clean Empty State Presentation
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Navy800.copy(alpha = 0.9f),
                border = BorderStroke(1.dp, Slate700),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Empty Canvas Icon
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(Navy900)
                            .border(2.dp, Cyan400.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Inbox,
                            contentDescription = "Empty Space",
                            tint = Cyan300,
                            modifier = Modifier.size(44.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = if (isAr) "مساحة الضيوف والزوار" else "Guest & Visitor Workspace",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 20.sp
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isAr)
                            "هذه الصفحة فارغة ومجهزة بالكامل لحسابات الضيوف. لا توجد عناصر أو مهام معروضة في الوقت الحالي."
                        else
                            "This workspace is clean and ready for guest access. There are currently no assigned tasks or public programs in this view.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Slate300,
                            fontSize = 13.sp,
                            lineHeight = 20.sp
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    HorizontalDivider(color = Slate700, modifier = Modifier.padding(horizontal = 24.dp))

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onReturnToGateway,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Cyan500,
                                contentColor = Navy900
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAr) "الرئيسية الترحيبية" else "Home Gateway",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                            )
                        }

                        OutlinedButton(
                            onClick = onEnterOrganizerMode,
                            border = BorderStroke(1.dp, Gold400),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Gold400,
                                containerColor = Navy900
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAr) "دخول المنظم" else "Organizer Portal",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Navy800.copy(alpha = 0.5f),
                border = BorderStroke(0.5.dp, Gold500.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isAr)
                            "قيادة وإشراف الرئيس التنفيذي للشركة والشركات التابعة"
                        else
                            "Leadership & Supervision of Group & Subsidiaries CEO",
                        style = MaterialTheme.typography.labelSmall.copy(color = Gold400, fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = if (isAr)
                            "الأستاذ القانوني والخبير التقني / ياسر الرشيدي\nومدير الاستثمار الأجنبي / شوكت فيتا"
                        else
                            "Legal & Tech Expert / Yasser Al-Rashidi\nForeign Investment / Shawkat Fita",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Slate300,
                            fontSize = 9.5.sp,
                            lineHeight = 15.sp
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (isAr)
                            "البوابة الرقمية الشاملة لإدارة المنظومات في الشركات"
                        else
                            "The Comprehensive Digital Gateway for Enterprise Systems Management",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Cyan300,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isAr) "شركة الشرق والغرب العالمية • جميع الحقوق محفوظة © 2026" else "East & West Global • All Rights Reserved © 2026",
                style = MaterialTheme.typography.labelSmall.copy(color = Slate400, fontSize = 10.sp),
                textAlign = TextAlign.Center
            )
        }
    }
}
