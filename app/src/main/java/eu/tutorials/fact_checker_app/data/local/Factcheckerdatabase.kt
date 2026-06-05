package eu.tutorials.fact_checker_app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [VerificationEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(EvidenceListConverter::class)
abstract class FactCheckerDatabase : RoomDatabase() {

    abstract fun verificationDao(): VerificationDao

    companion object {
        const val DATABASE_NAME = "fact_checker_db"
    }
}
