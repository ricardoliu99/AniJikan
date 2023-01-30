package app.anijikan.data.repository

import app.anijikan.data.database.*

import kotlinx.coroutines.flow.Flow

class DatabaseMediaRepository(
    private val seasonalMediaDao: SeasonalMediaDao,
    private val detailedMediaDao: DetailedMediaDao
) {
    suspend fun insertAllSeasonalMedia(mediaList: List<DatabaseSeasonalMedia>) =
        seasonalMediaDao.insertAllSeasonalMedia(mediaList)

    fun getAllSeasonalMedia(season: String): Flow<List<DatabaseSeasonalMedia>> =
        seasonalMediaDao.getAllSeasonalMediaData(season)

    suspend fun insertDetailedMedia(detailedMedia: DatabaseDetailedMedia) =
        detailedMediaDao.insertDetailedMedia(detailedMedia)

    suspend fun insertCharacter(characters: List<DatabaseCharacter>) =
        detailedMediaDao.insertCharacters(characters)

    fun getDetailedMediaDataById(id: Int): Flow<DetailedMediaWithCharacters> =
        detailedMediaDao.getDetailedMediaDataById(id)
}