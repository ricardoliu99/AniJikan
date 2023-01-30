package app.anijikan.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.anijikan.SeasonalMediaInfoQuery
import app.anijikan.data.database.domainAsDatabaseSeasonalMedia
import app.anijikan.data.domain.SeasonalMedia
import app.anijikan.data.domain.databaseAsDomainSeasonalMedia
import app.anijikan.data.domain.networkAsDomainSeasonalMedia
import app.anijikan.data.repository.DatabaseMediaRepository
import app.anijikan.data.repository.NetworkMediaRepository
import app.anijikan.type.MediaFormat
import app.anijikan.type.MediaSeason
import app.anijikan.type.MediaType
import com.apollographql.apollo3.exception.ApolloNetworkException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


sealed interface HomeUiState {
    var fullMediaList: List<SeasonalMedia>
    var shownMediaList: List<SeasonalMedia>


    data class Success(
        override var fullMediaList: List<SeasonalMedia> = mutableListOf(),
        override var shownMediaList: List<SeasonalMedia> = mutableListOf()
    ) : HomeUiState

    data class OfflineLoading(
        override var fullMediaList: List<SeasonalMedia> = mutableListOf(),
        override var shownMediaList: List<SeasonalMedia> = mutableListOf()
    ) : HomeUiState

    data class Loading(
        override var fullMediaList: List<SeasonalMedia> = mutableListOf(),
        override var shownMediaList: List<SeasonalMedia> = mutableListOf()
    ) : HomeUiState

    data class Error(
        override var fullMediaList: List<SeasonalMedia> = mutableListOf(),
        override var shownMediaList: List<SeasonalMedia> = mutableListOf()
    ) : HomeUiState
}

