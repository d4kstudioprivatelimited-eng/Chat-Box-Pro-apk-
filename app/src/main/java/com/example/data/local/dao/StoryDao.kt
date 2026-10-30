package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entities.StoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StoryDao {
    @Query("SELECT * FROM stories WHERE expiresAt > :currentTime ORDER BY timestamp DESC")
    fun getActiveStoriesFlow(currentTime: Long = System.currentTimeMillis()): Flow<List<StoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: StoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(stories: List<StoryEntity>)

    @Query("UPDATE stories SET isViewed = 1 WHERE id = :storyId")
    suspend fun markStoryViewed(storyId: String)

    @Query("DELETE FROM stories WHERE expiresAt <= :currentTime")
    suspend fun cleanupExpiredStories(currentTime: Long = System.currentTimeMillis())

    @Query("DELETE FROM stories WHERE id IN ('s_sarah', 's_alex', 's_mom')")
    suspend fun deleteMockStories()

    @Query("DELETE FROM stories")
    suspend fun clearAllStories()
}
