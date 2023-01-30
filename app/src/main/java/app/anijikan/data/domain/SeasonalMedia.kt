package app.anijikan.data.domain

import app.anijikan.SeasonalMediaInfoQuery
import app.anijikan.data.database.DatabaseSeasonalMedia

data class SeasonalMedia(
    val id: Int,
    val description: String,
    val coverImageExtraLarge: String,
    val nextAiringEpisodeEpisode: Int,
    val nextAiringEpisodeAiringAt: Int,
    val titleRomaji: String
)

fun networkAsDomainSeasonalMedia(
    networkMedia: SeasonalMediaInfoQuery.Medium?
): SeasonalMedia {
    return SeasonalMedia(
        id = networkMedia?.id!!,
        description = networkMedia.description ?: "",
        coverImageExtraLarge = networkMedia.coverImage?.extraLarge ?: "",
        nextAiringEpisodeEpisode = networkMedia.nextAiringEpisode?.episode ?: -1,
        nextAiringEpisodeAiringAt = networkMedia.nextAiringEpisode?.airingAt ?: 0,
        titleRomaji = networkMedia.title?.romaji ?: ""
    )
}

fun databaseAsDomainSeasonalMedia(
    databaseMedia: DatabaseSeasonalMedia
): SeasonalMedia {
    return SeasonalMedia(
        id = databaseMedia.id,
        description = databaseMedia.description,
        coverImageExtraLarge = databaseMedia.coverImageExtraLarge,
        nextAiringEpisodeEpisode = databaseMedia.nextAiringEpisodeEpisode,
        nextAiringEpisodeAiringAt = databaseMedia.nextAiringEpisodeAiringAt,
        titleRomaji = databaseMedia.titleRomaji
    )
}