package com.example.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgramDao {
    @Query("SELECT * FROM programs ORDER BY createdAt DESC")
    fun getAllPrograms(): Flow<List<ProgramEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgram(program: ProgramEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(programs: List<ProgramEntity>)

    @Delete
    suspend fun deleteProgram(program: ProgramEntity)

    @Query("DELETE FROM programs")
    suspend fun clearAll()
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM industrial_orders ORDER BY orderTimestamp DESC")
    fun getAllOrders(): Flow<List<IndustrialOrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: IndustrialOrderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(orders: List<IndustrialOrderEntity>)

    @Query("UPDATE industrial_orders SET status = :newStatus WHERE orderId = :orderId")
    suspend fun updateOrderStatus(orderId: String, newStatus: String)

    @Query("DELETE FROM industrial_orders")
    suspend fun clearAll()
}

@Dao
interface UserDao {
    @Query("SELECT * FROM user_accounts ORDER BY id ASC")
    fun getAllUsers(): Flow<List<UserAccountEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserAccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<UserAccountEntity>)

    @Delete
    suspend fun deleteUser(user: UserAccountEntity)

    @Query("DELETE FROM user_accounts")
    suspend fun clearAll()
}

@Dao
interface AuditDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AuditLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(logs: List<AuditLogEntity>)

    @Query("DELETE FROM audit_logs")
    suspend fun clearAll()
}
