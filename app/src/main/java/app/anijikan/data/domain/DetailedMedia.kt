package app.anijikan.data.domain

import app.anijikan.DetailedMediaInfoByIdQuery
import app.anijikan.data.database.DetailedMediaWithCharacters

data class DetailedMedia(
    val id: Int,
    val description: String,
    val episodes: Int,
    val source: String,
    val coverImageExtraLarge: String,
    val startDateYear: Int,
    val startDateMonth: Int,
    val startDateDay: Int,
    val endDateYear: Int,
    val endDateMonth: Int,
    val endDateDay: Int,
    val titleRomaji: String,
    val titleEnglish: String,
    val animationStudio: String,
    val genres: String,
    val characters: List<Character>
)

data class Character(
    val detailedMediaId: Int,
    val name: String,
    val image: String,
    val role: String,
    val voiceActorName: String,
    val voiceActorImage: String
)

fun networkAsDomainDetailedMedia(
    networkMedia: DetailedMediaInfoByIdQuery.Media?
): DetailedMedia {
    val characters = networkMedia?.characters?.edges?.map { character ->
        Character(
            detailedMediaId = networkMedia.id,
            name = character?.node?.name?.userPreferred ?: "",
            image = character?.node?.image?.large ?: "",
            role = character?.role?.rawValue ?: "",
            voiceActorName = character?.voiceActors?.getOrNull(0)?.name?.userPreferred ?: "",
            voiceActorImage = character?.voiceActors?.getOrNull(0)?.image?.large ?: ""
        )
    }
    return DetailedMedia(
        id = networkMedia?.id!!,
        description = networkMedia.description ?: "",
        episodes = networkMedia.episodes ?: 0,
        source = networkMedia.source?.rawValue?.replace("_", " ") ?: "",
        coverImageExtraLarge = networkMedia.coverImage?.extraLarge ?: "",
        startDateYear = networkMedia.startDate?.year ?: 0,
        startDateMonth = networkMedia.startDate?.month ?: 0,
        startDateDay = networkMedia.startDate?.day ?: 0,
        endDateYear = networkMedia.endDate?.year ?: 0,
        endDateMonth = networkMedia.endDate?.month ?: 0,
        endDateDay = networkMedia.endDate?.day ?: 0,
        titleRomaji = networkMedia.title?.romaji ?: "",
        titleEnglish = networkMedia.title?.english ?: "",
        animationStudio = networkMedia.studios?.nodes?.find { it?.isAnimationStudio ?: false }?.name
            ?: "",
        genres = networkMedia.genres?.joinToString() ?: "",
        characters = characters ?: listOf()
    )
}

fun databaseAsDomainDetailedMedia(
    databaseMedia: DetailedMediaWithCharacters
): DetailedMedia {
    val characters = databaseMedia.characters
        .sortedBy { it.indexInList }
        .map { character ->
            Character(
                detailedMediaId = databaseMedia.detailedMedia.id,
                name = character.name,
                image = character.image,
                role = character.role,
                voiceActorName = character.voiceActorName,
                voiceActorImage = character.voiceActorImage
            )
        }

    return DetailedMedia(
        id = databaseMedia.detailedMedia.id,
        description = databaseMedia.detailedMedia.description,
        episodes = databaseMedia.detailedMedia.episodes,
        source = databaseMedia.detailedMedia.source,
        coverImageExtraLarge = databaseMedia.detailedMedia.coverImageExtraLarge,
        startDateYear = databaseMedia.detailedMedia.startDateYear,
        startDateMonth = databaseMedia.detailedMedia.startDateMonth,
        startDateDay = databaseMedia.detailedMedia.startDateDay,
        endDateYear = databaseMedia.detailedMedia.endDateYear,
        endDateMonth = databaseMedia.detailedMedia.endDateMonth,
        endDateDay = databaseMedia.detailedMedia.endDateDay,
        titleRomaji = databaseMedia.detailedMedia.titleRomaji,
        titleEnglish = databaseMedia.detailedMedia.titleEnglish,
        animationStudio = databaseMedia.detailedMedia.animationStudio,
        genres = databaseMedia.detailedMedia.genres,
        characters = characters
    )
}
