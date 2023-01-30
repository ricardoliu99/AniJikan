package app.anijikan.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DetailedMediaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetailedMedia(detailedMedia: DatabaseDetailedMedia)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacters(characters: List<DatabaseCharacter>)

    @Transaction
    @Query("SELECT * FROM detailed_media WHERE id = :id")
    fun getDetailedMediaDataById(id: Int): Flow<DetailedMediaWithCharacters>
}