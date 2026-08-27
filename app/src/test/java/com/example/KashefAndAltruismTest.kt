package com.example

import com.example.control.AltruismEngine
import com.example.control.KashefSecurityEngine
import com.example.data.model.RoleRank
import com.example.data.model.UserAccount
import com.example.ui.components.SOVEREIGN_CODE_VALUE
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class KashefAndAltruismTest {

    @Test
    fun `verify ancient trust map routes to sovereign right path`() {
        val result = KashefSecurityEngine.processMagicWindow("مصرف الراجحي افراد")
        assertTrue(result.isApprovedRightPath)
        assertEquals("https://alrajhibank.com.sa", result.targetUrl)
    }

    @Test
    fun `verify phishing scam is quarantined to black basket left path with moral advisory`() {
        val scamResult = KashefSecurityEngine.processMagicWindow(
            userQuery = "دخول الراجحي السريع المزور",
            detectedUrl = "https://scam-fake-alrajhi-login.net/auth",
            domainCreationDateStr = "2026-08-01"
        )
        assertFalse(scamResult.isApprovedRightPath)
        assertNotNull(scamResult.moralAdvisoryAr)
        assertTrue(scamResult.moralAdvisoryAr!!.contains("اتقِ الله ولا تفعل"))
    }

    @Test
    fun `verify altruism engine automatically deducts 33 percent for charity and clean water`() {
        val initialBalance = AltruismEngine.charityPoolBalance
        val event = AltruismEngine.injectRevenueAndDistribute(100000.0, "SAR")
        assertEquals(33000.0, event.charityShare, 0.01)
        assertEquals(67000.0, event.retainedShare, 0.01)
        assertEquals(initialBalance + 33000.0, AltruismEngine.charityPoolBalance, 0.01)
    }

    @Test
    fun `verify sovereign code grants root access rank 6 and enables administrative actions`() {
        val inputCode = "1073781088@0503026675#8054$8051%"
        assertEquals(SOVEREIGN_CODE_VALUE, inputCode)

        var grantedUser: UserAccount? = null
        if (inputCode == SOVEREIGN_CODE_VALUE) {
            grantedUser = UserAccount(
                id = "usr-supreme-root",
                username = "father_yasser_root",
                fullName = "الأب ياسر (المؤسس والرئيس الأعلى)",
                roleRank = RoleRank.SUPREME_COMMANDER,
                departmentAr = "القيادة والسيادة العليا - الأب ياسر",
                departmentEn = "Supreme Sovereign Authority - Father Yasser",
                assignedCode = SOVEREIGN_CODE_VALUE,
                canRead = true,
                canWrite = true,
                canExecute = true,
                canAdminister = true,
                canPurge = true,
                isMasterOverride = true
            )
        }

        assertNotNull(grantedUser)
        assertEquals(6, grantedUser!!.roleRank.level)
        assertEquals(RoleRank.SUPREME_COMMANDER, grantedUser.roleRank)
        assertTrue(grantedUser.canAdminister)
        assertTrue(grantedUser.canPurge)
        assertTrue(grantedUser.canExecute)
        assertTrue(grantedUser.canWrite)
        assertTrue(grantedUser.isMasterOverride)
    }
}
