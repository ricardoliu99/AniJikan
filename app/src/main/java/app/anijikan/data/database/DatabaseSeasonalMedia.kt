package app.anijikan.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import app.anijikan.data.domain.SeasonalMedia

@Entity(tableName = "seasonal_media")
data class DatabaseSeasonalMedia(
    @PrimaryKey
    val id: Int,
    val description: String,
    val coverImageExtraLarge: String,
    val nextAiringEpisodeEpisode: Int,
    val nextAiringEpisodeAiringAt: Int,
    val titleRomaji: String,
    val season: String
)

fun domainAsDatabaseSeasonalMedia(
    seasonalMedia: SeasonalMedia,
    season: String
): DatabaseSeasonalMedia {
    return DatabaseSeasonalMedia(
        id = seasonalMedia.id,
        description = seasonalMedia.description,
        coverImageExtraLarge = seasonalMedia.coverImageExtraLarge,
        nextAiringEpisodeEpisode = seasonalMedia.nextAiringEpisodeEpisode,
        nextAiringEpisodeAiringAt = seasonalMedia.nextAiringEpisodeAiringAt,
        titleRomaji = seasonalMedia.titleRomaji,
        season = season
    )
}