package app.anijikan

import android.app.Application
import app.anijikan.data.AppContainer
import app.anijikan.data.AppDataContainer

class AniJikanApplication: Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}