package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.model.AppLanguage
import com.example.data.model.UserAccount
import com.example.ui.theme.*
import java.io.File

@Composable
fun TopBrandBar(
    language: AppLanguage,
    activeUser: UserAccount,
    isMasterUnlocked: Boolean,
    onToggleLanguage: () -> Unit,
    onOpenMasterDialog: () -> Unit,
    onRoleBadgeClick: () -> Unit,
    onProfileClick: () -> Unit = {},
    onReturnToGateway: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isAr = language == AppLanguage.ARABIC

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Navy800,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Logo & Company Name
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Navy900)
                            .border(1.5.dp, Gold500, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_company_logo),
                            contentDescription = "East West Global Logo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isAr) "شركة الشرق والغرب العالمية" else "East & West Global Co.",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Gold400,
                                    fontSize = 15.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Gold500.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(0.5.dp, Gold400)
                            ) {
                                Text(
                                    text = if (isAr) "CEO • الرئيس التنفيذي" else "CEO & Founder",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Gold300
                                    ),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = if (isAr) "منظومة الأتمتة والسيادة وحلول التوريد الصناعي LK-W" else "Enterprise Automation & LK-W Industrial",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Slate300,
                                fontSize = 10.5.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Action Buttons (Gateway + Language + Master Key)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (onReturnToGateway != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Navy700,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Slate700),
                            modifier = Modifier
                                .testTag("return_to_gateway_button")
                                .clickable { onReturnToGateway() }
                        ) {
                            Box(
                                modifier = Modifier.padding(6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = "Welcome Gateway",
                                    tint = Cyan300,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // Language Switcher
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Navy700,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate700),
                        modifier = Modifier
                            .testTag("language_toggle_button")
                            .clickable { onToggleLanguage() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Language",
                                tint = Cyan400,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isAr) "EN" else "عربي",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }

                    // Master Key Unlock Button
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isMasterUnlocked) Gold500 else Navy700,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isMasterUnlocked) Gold300 else Slate700
                        ),
                        modifier = Modifier
                            .testTag("master_key_button")
                            .clickable { onOpenMasterDialog() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = if (isMasterUnlocked) Icons.Default.VpnKey else Icons.Default.Lock,
                                contentDescription = "Master Key",
                                tint = if (isMasterUnlocked) Navy900 else Gold400,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isMasterUnlocked) {
                                    if (isAr) "السيادة نشطة" else "Root Active"
                                } else {
                                    if (isAr) "كود السيادة" else "Master Key"
                                },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isMasterUnlocked) Navy900 else Gold400
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Active Role and Department Pill Bar
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Navy700.copy(alpha = 0.85f),
                border = BorderStroke(0.5.dp, Gold500.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onProfileClick() }
                    ) {
                        val photo = activeUser.photoUrl
                        if (photo.isNotEmpty() && (photo.startsWith("/") || photo.startsWith("file:") || photo.startsWith("http") || photo.startsWith("content:"))) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, Gold400, CircleShape)
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                                        .data(if (photo.startsWith("/")) File(photo) else photo)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "User Avatar",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        } else if (activeUser.roleRank.level == 6 || activeUser.fullName.contains("ياسر") || activeUser.fullName.contains("Yasser")) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, Gold400, CircleShape)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.img_father_yasser_avatar),
                                    contentDescription = "Father Yasser Portrait",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        } else {
                            Text(
                                text = activeUser.roleRank.badgeIcon,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = activeUser.fullName,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = when (activeUser.roleRank.level) {
                                6 -> Gold500.copy(alpha = 0.25f)
                                5 -> Cyan500.copy(alpha = 0.25f)
                                4 -> Slate300.copy(alpha = 0.25f)
                                else -> Slate700
                            }
                        ) {
                            Text(
                                text = if (isAr) activeUser.roleRank.titleAr else activeUser.roleRank.titleEn,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (activeUser.roleRank.level == 6) Gold300 else Cyan400
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Quick Profile Button
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Navy800,
                            border = BorderStroke(0.5.dp, Gold400.copy(alpha = 0.6f)),
                            modifier = Modifier
                                .testTag("top_profile_customizer_btn")
                                .clickable { onProfileClick() }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Edit Profile",
                                    tint = Gold400,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = if (isAr) "الصورة" else "Photo",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Gold300
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Switch User Button
                        Text(
                            text = if (isAr) "تبديل ▾" else "Switch ▾",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Slate300,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier
                                .clickable { onRoleBadgeClick() }
                                .padding(4.dp)
                        )
                    }
                }
            }
        }
    }
}
