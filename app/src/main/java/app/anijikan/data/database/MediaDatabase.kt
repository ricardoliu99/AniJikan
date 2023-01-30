package app.anijikan.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [DatabaseSeasonalMedia::class, DatabaseDetailedMedia::class, DatabaseCharacter::class],
    version = 1,
    exportSchema = false)
abstract class MediaDatabase : RoomDatabase() {

    abstract fun seasonalMediaDao(): SeasonalMediaDao

    abstract fun detailedMediaDao(): DetailedMediaDao

    companion object {
        @Volatile
        private var INSTANCE: MediaDatabase? = null

        fun getDatabase(context: Context): MediaDatabase {
            return INSTANCE ?: synchronized(this) {
                Room
                    .databaseBuilder(context, MediaDatabase::class.java, "media_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}