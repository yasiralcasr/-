package com.example.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.RoleRank
import com.example.data.model.UserAccount
import com.example.ui.AppTab
import com.example.ui.theme.*

@Composable
fun BottomNav(
    currentTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    language: AppLanguage,
    activeUser: UserAccount? = null,
    isMasterUnlocked: Boolean = false,
    modifier: Modifier = Modifier
) {
    val isAr = language == AppLanguage.ARABIC
    val canAccessBuilder = isMasterUnlocked || (activeUser?.canWrite == true)
    val canAccessCommand = isMasterUnlocked || (activeUser?.canAdminister == true) || ((activeUser?.roleRank?.level ?: 1) >= RoleRank.SUPERVISOR.level)

    NavigationBar(
        modifier = modifier.testTag("main_bottom_nav"),
        containerColor = Navy800,
        contentColor = Slate100,
        tonalElevation = 8.dp
    ) {
        // Tab 0: Dashboard (Role Adaptive for Admin vs End-user)
        NavigationBarItem(
            selected = currentTab == AppTab.DASHBOARD,
            onClick = { onTabSelected(AppTab.DASHBOARD) },
            icon = {
                Icon(
                    imageVector = if (canAccessCommand) Icons.Default.Dashboard else Icons.Default.SpaceDashboard,
                    contentDescription = "Dashboard",
                    modifier = Modifier.size(20.dp)
                )
            },
            label = {
                Text(
                    text = if (isAr) (if (canAccessCommand) "الرئيسية" else "البوابة") else (if (canAccessCommand) "Dashboard" else "Portal"),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = if (currentTab == AppTab.DASHBOARD) FontWeight.Bold else FontWeight.Normal
                    )
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Navy900,
                selectedTextColor = Gold400,
                indicatorColor = Gold400,
                unselectedIconColor = Slate300,
                unselectedTextColor = Slate300
            ),
            modifier = Modifier.testTag("tab_dashboard")
        )

        // Tab 1: Programs & Systems
        NavigationBarItem(
            selected = currentTab == AppTab.PROGRAMS,
            onClick = { onTabSelected(AppTab.PROGRAMS) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Hub,
                    contentDescription = "Programs",
                    modifier = Modifier.size(20.dp)
                )
            },
            label = {
                Text(
                    text = if (isAr) "المنظومات" else "Programs",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = if (currentTab == AppTab.PROGRAMS) FontWeight.Bold else FontWeight.Normal
                    )
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Navy900,
                selectedTextColor = Gold400,
                indicatorColor = Gold400,
                unselectedIconColor = Slate300,
                unselectedTextColor = Slate300
            ),
            modifier = Modifier.testTag("tab_programs")
        )

        // Tab 2: Program Builder (Admin/Specialist Only)
        if (canAccessBuilder) {
            NavigationBarItem(
                selected = currentTab == AppTab.BUILDER,
                onClick = { onTabSelected(AppTab.BUILDER) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.AddBox,
                        contentDescription = "Builder",
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = {
                    Text(
                        text = if (isAr) "المنشئ" else "Builder",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = if (currentTab == AppTab.BUILDER) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Navy900,
                    selectedTextColor = Gold400,
                    indicatorColor = Gold400,
                    unselectedIconColor = Slate300,
                    unselectedTextColor = Slate300
                ),
                modifier = Modifier.testTag("tab_builder")
            )
        }

        // Tab 3: LK-W Industrial
        NavigationBarItem(
            selected = currentTab == AppTab.INDUSTRIAL_LKW,
            onClick = { onTabSelected(AppTab.INDUSTRIAL_LKW) },
            icon = {
                Icon(
                    imageVector = Icons.Default.PrecisionManufacturing,
                    contentDescription = "LK-W Products",
                    modifier = Modifier.size(20.dp)
                )
            },
            label = {
                Text(
                    text = if (isAr) "LK-W" else "LK-W",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = if (currentTab == AppTab.INDUSTRIAL_LKW) FontWeight.Bold else FontWeight.Normal
                    )
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Navy900,
                selectedTextColor = Gold400,
                indicatorColor = Gold400,
                unselectedIconColor = Slate300,
                unselectedTextColor = Slate300
            ),
            modifier = Modifier.testTag("tab_industrial")
        )

        // Tab 4: Continents & Kashef Al-Mastoor
        NavigationBarItem(
            selected = currentTab == AppTab.CONTINENTS_KASHEF,
            onClick = { onTabSelected(AppTab.CONTINENTS_KASHEF) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Public,
                    contentDescription = "Continents",
                    modifier = Modifier.size(20.dp)
                )
            },
            label = {
                Text(
                    text = if (isAr) "القارات" else "Continents",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = if (currentTab == AppTab.CONTINENTS_KASHEF) FontWeight.Bold else FontWeight.Normal
                    )
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Navy900,
                selectedTextColor = Cyan400,
                indicatorColor = Cyan400,
                unselectedIconColor = Slate300,
                unselectedTextColor = Slate300
            ),
            modifier = Modifier.testTag("tab_continents")
        )

        // Tab 5: Command & Roles (Leadership / Admin only)
        if (canAccessCommand) {
            NavigationBarItem(
                selected = currentTab == AppTab.COMMAND_ROLES,
                onClick = { onTabSelected(AppTab.COMMAND_ROLES) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Command & Roles",
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = {
                    Text(
                        text = if (isAr) "القيادة" else "Command",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = if (currentTab == AppTab.COMMAND_ROLES) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Navy900,
                    selectedTextColor = Gold400,
                    indicatorColor = Gold400,
                    unselectedIconColor = Slate300,
                    unselectedTextColor = Slate300
                ),
                modifier = Modifier.testTag("tab_command")
            )
        }

        // Tab 6: Profile & Avatar
        NavigationBarItem(
            selected = currentTab == AppTab.PROFILE,
            onClick = { onTabSelected(AppTab.PROFILE) },
            icon = {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile",
                    modifier = Modifier.size(20.dp)
                )
            },
            label = {
                Text(
                    text = if (isAr) "الملف" else "Profile",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = if (currentTab == AppTab.PROFILE) FontWeight.Bold else FontWeight.Normal
                    )
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Navy900,
                selectedTextColor = Gold400,
                indicatorColor = Gold400,
                unselectedIconColor = Slate300,
                unselectedTextColor = Slate300
            ),
            modifier = Modifier.testTag("tab_profile")
        )
    }
}
