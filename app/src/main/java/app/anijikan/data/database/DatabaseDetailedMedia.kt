package app.anijikan.data.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import app.anijikan.data.domain.DetailedMedia

@Entity(tableName = "detailed_media")
data class DatabaseDetailedMedia(
    @PrimaryKey
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
    val genres: String
)

@Entity(tableName = "character", primaryKeys = ["detailedMediaId", "name"])
data class DatabaseCharacter(
    val detailedMediaId: Int,
    val name: String,
    val image: String,
    val role: String,
    val voiceActorName: String,
    val voiceActorImage: String,
    val indexInList: Int
)

data class DetailedMediaWithCharacters(
    @Embedded
    val detailedMedia: DatabaseDetailedMedia,
    @Relation(
        parentColumn = "id",
        entityColumn = "detailedMediaId"
    )
    val characters: List<DatabaseCharacter>
)

fun domainAsDatabaseCharacter(
    detailedMediaId: Int,
    indexInList: Int,
    character: app.anijikan.data.domain.Character
): DatabaseCharacter {
    return DatabaseCharacter(
        detailedMediaId = detailedMediaId,
        name = character.name,
        image = character.image,
        role = character.role,
        voiceActorName = character.voiceActorName,
        voiceActorImage = character.voiceActorImage,
        indexInList = indexInList
    )
}

fun domainAsDatabaseDetailedMedia(
    detailedMedia: DetailedMedia
): DatabaseDetailedMedia {
    return DatabaseDetailedMedia (
        id = detailedMedia.id,
        description = detailedMedia.description,
        episodes = detailedMedia.episodes,
        source = detailedMedia.source,
        coverImageExtraLarge = detailedMedia.coverImageExtraLarge,
        startDateYear = detailedMedia.startDateYear,
        startDateMonth = detailedMedia.startDateMonth,
        startDateDay = detailedMedia.startDateDay,
        endDateYear = detailedMedia.endDateYear,
        endDateMonth = detailedMedia.endDateMonth,
        endDateDay = detailedMedia.endDateDay,
        titleRomaji = detailedMedia.titleRomaji,
        titleEnglish = detailedMedia.titleEnglish,
        animationStudio = detailedMedia.animationStudio,
        genres = detailedMedia.genres
    )
}
