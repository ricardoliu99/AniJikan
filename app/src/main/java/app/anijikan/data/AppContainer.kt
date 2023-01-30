package app.anijikan.data

import android.content.Context
import app.anijikan.data.database.MediaDatabase
import app.anijikan.data.repository.DatabaseMediaRepository
import app.anijikan.data.repository.NetworkMediaRepository
import com.apollographql.apollo3.ApolloClient

interface AppContainer {
    val networkMediaRepository: NetworkMediaRepository
    val databaseMediaRepository: DatabaseMediaRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    private var apolloClient: ApolloClient = ApolloClient.Builder()
        .serverUrl("https://graphql.anilist.co")
        .build()

    override val networkMediaRepository: NetworkMediaRepository by lazy {
        NetworkMediaRepository(apolloClient)
    }

    override val databaseMediaRepository: DatabaseMediaRepository by lazy {
        DatabaseMediaRepository(
            MediaDatabase.getDatabase(context).seasonalMediaDao(),
            MediaDatabase.getDatabase(context).detailedMediaDao()
        )
    }

}