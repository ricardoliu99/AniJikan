package app.anijikan.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SeasonalMediaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSeasonalMedia(seasonalMedia: List<DatabaseSeasonalMedia>)

    @Query("SELECT * FROM seasonal_media WHERE season = :season ORDER BY titleRomaji")
    fun getAllSeasonalMediaData(season: String): Flow<List<DatabaseSeasonalMedia>>
    
}