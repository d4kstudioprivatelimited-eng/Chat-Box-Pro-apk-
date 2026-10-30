package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entities.CallEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CallDao {
    @Query("SELECT * FROM calls ORDER BY timestamp DESC")
    fun getAllCallsFlow(): Flow<List<CallEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCall(call: CallEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalls(calls: List<CallEntity>)

    @Query("DELETE FROM calls WHERE id = :callId")
    suspend fun deleteCall(callId: String)

    @Query("DELETE FROM calls")
    suspend fun clearCallHistory()
}
