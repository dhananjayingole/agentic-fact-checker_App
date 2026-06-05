package eu.tutorials.fact_checker_app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VerificationDao {

    // ── Insert / Update ───────────────────────────────────────
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(verification: VerificationEntity): Long

    @Update
    suspend fun update(verification: VerificationEntity)

    // ── Queries ───────────────────────────────────────────────

    @Query("SELECT * FROM verifications ORDER BY timestamp DESC")
    fun getAllFlow(): Flow<List<VerificationEntity>>

    @Query("SELECT * FROM verifications WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): VerificationEntity?

    @Query("""
        SELECT * FROM verifications 
        WHERE claim LIKE '%' || :query || '%'
           OR evidenceSummary LIKE '%' || :query || '%'
        ORDER BY timestamp DESC
    """)
    fun searchFlow(query: String): Flow<List<VerificationEntity>>

    @Query("SELECT * FROM verifications WHERE verdict = :verdict ORDER BY timestamp DESC")
    fun getByVerdictFlow(verdict: String): Flow<List<VerificationEntity>>

    @Query("SELECT * FROM verifications WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoritesFlow(): Flow<List<VerificationEntity>>

    // ── Stats ─────────────────────────────────────────────────
    @Query("SELECT COUNT(*) FROM verifications")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM verifications WHERE verdict = :verdict")
    fun getCountByVerdict(verdict: String): Flow<Int>

    // ── Delete ────────────────────────────────────────────────
    @Delete
    suspend fun delete(verification: VerificationEntity)

    @Query("DELETE FROM verifications")
    suspend fun deleteAll()

    // ── Favorite toggle ───────────────────────────────────────
    @Query("UPDATE verifications SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Long, isFavorite: Boolean)
}