class HomeViewModel(
    private val networkMediaRepository: NetworkMediaRepository,
    private val databaseMediaRepository: DatabaseMediaRepository
) : ViewModel() {
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private var _homeUiState: MutableStateFlow<HomeUiState> =
        MutableStateFlow(HomeUiState.Loading())

    val homeUiState: StateFlow<HomeUiState> = _homeUiState

    var offlineHomeUiState: StateFlow<HomeUiState> = databaseMediaRepository
        .getAllSeasonalMedia(getSeason().rawValue)
        .map {
            val seasonalMediaList = it.map { media ->
                databaseAsDomainSeasonalMedia(media)
            }
            HomeUiState.Success(
                fullMediaList = seasonalMediaList,
                shownMediaList = seasonalMediaList
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = HomeUiState.Loading() as HomeUiState
        )

    var weekDayMap: Map<Int, MutableList<SeasonalMedia>> by mutableStateOf(
        mapOf(
            Pair(1, mutableListOf()),
            Pair(2, mutableListOf()),
            Pair(3, mutableListOf()),
            Pair(4, mutableListOf()),
            Pair(5, mutableListOf()),
            Pair(6, mutableListOf()),
            Pair(7, mutableListOf())
        )
    )

    init {
        getSeasonalMediaData()
        setUpWeekDayMap()
    }

    fun getSeasonalMediaData(
        season: MediaSeason = getSeason(),
        seasonYear: Int = getCurrentYear()
    ) {
        viewModelScope.launch {
            _homeUiState.value = HomeUiState.Loading()
            var seasonalMediaData: SeasonalMediaInfoQuery.Data?
            try {
                var pageNum = 1
                do {
                    seasonalMediaData = networkMediaRepository.getSeasonalMediaInfo(
                        page = pageNum,
                        type = MediaType.ANIME,
                        format = MediaFormat.TV,
                        asHtml = true,
                        season = season,
                        seasonYear = seasonYear
                    )
                    val currentList: List<SeasonalMedia> =
                        seasonalMediaData?.Page?.media?.map { networkAsDomainSeasonalMedia(it) }
                            ?: listOf()

                    _homeUiState.value =
                        HomeUiState.Loading(homeUiState.value.fullMediaList.plus(currentList))

                    pageNum++
                } while (seasonalMediaData?.Page?.pageInfo?.hasNextPage == true)

                _homeUiState.value.fullMediaList =
                    homeUiState.value.fullMediaList.sortedBy { it.titleRomaji }

                _homeUiState.value = HomeUiState.Success(
                    fullMediaList = homeUiState.value.fullMediaList,
                    shownMediaList = homeUiState.value.fullMediaList
                )
                databaseMediaRepository.insertAllSeasonalMedia(
                    homeUiState.value.fullMediaList.map { seasonalMedia ->
                        domainAsDatabaseSeasonalMedia(seasonalMedia, season.rawValue)
                    }
                )
            } catch (e: ApolloNetworkException) {
                offlineHomeUiState = databaseMediaRepository
                    .getAllSeasonalMedia(season.rawValue)
                    .map {
                        val seasonalMediaList = it.map { media ->
                            databaseAsDomainSeasonalMedia(media)
                        }
                        HomeUiState.Success(
                            fullMediaList = seasonalMediaList,
                            shownMediaList = seasonalMediaList
                        )
                    }.stateIn(
                        scope = viewModelScope,
                        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                        initialValue = HomeUiState.Loading() as HomeUiState
                    )
                offlineHomeUiState.collect {
                    if (offlineHomeUiState.value is HomeUiState.Success) {
                        _homeUiState.value = HomeUiState.OfflineLoading()
                    } else {
                        _homeUiState.value = HomeUiState.Error()
                    }
                }
            } catch (e: Exception) {
                _homeUiState.value = HomeUiState.Error()
                e.printStackTrace()
            }
        }
    }

    private fun chooseHomeUiState(): StateFlow<HomeUiState> {
        return when (_homeUiState.value) {
            is HomeUiState.Success -> homeUiState
            is HomeUiState.OfflineLoading -> offlineHomeUiState
            is HomeUiState.Loading -> MutableStateFlow(HomeUiState.Loading())
            is HomeUiState.Error -> MutableStateFlow(HomeUiState.Error())
        }
    }

    fun getSeason(month: Int = Calendar.getInstance().get(Calendar.MONTH)): MediaSeason {
        return when (month) {
            0, 1, 2 -> MediaSeason.WINTER
            3, 4, 5 -> MediaSeason.SPRING
            6, 7, 8 -> MediaSeason.SUMMER
            else -> MediaSeason.FALL
        }
    }

    private fun getCurrentYear(): Int {
        return Calendar.getInstance().get(Calendar.YEAR)
    }

    private fun setUpWeekDayMap() {
        viewModelScope.launch {
            _homeUiState.collect {
                weekDayMap = mapOf(
                    Pair(1, mutableListOf()),
                    Pair(2, mutableListOf()),
                    Pair(3, mutableListOf()),
                    Pair(4, mutableListOf()),
                    Pair(5, mutableListOf()),
                    Pair(6, mutableListOf()),
                    Pair(7, mutableListOf())
                )
                val shownMediaList = chooseHomeUiState().value.shownMediaList

                shownMediaList.forEach { media ->
                    val formatter = SimpleDateFormat("EEE", Locale.getDefault())
                    val dateInMillis = Date(media.nextAiringEpisodeAiringAt.toLong().times(1000))
                    when (formatter.format(dateInMillis)) {
                        "Sun" -> weekDayMap[1]?.add(media)
                        "Mon" -> weekDayMap[2]?.add(media)
                        "Tue" -> weekDayMap[3]?.add(media)
                        "Wed" -> weekDayMap[4]?.add(media)
                        "Thu" -> weekDayMap[5]?.add(media)
                        "Fri" -> weekDayMap[6]?.add(media)
                        "Sat" -> weekDayMap[7]?.add(media)

                    }
                }
                weekDayMap.forEach { (key) ->
                    weekDayMap[key]?.sortBy { media ->
                        media.nextAiringEpisodeAiringAt
                    }
                }
            }
        }
    }

    fun searchFilter(search: String) {
        val shownMediaList = chooseHomeUiState().value.fullMediaList.filter { media ->
            media.titleRomaji.contains(search, ignoreCase = true)
        }
        _homeUiState.value = HomeUiState.Success(
            fullMediaList = chooseHomeUiState().value.fullMediaList,
            shownMediaList = shownMediaList
        )
    }

}

