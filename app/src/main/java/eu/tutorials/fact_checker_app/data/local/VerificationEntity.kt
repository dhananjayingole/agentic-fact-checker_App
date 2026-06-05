package eu.tutorials.fact_checker_app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// ── Type Converters ───────────────────────────────────────────

class StringListConverter {
    private val gson = Gson()
    @TypeConverter fun fromList(list: List<String>): String = gson.toJson(list)
    @TypeConverter fun toList(json: String): List<String> =
        gson.fromJson(json, object : TypeToken<List<String>>() {}.type) ?: emptyList()
}

class EvidenceListConverter {
    private val gson = Gson()
    @TypeConverter fun fromList(list: List<EvidenceEntity>): String = gson.toJson(list)
    @TypeConverter fun toList(json: String): List<EvidenceEntity> =
        gson.fromJson(json, object : TypeToken<List<EvidenceEntity>>() {}.type) ?: emptyList()
}

// ── Evidence embedded model ───────────────────────────────────

data class EvidenceEntity(
    val title: String,
    val url: String,
    val snippet: String,
    val relevanceScore: Double,
    val stance: String,
    val sourceCredibility: String,
    val sourceType: String
)

// ── Main verification entity ──────────────────────────────────

@Entity(tableName = "verifications")
@TypeConverters(EvidenceListConverter::class)
data class VerificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val claim: String,
    val verdict: String,            // "TRUE" | "FALSE" | "INCONCLUSIVE" | "UNVERIFIABLE"
    val confidenceScore: Double,
    val evidenceCount: Int,
    val evidenceList: List<EvidenceEntity>,
    val evidenceSummary: String,
    val reasoning: String,
    val processingTimeSeconds: Double,
    val sourcesSearched: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)