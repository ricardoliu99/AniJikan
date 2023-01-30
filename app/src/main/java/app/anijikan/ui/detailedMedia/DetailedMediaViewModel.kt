package app.anijikan.ui.detailedMedia

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.anijikan.data.database.domainAsDatabaseCharacter
import app.anijikan.data.database.domainAsDatabaseDetailedMedia
import app.anijikan.data.domain.DetailedMedia
import app.anijikan.data.domain.databaseAsDomainDetailedMedia
import app.anijikan.data.domain.networkAsDomainDetailedMedia
import app.anijikan.data.repository.DatabaseMediaRepository
import app.anijikan.data.repository.NetworkMediaRepository
import com.apollographql.apollo3.exception.ApolloNetworkException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface DetailedMediaUiState {

    data class Success(
        val detailedMedia: DetailedMedia
    ) : DetailedMediaUiState

    object Loading : DetailedMediaUiState

    object Error : DetailedMediaUiState
}


class DetailedMediaViewModel(
    savedStateHandle: SavedStateHandle,
    private val networkMediaRepository: NetworkMediaRepository,
    private val databaseMediaRepository: DatabaseMediaRepository
) : ViewModel() {

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private val mediaId: Int = checkNotNull(savedStateHandle[DetailedMediaDestination.mediaIdArg])

    private val _detailedMediaUiState: MutableStateFlow<DetailedMediaUiState> =
        MutableStateFlow(DetailedMediaUiState.Loading)

    private val detailedMediaUiState: StateFlow<DetailedMediaUiState> = _detailedMediaUiState

    private val offlineDetailedMediaUiState: StateFlow<DetailedMediaUiState> =
        databaseMediaRepository
            .getDetailedMediaDataById(mediaId)
            .filterNotNull()
            .map {
                DetailedMediaUiState.Success(
                    databaseAsDomainDetailedMedia(it)
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = DetailedMediaUiState.Loading
            )

    init {
        getCurrentMedia()
    }

    private fun getCurrentMedia() {
        viewModelScope.launch {
            try {
                _detailedMediaUiState.value = DetailedMediaUiState.Success(
                    networkAsDomainDetailedMedia(
                        networkMediaRepository.getDetailedMediaInfoById(
                            mediaId
                        )?.Media
                    )
                )
                databaseMediaRepository.insertDetailedMedia(
                    domainAsDatabaseDetailedMedia((detailedMediaUiState.value as DetailedMediaUiState.Success).detailedMedia)
                )
                databaseMediaRepository.insertCharacter(
                    (detailedMediaUiState.value as DetailedMediaUiState.Success).detailedMedia.characters.mapIndexed { index, character ->
                        domainAsDatabaseCharacter(
                            (detailedMediaUiState.value as DetailedMediaUiState.Success).detailedMedia.id,
                            index,
                            character
                        )
                    }
                )
            } catch (e: ApolloNetworkException) {

                offlineDetailedMediaUiState.collect {
                    if (offlineDetailedMediaUiState.value is DetailedMediaUiState.Success) {
                        _detailedMediaUiState.value = DetailedMediaUiState.Loading
                    } else {
                        _detailedMediaUiState.value = DetailedMediaUiState.Error
                    }
                }
            } catch (e: Exception) {
                _detailedMediaUiState.value = DetailedMediaUiState.Error
                e.printStackTrace()
            }
        }
    }

    fun chooseDetailedMediaUiState(): StateFlow<DetailedMediaUiState> {
        return when (_detailedMediaUiState.value) {
            is DetailedMediaUiState.Success -> detailedMediaUiState
            is DetailedMediaUiState.Loading -> offlineDetailedMediaUiState
            is DetailedMediaUiState.Error -> MutableStateFlow(DetailedMediaUiState.Error)
        }
    }

    fun getMediaId(): Int {
        return mediaId
    }
}