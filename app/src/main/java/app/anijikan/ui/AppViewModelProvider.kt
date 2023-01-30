package app.anijikan.ui


import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import app.anijikan.AniJikanApplication
import app.anijikan.ui.detailedMedia.DetailedMediaViewModel
import app.anijikan.ui.home.HomeViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HomeViewModel(
                aniJikanApplication().container.networkMediaRepository,
                aniJikanApplication().container.databaseMediaRepository
            )
        }
        initializer {
            DetailedMediaViewModel(
                this.createSavedStateHandle(),
                aniJikanApplication().container.networkMediaRepository,
                aniJikanApplication().container.databaseMediaRepository
            )
        }
    }
}

fun CreationExtras.aniJikanApplication(): AniJikanApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as AniJikanApplication)