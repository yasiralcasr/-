package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AppLanguage
import com.example.ui.AppTab
import com.example.ui.MainViewModel
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                var showUserSwitchModal by remember { mutableStateOf(false) }

                val layoutDirection = if (uiState.language == AppLanguage.ARABIC) {
                    LayoutDirection.Rtl
                } else {
                    LayoutDirection.Ltr
                }

                // Show toast when message is present
                LaunchedEffect(uiState.toastMessage) {
                    uiState.toastMessage?.let { msg ->
                        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_LONG).show()
                        viewModel.clearToast()
                    }
                }

                CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                    when (uiState.screenMode) {
                        com.example.ui.AppScreenMode.WELCOME_GATEWAY -> {
                            WelcomeGatewayScreen(
                                language = uiState.language,
                                activeUser = uiState.activeUser,
                                isMasterUnlocked = uiState.isMasterCodeUnlocked,
                                onEnterGuestPortal = { viewModel.enterGuestPortal() },
                                onEnterSubsidiariesPortal = { viewModel.enterSubsidiariesPortal() },
                                onEnterOrganizerEnterprise = { viewModel.enterOrganizerEnterprise() },
                                onLoginStaffSubsidiary = { compAr, compEn, deptAr, deptEn, name, pass ->
                                    viewModel.loginStaffFromSubsidiary(compAr, compEn, deptAr, deptEn, name, pass)
                                },
                                onSubmitClientInquiry = { compAr, compEn, clientName, orgName, phone, email, type, notes ->
                                    viewModel.submitClientInquiryFromGateway(compAr, compEn, clientName, orgName, phone, email, type, notes)
                                },
                                onCreateNewUserAccount = { username, fullName, role, deptAr, deptEn ->
                                    viewModel.createUserAccount(username, fullName, role, deptAr, deptEn)
                                },
                                onSwitchUser = { user -> viewModel.switchActiveUser(user) },
                                onToggleLanguage = { viewModel.toggleLanguage() },
                                onOpenMasterKeyDialog = { viewModel.showMasterDialog(true) }
                            )
                        }

                        com.example.ui.AppScreenMode.GUEST_PORTAL -> {
                            GuestHomeScreen(
                                language = uiState.language,
                                onReturnToGateway = { viewModel.returnToWelcomeGateway() },
                                onEnterOrganizerMode = { viewModel.enterOrganizerEnterprise() }
                            )
                        }

                        com.example.ui.AppScreenMode.SUBSIDIARIES_PORTAL -> {
                            SubsidiariesPortalScreen(
                                language = uiState.language,
                                onReturnToGateway = { viewModel.returnToWelcomeGateway() },
                                onToggleLanguage = { viewModel.toggleLanguage() },
                                onEnterOrganizerMode = { viewModel.enterOrganizerEnterprise() },
                                onSwitchUser = { viewModel.switchActiveUser(it) }
                            )
                        }

                        com.example.ui.AppScreenMode.ORGANIZER_ENTERPRISE -> {
                            Scaffold(
                                modifier = Modifier.fillMaxSize(),
                                topBar = {
                                    TopBrandBar(
                                        language = uiState.language,
                                        activeUser = uiState.activeUser,
                                        isMasterUnlocked = uiState.isMasterCodeUnlocked,
                                        onToggleLanguage = { viewModel.toggleLanguage() },
                                        onOpenMasterDialog = { viewModel.showMasterDialog(true) },
                                        onRoleBadgeClick = { showUserSwitchModal = true },
                                        onProfileClick = { viewModel.selectTab(AppTab.PROFILE) },
                                        onReturnToGateway = { viewModel.returnToWelcomeGateway() }
                                    )
                                },
                                bottomBar = {
                                    BottomNav(
                                        currentTab = uiState.currentTab,
                                        onTabSelected = { viewModel.selectTab(it) },
                                        language = uiState.language,
                                        activeUser = uiState.activeUser,
                                        isMasterUnlocked = uiState.isMasterCodeUnlocked
                                    )
                                }
                            ) { innerPadding ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding)
                                ) {
                                    when (uiState.currentTab) {
                                AppTab.DASHBOARD -> {
                                    com.example.ui.components.EnterpriseRoleDashboard(
                                        activeUser = uiState.activeUser,
                                        firestoreProfile = uiState.currentProfile,
                                        isMasterUnlocked = uiState.isMasterCodeUnlocked,
                                        language = uiState.language,
                                        programsList = uiState.programsList,
                                        industrialProducts = uiState.industrialProducts,
                                        industrialOrders = uiState.industrialOrders,
                                        usersList = uiState.usersList,
                                        auditLogs = uiState.auditLogs,
                                        firestoreAuditLogs = uiState.firestoreAuditLogs,
                                        charityPoolBalance = uiState.charityPoolBalance,
                                        totalRevenueProcessed = uiState.totalRevenueProcessed,
                                        onNavigateTab = { viewModel.selectTab(it) },
                                        onToggleApproval = { viewModel.toggleProgramApproval(it) },
                                        onOpenMasterDialog = { viewModel.showMasterDialog(true) },
                                        onOpenOrderDialog = { viewModel.openOrderDialog(it) },
                                        onOpenLoginDialog = { viewModel.showLoginDialog(true) },
                                        onSwitchUser = { viewModel.switchActiveUser(it) }
                                    )
                                }

                                AppTab.PROGRAMS -> {
                                    ProgramsScreen(
                                        programsList = uiState.programsList,
                                        selectedSector = uiState.selectedSectorFilter,
                                        searchQuery = uiState.searchQuery,
                                        language = uiState.language,
                                        activeUser = uiState.activeUser,
                                        isMasterUnlocked = uiState.isMasterCodeUnlocked,
                                        onSectorFilterChange = { viewModel.setSectorFilter(it) },
                                        onSearchChange = { viewModel.setSearchQuery(it) },
                                        onDeleteProgram = { viewModel.deleteProgram(it) },
                                        onToggleApproval = { viewModel.toggleProgramApproval(it) },
                                        onNavigateToBuilder = { viewModel.selectTab(AppTab.BUILDER) }
                                    )
                                }

                                AppTab.BUILDER -> {
                                    ProgramBuilderScreen(
                                        language = uiState.language,
                                        activeUser = uiState.activeUser,
                                        isMasterUnlocked = uiState.isMasterCodeUnlocked,
                                        onCreateProgram = { titleAr, titleEn, sector, descAr, descEn, autoType, audAr, audEn, hooks ->
                                            viewModel.createProgram(
                                                titleAr,
                                                titleEn,
                                                sector,
                                                descAr,
                                                descEn,
                                                autoType,
                                                audAr,
                                                audEn,
                                                hooks
                                            )
                                        }
                                    )
                                }

                                AppTab.INDUSTRIAL_LKW -> {
                                    IndustrialLkwScreen(
                                        products = uiState.industrialProducts,
                                        orders = uiState.industrialOrders,
                                        language = uiState.language,
                                        activeUser = uiState.activeUser,
                                        isMasterUnlocked = uiState.isMasterCodeUnlocked,
                                        onOpenOrderDialog = { viewModel.openOrderDialog(it) },
                                        onUpdateOrderStatus = { orderId, newStatus ->
                                            viewModel.updateOrderStatus(orderId, newStatus)
                                        }
                                    )
                                }

                                AppTab.CONTINENTS_KASHEF -> {
                                    ContinentsScreen(
                                        selectedContinent = uiState.selectedContinent,
                                        magicQuery = uiState.magicWindowQuery,
                                        magicDetectedUrl = uiState.magicWindowDetectedUrl,
                                        magicResult = uiState.magicInspectionResult,
                                        quarantinedThreats = uiState.quarantinedThreats,
                                        altruismHistory = uiState.altruismHistory,
                                        charityPoolBalance = uiState.charityPoolBalance,
                                        totalRevenueProcessed = uiState.totalRevenueProcessed,
                                        language = uiState.language,
                                        activeUser = uiState.activeUser,
                                        onSelectContinent = { viewModel.selectContinent(it) },
                                        onQueryChange = { viewModel.setMagicWindowQuery(it) },
                                        onDetectedUrlChange = { viewModel.setMagicWindowDetectedUrl(it) },
                                        onProcessMagicWindow = { q, u -> viewModel.processMagicWindow(q, u) },
                                        onInjectAltruismRevenue = { amt, cur -> viewModel.injectAltruismRevenue(amt, cur) },
                                        onClearQuarantine = { viewModel.clearQuarantinedThreats() }
                                    )
                                }

                                AppTab.COMMAND_ROLES -> {
                                    CommandRolesScreen(
                                        usersList = uiState.usersList,
                                        auditLogs = uiState.auditLogs,
                                        firestoreAuditLogs = uiState.firestoreAuditLogs,
                                        isFirestoreAuditSyncing = uiState.isFirestoreAuditSyncing,
                                        activeUser = uiState.activeUser,
                                        isMasterUnlocked = uiState.isMasterCodeUnlocked,
                                        masterKeyInput = uiState.masterKeyInput,
                                        masterKeyError = uiState.masterKeyError,
                                        language = uiState.language,
                                        firebaseUserEmail = uiState.firebaseUserEmail,
                                        searchUserQuery = uiState.userSearchQuery,
                                        onSearchUserQueryChanged = { viewModel.setUserSearchQuery(it) },
                                        selectedUserRankFilter = uiState.userRankFilter,
                                        onSelectedUserRankFilterChanged = { viewModel.setUserRankFilter(it) },
                                        onOpenLoginScreen = { viewModel.showLoginDialog(true) },
                                        onSignOut = { viewModel.signOutAuth() },
                                        onMasterKeyInputChanged = { viewModel.onMasterKeyInputChanged(it) },
                                        onSubmitMasterKey = { viewModel.submitMasterKey() },
                                        onSwitchUser = { viewModel.switchActiveUser(it) },
                                        onEditUser = { viewModel.openEditUserDialog(it) },
                                        onCreateUser = { username, fullName, role, deptAr, deptEn ->
                                            viewModel.createUserAccount(username, fullName, role, deptAr, deptEn)
                                        },
                                        onDeleteUser = { viewModel.deleteUserAccount(it) },
                                        onShowPurgeDialog = { viewModel.showPurgeDialog(true) },
                                        onSyncFirestoreAudit = { viewModel.syncAuditLogsToFirestore() }
                                    )
                                }

                                AppTab.AUTH_LOGIN -> {
                                    LoginScreen(
                                        language = uiState.language,
                                        isLoading = uiState.isAuthLoading,
                                        errorMessage = uiState.authErrorMessage,
                                        onSignInWithGoogle = { act -> viewModel.signInWithGoogle(act) },
                                        onSignInWithEmail = { email, pass -> viewModel.signInWithEmail(email, pass) },
                                        onSignUpWithEmail = { email, pass, name -> viewModel.signUpWithEmail(email, pass, name) },
                                        onContinueAsGuest = { viewModel.continueAsGuest() },
                                        onOpenSovereignDialog = { viewModel.showMasterDialog(true) },
                                        onSendPasswordReset = { viewModel.sendPasswordResetEmail(it) },
                                        onSelectPresetRole = { viewModel.signInWithPresetRole(it) },
                                        onToggleLanguage = { viewModel.toggleLanguage() }
                                    )
                                }

                                AppTab.PROFILE -> {
                                    ProfileCustomizationScreen(
                                        activeUser = uiState.activeUser,
                                        currentProfile = uiState.currentProfile,
                                        isSaving = uiState.isProfileSaving,
                                        isLoading = uiState.isProfileLoading,
                                        language = uiState.language,
                                        onCameraCapture = { viewModel.onCameraImageCaptured(it) },
                                        onGalleryUpload = { viewModel.onGalleryImageSelected(it) },
                                        onSelectPreset = { viewModel.onSelectPresetAvatar(it) },
                                        onSaveProfile = { fullName, email, phone, bio, status ->
                                            viewModel.saveUserProfileToFirestore(fullName, email, phone, bio, status)
                                        },
                                        onRefreshFromCloud = { viewModel.loadUserProfile(uiState.activeUser.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Login Screen Modal Dialog
                    if (uiState.showLoginModal) {
                        Dialog(
                            onDismissRequest = { viewModel.showLoginDialog(false) },
                            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                LoginScreen(
                                    language = uiState.language,
                                    isLoading = uiState.isAuthLoading,
                                    errorMessage = uiState.authErrorMessage,
                                    onSignInWithGoogle = { act -> viewModel.signInWithGoogle(act) },
                                    onSignInWithEmail = { email, pass -> viewModel.signInWithEmail(email, pass) },
                                    onSignUpWithEmail = { email, pass, name -> viewModel.signUpWithEmail(email, pass, name) },
                                    onContinueAsGuest = { viewModel.showLoginDialog(false) },
                                    onOpenSovereignDialog = {
                                        viewModel.showLoginDialog(false)
                                        viewModel.showMasterDialog(true)
                                    },
                                    onSendPasswordReset = { viewModel.sendPasswordResetEmail(it) },
                                    onSelectPresetRole = { viewModel.signInWithPresetRole(it) },
                                    onToggleLanguage = { viewModel.toggleLanguage() }
                                )

                                IconButton(
                                    onClick = { viewModel.showLoginDialog(false) },
                                    modifier = Modifier
                                        .statusBarsPadding()
                                        .padding(16.dp)
                                        .align(Alignment.TopEnd)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close",
                                        tint = androidx.compose.ui.graphics.Color.White
                                    )
                                }
                            }
                        }
                    }

                    // Dialogs
                    if (uiState.showCredentialsDialog && uiState.newlyCreatedProgram != null) {
                        CredentialsDialog(
                            program = uiState.newlyCreatedProgram!!,
                            language = uiState.language,
                            onDismiss = { viewModel.dismissCredentialsDialog() }
                        )
                    }

                    if (uiState.showMasterCodeDialog) {
                        SovereignAccessDialog(
                            sovereignCodeInput = uiState.masterKeyInput,
                            errorMessage = uiState.masterKeyError,
                            language = uiState.language,
                            onInputChanged = { viewModel.onMasterKeyInputChanged(it) },
                            onSubmit = { viewModel.submitMasterKey() },
                            onDismiss = { viewModel.showMasterDialog(false) }
                        )
                    }

                    if (uiState.showOrderDialog && uiState.selectedProductForOrder != null) {
                        OrderDialog(
                            product = uiState.selectedProductForOrder!!,
                            language = uiState.language,
                            onSubmitOrder = { client, sector, qty, priority, loc, email, phone, notes ->
                                viewModel.submitIndustrialOrder(client, sector, qty, priority, loc, email, phone, notes)
                            },
                            onDismiss = { viewModel.dismissOrderDialog() }
                        )
                    }

                    if (uiState.showPurgeConfirmDialog) {
                        PurgeConfirmDialog(
                            language = uiState.language,
                            onConfirm = { viewModel.purgeSystemRecords() },
                            onDismiss = { viewModel.showPurgeDialog(false) }
                        )
                    }

                    if (uiState.showEditUserDialog && uiState.userBeingEdited != null) {
                        EditUserPrivilegesDialog(
                            user = uiState.userBeingEdited!!,
                            language = uiState.language,
                            onDismiss = { viewModel.closeEditUserDialog() },
                            onSave = { viewModel.updateUserAccount(it) }
                        )
                    }

                    if (showUserSwitchModal) {
                        UserSwitchDialog(
                            usersList = uiState.usersList,
                            activeUser = uiState.activeUser,
                            language = uiState.language,
                            onSelectUser = { viewModel.switchActiveUser(it) },
                            onDismiss = { showUserSwitchModal = false }
                        )
                    }
                }
            }
        }
    }
}
