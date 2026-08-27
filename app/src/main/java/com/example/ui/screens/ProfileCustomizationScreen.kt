package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.firestore.FirestoreUserProfile
import com.example.data.model.AppLanguage
import com.example.data.model.RoleRank
import com.example.data.model.UserAccount
import com.example.ui.theme.*
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileCustomizationScreen(
    activeUser: UserAccount,
    currentProfile: FirestoreUserProfile?,
    isSaving: Boolean,
    isLoading: Boolean,
    language: AppLanguage,
    onCameraCapture: (Bitmap) -> Unit,
    onGalleryUpload: (Uri) -> Unit,
    onSelectPreset: (String) -> Unit,
    onSaveProfile: (fullName: String, email: String, phone: String, bio: String, statusMessage: String) -> Unit,
    onRefreshFromCloud: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAr = language == AppLanguage.ARABIC
    val context = LocalContext.current

    // Form inputs state initialized from active user / Firestore profile
    var fullNameInput by remember(currentProfile, activeUser) {
        mutableStateOf(currentProfile?.fullName?.ifEmpty { activeUser.fullName } ?: activeUser.fullName)
    }
    var emailInput by remember(currentProfile) {
        mutableStateOf(currentProfile?.email ?: "${activeUser.username}@eastwest-global.com")
    }
    var phoneInput by remember(currentProfile, activeUser) {
        mutableStateOf(currentProfile?.phoneNumber?.ifEmpty { activeUser.phoneNumber } ?: activeUser.phoneNumber)
    }
    var bioInput by remember(currentProfile, activeUser) {
        mutableStateOf(currentProfile?.bio?.ifEmpty { activeUser.bio } ?: if (isAr) "القيادة والسيادة في الأتمتة الشاملة والحلول الصناعية" else "Sovereign Executive in Enterprise Automation")
    }
    var statusMessageInput by remember(currentProfile) {
        mutableStateOf(currentProfile?.statusMessage ?: if (isAr) "جاهز للعمليات السيادية 24/7" else "Active Operational Readiness 24/7")
    }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            onCameraCapture(bitmap)
        }
    }

    // Permission launcher for camera
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(
                context,
                if (isAr) "يرجى منح إذن الكاميرا لالتقاط صورة الملف الشخصي" else "Please grant camera permission to take profile photo",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Gallery / File Image Picker launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onGalleryUpload(uri)
        }
    }

    val photoReference = currentProfile?.photoUrl?.ifEmpty { activeUser.photoUrl } ?: activeUser.photoUrl
    val imageRefString = currentProfile?.imageReference?.ifEmpty { activeUser.imageReference } ?: activeUser.imageReference

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Navy900)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Executive Header Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Navy800),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Gold500.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile Icon",
                            tint = Gold400,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAr) "تخصيص الملف والصورة السيادية" else "Executive Profile & Avatar Customizer",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Gold300
                            )
                        )
                    }

                    // Cloud Sync Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Cyan500.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, Cyan400.copy(alpha = 0.5f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Firestore Live",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Cyan300
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Avatar Frame with Gold Glowing Ring
                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier = Modifier.size(130.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .shadow(12.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(Gold400.copy(alpha = 0.3f), Navy900)
                                )
                            )
                            .border(3.dp, Gold400, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (photoReference.isNotEmpty() && (photoReference.startsWith("/") || photoReference.startsWith("file:") || photoReference.startsWith("http") || photoReference.startsWith("content:"))) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(if (photoReference.startsWith("/")) File(photoReference) else photoReference)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Profile Photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else if (activeUser.roleRank == RoleRank.SUPREME_COMMANDER || activeUser.fullName.contains("ياسر") || activeUser.fullName.contains("Yasser")) {
                            Image(
                                painter = painterResource(id = R.drawable.img_father_yasser_avatar),
                                contentDescription = "Father Yasser Sovereign Portrait",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            // Default Rank Emblem
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = activeUser.roleRank.badgeIcon,
                                    fontSize = 42.sp
                                )
                                Text(
                                    text = activeUser.roleRank.titleAr.take(8),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Gold300
                                    )
                                )
                            }
                        }
                    }

                    // Camera Action Trigger Badge
                    Surface(
                        shape = CircleShape,
                        color = Gold500,
                        border = BorderStroke(2.dp, Navy900),
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .testTag("avatar_quick_camera_button")
                            .clickable {
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                    cameraLauncher.launch(null)
                                } else {
                                    permissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = "Take Photo",
                                tint = Navy900,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = fullNameInput.ifEmpty { activeUser.fullName },
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 18.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = if (isAr) activeUser.departmentAr else activeUser.departmentEn,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Slate300,
                        fontSize = 12.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (activeUser.roleRank.level) {
                        6 -> Gold500.copy(alpha = 0.25f)
                        5 -> Cyan500.copy(alpha = 0.25f)
                        4 -> Slate300.copy(alpha = 0.25f)
                        else -> Slate700
                    },
                    border = BorderStroke(1.dp, if (activeUser.roleRank.level == 6) Gold400 else Cyan400)
                ) {
                    Text(
                        text = if (isAr) activeUser.roleRank.titleAr else activeUser.roleRank.titleEn,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (activeUser.roleRank.level == 6) Gold300 else Cyan300
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
            }
        }

        // Camera & Upload Action Panel
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Navy800),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, Slate700)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = if (isAr) "📸 خيارات التقاط ورفع الصورة الشخصية" else "📸 Profile Photo Capture & Upload Options",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Gold400
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Capture with Camera Button
                    Button(
                        onClick = {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                cameraLauncher.launch(null)
                            } else {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Gold500),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("capture_camera_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = "Camera",
                            tint = Navy900,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAr) "الكاميرا المباشرة" else "Live Camera",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Navy900
                            )
                        )
                    }

                    // Pick from Gallery / Storage Button
                    OutlinedButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Cyan400),
                        border = BorderStroke(1.dp, Cyan400),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("upload_gallery_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Gallery",
                            tint = Cyan400,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAr) "رفع من المعرض" else "Upload Gallery",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Cyan300
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Presets Selection
                Text(
                    text = if (isAr) "🎖️ أو اختر شارة قيادية معتمدة:" else "🎖️ Or choose an executive preset badge:",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Slate300,
                        fontSize = 11.5.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val presets = listOf(
                        Triple("👑", if (isAr) "صورة المؤسس" else "Founder", "PRESET_FATHER_YASSER"),
                        Triple("🛡️", if (isAr) "الدرع السيبراني" else "Cyber Shield", "PRESET_CYBER_SHIELD"),
                        Triple("⚙️", if (isAr) "أتمتة LK-W" else "LK-W Tech", "PRESET_LKW_TECH"),
                        Triple("⭐", if (isAr) "القيادة العامة" else "Command", "PRESET_GENERAL_STAR")
                    )

                    presets.forEach { (icon, title, key) ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Navy700,
                            border = BorderStroke(1.dp, if (photoReference == key) Gold400 else Slate700),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("preset_$key")
                                .clickable { onSelectPreset(key) }
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = icon, fontSize = 20.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 9.5.sp,
                                        color = if (photoReference == key) Gold300 else Slate300
                                    ),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        // Editable Profile Fields Form
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Navy800),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, Slate700)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = if (isAr) "📝 بيانات الملف الشخصي والاتصال" else "📝 Profile & Contact Information",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Gold400
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Full Name
                OutlinedTextField(
                    value = fullNameInput,
                    onValueChange = { fullNameInput = it },
                    label = { Text(if (isAr) "الاسم الكامل / اللقب الرسمي" else "Full Official Name") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Gold400) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Gold400,
                        unfocusedBorderColor = Slate700,
                        focusedContainerColor = Navy900,
                        unfocusedContainerColor = Navy900,
                        focusedLabelColor = Gold300,
                        unfocusedLabelColor = Slate300
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_profile_fullname")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Email
                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it },
                    label = { Text(if (isAr) "البريد الإلكتروني السحابي" else "Cloud Email") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Cyan400) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Cyan400,
                        unfocusedBorderColor = Slate700,
                        focusedContainerColor = Navy900,
                        unfocusedContainerColor = Navy900,
                        focusedLabelColor = Cyan300,
                        unfocusedLabelColor = Slate300
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_profile_email")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Phone
                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = { phoneInput = it },
                    label = { Text(if (isAr) "رقم الهاتف / الاتصال المباشر" else "Phone / Direct Contact") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Gold400) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Gold400,
                        unfocusedBorderColor = Slate700,
                        focusedContainerColor = Navy900,
                        unfocusedContainerColor = Navy900,
                        focusedLabelColor = Gold300,
                        unfocusedLabelColor = Slate300
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_profile_phone")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Sovereign Bio / Statement
                OutlinedTextField(
                    value = bioInput,
                    onValueChange = { bioInput = it },
                    label = { Text(if (isAr) "البيان التعريفي والمهام السيادية" else "Executive Bio & Sovereign Mission") },
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = Cyan400) },
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Cyan400,
                        unfocusedBorderColor = Slate700,
                        focusedContainerColor = Navy900,
                        unfocusedContainerColor = Navy900,
                        focusedLabelColor = Cyan300,
                        unfocusedLabelColor = Slate300
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_profile_bio")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Status Message
                OutlinedTextField(
                    value = statusMessageInput,
                    onValueChange = { statusMessageInput = it },
                    label = { Text(if (isAr) "شعار الحالة والجاهزية" else "Status & Readiness Motto") },
                    leadingIcon = { Icon(Icons.Default.Bolt, contentDescription = null, tint = Gold400) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Gold400,
                        unfocusedBorderColor = Slate700,
                        focusedContainerColor = Navy900,
                        unfocusedContainerColor = Navy900,
                        focusedLabelColor = Gold300,
                        unfocusedLabelColor = Slate300
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_profile_status")
                )
            }
        }

        // Firestore Image Reference Inspector
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            colors = CardDefaults.cardColors(containerColor = Navy800),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, Slate700)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CloudQueue,
                            contentDescription = "Cloud Storage",
                            tint = Cyan400,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAr) "مرجع الصورة السحابي (Firestore Reference)" else "Firestore Image Reference URI",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Cyan300
                            )
                        )
                    }

                    Text(
                        text = "collection: user_profiles",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Slate400,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Navy900,
                    border = BorderStroke(0.5.dp, Slate700),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (imageRefString.isNotBlank()) imageRefString.take(80) + if (imageRefString.length > 80) "..." else ""
                        else if (photoReference.isNotBlank()) photoReference
                        else "gs://eastwest-global-2026.appspot.com/avatars/${activeUser.id}_avatar.jpg",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = if (imageRefString.isNotEmpty()) Gold300 else Slate400
                        ),
                        modifier = Modifier.padding(10.dp)
                    )
                }

                if (currentProfile?.lastUpdated?.isNotEmpty() == true) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isAr) "آخر مزامنة سحابية: ${currentProfile.lastUpdated}" else "Last Cloud Sync: ${currentProfile.lastUpdated}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Slate400,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }

        // Action Buttons: Save & Sync + Refresh
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Save & Sync to Firestore
            Button(
                onClick = {
                    onSaveProfile(
                        fullNameInput,
                        emailInput,
                        phoneInput,
                        bioInput,
                        statusMessageInput
                    )
                },
                enabled = !isSaving,
                colors = ButtonDefaults.buttonColors(containerColor = Gold500),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1.5f)
                    .height(52.dp)
                    .testTag("save_profile_firestore_button")
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Navy900,
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAr) "جارِ المزامنة مع السحابة..." else "Syncing with Firestore...",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Navy900
                        )
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = "Save Cloud",
                        tint = Navy900,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAr) "حفظ ومزامنة مع Firestore" else "Save & Sync to Firestore",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Navy900
                        )
                    )
                }
            }

            // Refresh from Cloud Button
            OutlinedButton(
                onClick = { onRefreshFromCloud() },
                enabled = !isLoading,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Cyan400),
                border = BorderStroke(1.dp, Cyan400),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("refresh_profile_button")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Cyan400,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = Cyan400,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAr) "استرجاع السحابة" else "Cloud Fetch",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Cyan300
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
