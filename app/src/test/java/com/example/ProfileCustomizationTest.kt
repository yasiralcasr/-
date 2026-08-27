package com.example

import android.app.Application
import android.graphics.Bitmap
import androidx.test.core.app.ApplicationProvider
import com.example.data.firestore.FirestoreProfileManager
import com.example.data.firestore.FirestoreUserProfile
import com.example.data.model.RoleRank
import com.example.data.model.UserAccount
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ProfileCustomizationTest {

    private lateinit var app: Application
    private lateinit var profileManager: FirestoreProfileManager

    @Before
    fun setUp() {
        app = ApplicationProvider.getApplicationContext()
        profileManager = FirestoreProfileManager.getInstance(app)
    }

    @Test
    fun `verify FirestoreUserProfile mapping from UserAccount`() {
        val user = UserAccount(
            id = "usr-test-1",
            username = "test_commander",
            fullName = "القائد التنفيذي",
            roleRank = RoleRank.GENERAL,
            departmentAr = "العمليات السيادية",
            departmentEn = "Sovereign Operations",
            assignedCode = "TEST-CODE",
            canRead = true,
            canWrite = true,
            canExecute = true,
            canAdminister = true,
            canPurge = false,
            photoUrl = "https://example.com/avatar.jpg",
            imageReference = "gs://bucket/avatars/user.jpg",
            bio = "مدير المنظومة",
            phoneNumber = "+966500000000"
        )

        val profile = FirestoreUserProfile.fromUserAccount(user, "test@eastwest.com")

        assertEquals("usr-test-1", profile.userId)
        assertEquals("القائد التنفيذي", profile.fullName)
        assertEquals(5, profile.roleRankLevel)
        assertEquals("+966500000000", profile.phoneNumber)
        assertEquals("مدير المنظومة", profile.bio)
        assertEquals("https://example.com/avatar.jpg", profile.photoUrl)
        assertEquals("gs://bucket/avatars/user.jpg", profile.imageReference)

        val map = profile.toMap()
        assertEquals("usr-test-1", map["userId"])
        assertEquals("القائد التنفيذي", map["fullName"])
        assertEquals(5, map["roleRankLevel"])
        assertEquals("+966500000000", map["phoneNumber"])
    }

    @Test
    fun `verify camera bitmap processing generates local file and storage reference`() = runBlocking {
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val (localPath, imageRef) = profileManager.processCapturedCameraBitmap("usr-test-camera", bitmap)

        assertNotNull(localPath)
        assertTrue(localPath.isNotEmpty())
        assertNotNull(imageRef)
        assertTrue(imageRef.startsWith("data:image/jpeg;base64,"))
    }
}
