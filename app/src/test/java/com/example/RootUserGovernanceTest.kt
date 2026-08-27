package com.example

import android.app.Application
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.AppDatabase
import com.example.data.model.RoleRank
import com.example.data.model.UserAccount
import com.example.data.repository.EastWestRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RootUserGovernanceTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: EastWestRepository

    @Before
    fun setUp() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        db = Room.inMemoryDatabaseBuilder(app, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = EastWestRepository(db)
        runBlocking {
            repository.seedInitialData()
        }
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `verify default users seeded with proper ranks and permissions`() = runBlocking {
        val users = repository.getAllUsers().first()
        assertTrue(users.isNotEmpty())

        val supremeCommander = users.firstOrNull { it.roleRank == RoleRank.SUPREME_COMMANDER }
        assertNotNull(supremeCommander)
        assertTrue(supremeCommander!!.canAdminister)
        assertTrue(supremeCommander.canPurge)
        assertTrue(supremeCommander.isMasterOverride)
        assertEquals(6, supremeCommander.roleRank.level)
    }

    @Test
    fun `verify root admin can update user rank and privileges`() = runBlocking {
        val users = repository.getAllUsers().first()
        val targetUser = users.first { it.roleRank == RoleRank.OBSERVER }

        val promotedUser = targetUser.copy(
            roleRank = RoleRank.SUPERVISOR,
            canRead = true,
            canWrite = true,
            canExecute = true,
            canAdminister = false
        )

        repository.updateUser(promotedUser, actor = "الأب ياسر (الرئيس الأعلى - Root Access)")

        val updatedUsers = repository.getAllUsers().first()
        val retrieved = updatedUsers.first { it.id == targetUser.id }

        assertEquals(RoleRank.SUPERVISOR, retrieved.roleRank)
        assertEquals(4, retrieved.roleRank.level)
        assertTrue(retrieved.canRead)
        assertTrue(retrieved.canWrite)
        assertTrue(retrieved.canExecute)
        assertFalse(retrieved.canAdminister)
    }

    @Test
    fun `verify root admin can grant master override to key operative`() = runBlocking {
        val users = repository.getAllUsers().first()
        val specialist = users.first { it.roleRank == RoleRank.SPECIALIST }

        val empoweredSpecialist = specialist.copy(
            roleRank = RoleRank.GENERAL,
            canRead = true,
            canWrite = true,
            canExecute = true,
            canAdminister = true,
            isMasterOverride = true
        )

        repository.updateUser(empoweredSpecialist)

        val updatedUsers = repository.getAllUsers().first()
        val retrieved = updatedUsers.first { it.id == specialist.id }

        assertEquals(RoleRank.GENERAL, retrieved.roleRank)
        assertTrue(retrieved.isMasterOverride)
        assertTrue(retrieved.canAdminister)
    }

    @Test
    fun `verify audit log records administrative operations with root fidelity`() = runBlocking {
        repository.logAction(
            actorName = "الأب ياسر (الرئيس الأعلى)",
            actorRole = "Root Sovereign Authority",
            actionAr = "تعديل رتبة وصلاحيات المشرف العام",
            actionEn = "Updated General Supervisor Privileges",
            level = com.example.data.model.LogSeverity.MASTER_OVERRIDE,
            details = "Granted full Root Admin Privileges"
        )

        val logs = repository.getAllLogs().first()
        val latest = logs.firstOrNull { it.actionAr == "تعديل رتبة وصلاحيات المشرف العام" }
        assertNotNull(latest)
        assertEquals("الأب ياسر (الرئيس الأعلى)", latest!!.actorName)
        assertEquals(com.example.data.model.LogSeverity.MASTER_OVERRIDE, latest.level)
    }

    @Test
    fun `verify firestore audit log document conversion`() {
        val firestoreLog = com.example.data.firestore.FirestoreAuditLog(
            id = "audit-12345",
            timestamp = "2026-08-23 03:20:00",
            actorId = "usr-supreme",
            actorEmail = "yasiralcasr@gmail.com",
            actorName = "الأب ياسر",
            actorRole = "Supreme Commander",
            actorRankLevel = 6,
            actionAr = "توثيق عملية سيادية سحابية",
            actionEn = "Documented Sovereign Cloud Action",
            level = "MASTER_OVERRIDE",
            isRootAction = true,
            isMasterOverride = true
        )

        val map = firestoreLog.toMap()
        assertEquals("audit-12345", map["id"])
        assertEquals("yasiralcasr@gmail.com", map["actorEmail"])
        assertEquals(6, map["actorRankLevel"])
        assertEquals(true, map["isRootAction"])
        assertEquals(true, map["isMasterOverride"])
    }
}
