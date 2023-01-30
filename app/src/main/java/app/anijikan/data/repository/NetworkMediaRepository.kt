package app.anijikan.data.repository

import app.anijikan.DetailedMediaInfoByIdQuery
import app.anijikan.SeasonalMediaInfoQuery
import app.anijikan.type.MediaFormat
import app.anijikan.type.MediaSeason
import app.anijikan.type.MediaType
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional

class NetworkMediaRepository(
    private val apolloClient: ApolloClient
) {
    suspend fun getSeasonalMediaInfo(
        page: Int,
        type: MediaType,
        format: MediaFormat,
        asHtml: Boolean,
        season: MediaSeason,
        seasonYear: Int
    ): SeasonalMediaInfoQuery.Data? {
        return apolloClient
            .query(
                SeasonalMediaInfoQuery(
                    page = Optional.present(page),
                    type = Optional.present(type),
                    format = Optional.present(format),
                    asHtml = Optional.present(asHtml),
                    season = Optional.present(season),
                    seasonYear = Optional.present(seasonYear)
                )
            )
            .execute()
            .data
    }

    suspend fun getDetailedMediaInfoById(id: Int): DetailedMediaInfoByIdQuery.Data? {
        return apolloClient
            .query(
                DetailedMediaInfoByIdQuery(Optional.present(id))
            )
            .execute()
            .data
    }

}