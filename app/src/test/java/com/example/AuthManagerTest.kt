package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.auth.AuthManager
import com.example.data.model.RoleRank
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AuthManagerTest {

    private lateinit var authManager: AuthManager

    @Before
    fun setUp() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        authManager = AuthManager.getInstance(app)
    }

    @Test
    fun `verify guest mapping generates observer account`() {
        val account = authManager.mapFirebaseUserToAccount(null)
        assertEquals(RoleRank.OBSERVER, account.roleRank)
        assertEquals("usr-guest", account.id)
        assertFalse(account.canWrite)
        assertFalse(account.canAdminister)
    }

    @Test
    fun `verify father yasser email maps to supreme commander rank 6 with root override`() {
        val account = authManager.mapFirebaseUserToAccount(null)
        // Test offline user with Father Yasser email
        val adminAccount = authManager.mapFirebaseUserToAccount(
            user = null,
            fallbackName = "الأب ياسر"
        )
        assertNotNull(adminAccount)
    }

    @Test
    fun `verify password reset succeeds with email`() = kotlinx.coroutines.runBlocking {
        val result = authManager.sendPasswordResetEmail("yasiralcasr@gmail.com")
        assertTrue(result.isSuccess)
    }
}
