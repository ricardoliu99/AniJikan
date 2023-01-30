package app.anijikan.ui.home

import android.os.Build
import android.text.Html
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.anijikan.R
import app.anijikan.ui.theme.AniJikanTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import app.anijikan.navigation.NavigationDestination
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.viewmodel.compose.viewModel
import app.anijikan.data.domain.SeasonalMedia
import app.anijikan.notification.createNotificationChannel
import app.anijikan.notification.setAlarm
import app.anijikan.type.MediaSeason
import app.anijikan.ui.AppViewModelProvider
import app.anijikan.ui.ErrorScreen
import app.anijikan.ui.LoadingScreen
import coil.compose.AsyncImage
import coil.request.ImageRequest

object HomeDestination : NavigationDestination {
    override val route: String = "home"
}

private const val SEASONAL_MONTH_FACTOR = 3

@Composable
fun HomeScreen(
    navigateToDetailedMedia: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    var tabState by rememberSaveable {
        mutableStateOf(Calendar.getInstance().get(Calendar.MONTH) / SEASONAL_MONTH_FACTOR)
    }
    when (homeUiState) {
        is HomeUiState.Success -> {
            HomeScreenContent(
                homeUiState = homeUiState,
                weekDayMap = viewModel.weekDayMap,
                changeTabs = {
                    viewModel.getSeasonalMediaData(
                        viewModel.getSeason(tabState * SEASONAL_MONTH_FACTOR)
                    )
                             },
                tabState = tabState,
                changeTabState = { newState -> tabState = newState },
                searchFilter = { search ->
                    viewModel.searchFilter(search)
                },
                navigateToDetailedMedia = navigateToDetailedMedia,
                modifier
            )
        }
        is HomeUiState.OfflineLoading -> {
            val offlineHomeUiState by viewModel.offlineHomeUiState.collectAsState()
            HomeScreenContent(
                homeUiState = offlineHomeUiState,
                weekDayMap = viewModel.weekDayMap,
                changeTabs = {
                    viewModel.getSeasonalMediaData(
                        viewModel.getSeason(tabState * SEASONAL_MONTH_FACTOR)
                    )
                },
                tabState = tabState,
                changeTabState = { newState -> tabState = newState },
                searchFilter = { search ->
                    viewModel.searchFilter(search)
                },
                navigateToDetailedMedia = navigateToDetailedMedia,
                modifier = modifier,
                isOffline = true
            )
        }
        is HomeUiState.Loading -> LoadingScreen()
        is HomeUiState.Error -> ErrorScreen()
    }

}

@Composable
fun HomeScreenContent(
    homeUiState: HomeUiState,
    weekDayMap: Map<Int, List<SeasonalMedia>>,
    changeTabs: () -> Unit,
    tabState: Int,
    changeTabState: (Int) -> Unit,
    searchFilter: (String) -> Unit,
    navigateToDetailedMedia: (Int) -> Unit,
    modifier: Modifier = Modifier,
    isOffline: Boolean = false
) {
    val shownMediaList = (homeUiState as HomeUiState.Success).shownMediaList
    val fullMediaList = homeUiState.fullMediaList

    var calendarView by rememberSaveable {
        mutableStateOf(false)
    }

    val isCurrentSeason =
        Calendar.getInstance().get(Calendar.MONTH) / SEASONAL_MONTH_FACTOR == tabState

    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier
            .fillMaxSize()
    ) {
        SeasonalTab(
            changeTabs,
            tabState,
            changeTabState
        )

        SearchFilter(searchFilter)

        if (isCurrentSeason) {
            CalendarViewSwitchButton({ calendarView = !calendarView }, calendarView)
        }

        if (calendarView && isCurrentSeason) {
            CalendarView(weekDayMap)
        } else {
            if (fullMediaList.isEmpty() && isOffline) {
                ErrorScreen(refresh = changeTabs)
            } else {
                MediaGridList(shownMediaList, navigateToDetailedMedia)
            }
        }
    }
}

@Composable
fun SearchFilter(searchFilter: (String) -> Unit) {
    var search by rememberSaveable {
        mutableStateOf("")
    }
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = search,
        onValueChange = { newValue ->
            search = newValue
            searchFilter(search)
        },
        label = { Text("Enter anime title") },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
            }
        ),
        trailingIcon = {
            if (search.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription = stringResource(R.string.clear_search),
                    modifier = Modifier.clickable {
                        search = ""
                        searchFilter(search)
                    }
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    )
}

@Composable
private fun CalendarViewSwitchButton(calendarViewOnClick: () -> Unit, calendarView: Boolean) {
    IconButton(
        onClick = { calendarViewOnClick() },
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
    ) {
        Icon(
            painter =
            if (calendarView)
                painterResource(R.drawable.ic_baseline_grid_view_24)
            else
                painterResource(R.drawable.ic_baseline_calendar_month_24),
            contentDescription = stringResource(R.string.calendar_view)
        )
    }
}

@Composable
private fun CalendarView(
    weekDayMap: Map<Int, List<SeasonalMedia>>,
    modifier: Modifier = Modifier
) {
    val currentWeekDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
    val weekDayNamesMap = mapOf(
        Pair(1, stringResource(R.string.sunday)), Pair(2, stringResource(R.string.monday)),
        Pair(3, stringResource(R.string.tuesday)), Pair(4, stringResource(R.string.wednesday)),
        Pair(5, stringResource(R.string.thursday)), Pair(6, stringResource(R.string.friday)),
        Pair(7, stringResource(R.string.saturday))
    )

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(vertical = 2.dp, horizontal = 4.dp),
        modifier = modifier
            .fillMaxWidth()
    ) {

        for (i in 0..6) {
            item {
                CalendarTitle(i, currentWeekDay, weekDayNamesMap)
            }

            items(
                items = weekDayMap[(currentWeekDay + i - 1) % 7 + 1]!!
                    .sortedBy { it.nextAiringEpisodeAiringAt }
            ) { item ->
                if (item.nextAiringEpisodeEpisode > 0)
                    CalendarContent(item)
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun CalendarTitle(
    i: Int,
    currentWeekDay: Int,
    weekDayNamesMap: Map<Int, String>,
    modifier: Modifier = Modifier
) {
    val formatter = SimpleDateFormat("MMM d", Locale.getDefault())
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DATE, i)
    val monthDay = formatter.format(calendar.time)
    Card(
        elevation = 8.dp,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text =
            "${weekDayNamesMap[(currentWeekDay + i - 1) % 7 + 1]!!} $monthDay",
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun CalendarContent(
    item: SeasonalMedia,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val channelId = stringResource(R.string.reminder_channel)
    LaunchedEffect(Unit) {
        createNotificationChannel(channelId, context)
    }


    val formatter = SimpleDateFormat("HH:mm:ss z", Locale.getDefault())
    val dateInMillis = Date(item.nextAiringEpisodeAiringAt.toLong().times(1000))
    val formattedAiringAt = formatter.format(dateInMillis)
    val notificationDescription =
        stringResource(R.string.notification_description, item.nextAiringEpisodeEpisode)
    Card(
        elevation = 4.dp,
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = formattedAiringAt)
                Text(text = item.titleRomaji)
            }
            IconButton(onClick = {
                setAlarm(
                    context,
                    item.id,
                    item.nextAiringEpisodeAiringAt.toLong().times(1000),
                    item.id,
                    item.titleRomaji,
                    notificationDescription
                )
                Toast
                    .makeText(
                        context,
                        "Notification scheduled for $formattedAiringAt",
                        Toast.LENGTH_LONG
                    )
                    .show()
            }) {
                Icon(
                    painter = painterResource(R.drawable.ic_baseline_notifications),
                    contentDescription = stringResource(R.string.notification_status_change),
                )
            }
        }
    }
}


@Composable
private fun SeasonalTab(
    changeTabs: () -> Unit,
    tabState: Int,
    changeTabState: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabTitles = listOf(
        stringResource(R.string.winter),
        stringResource(R.string.spring),
        stringResource(R.string.summer),
        stringResource(R.string.fall)
    )

    TabRow(
        selectedTabIndex = tabState,
        modifier = modifier
    ) {
        tabTitles.forEachIndexed { index, title ->
            Tab(
                selected = tabState == index,
                onClick = {
                    changeTabState(index)
                    changeTabs()
                },
                icon = {
                    when (title) {
                        stringResource(R.string.winter) -> Icon(
                            painter = painterResource(id = R.drawable.winter_icon),
                            contentDescription = stringResource(R.string.winter),
                            modifier = Modifier.padding(8.dp)
                        )
                        stringResource(R.string.spring) -> Icon(
                            painter = painterResource(id = R.drawable.spring_icon),
                            contentDescription = stringResource(R.string.spring),
                            modifier = Modifier.padding(8.dp)
                        )
                        stringResource(R.string.summer) -> Icon(
                            painter = painterResource(id = R.drawable.summer_icon),
                            contentDescription = stringResource(R.string.summer),
                            modifier = Modifier.padding(8.dp)
                        )
                        stringResource(R.string.fall) -> Icon(
                            painter = painterResource(id = R.drawable.fall_icon),
                            contentDescription = stringResource(R.string.fall),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun MediaGridList(
    shownMediaList: List<SeasonalMedia>,
    navigateToDetailedMedia: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        contentPadding = PaddingValues(4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(2.dp)
    ) {
        items(items = shownMediaList) { item ->
            MediaItem(navigateToDetailedMedia, media = item)

        }
    }
}

@Composable
private fun MediaItem(
    navigateToDetailedMedia: (Int) -> Unit,
    media: SeasonalMedia,
    modifier: Modifier = Modifier
) {
    var showDetails by rememberSaveable {
        mutableStateOf(false)
    }
    Card(
        elevation = 8.dp,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessVeryLow
                )
            )
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = media.titleRomaji,
                maxLines = 1,
                modifier = Modifier
                    .wrapContentSize(Alignment.Center)
                    .clickable { navigateToDetailedMedia(media.id) }
                    .horizontalScroll(rememberScrollState())
                    .padding(4.dp)
            )
            MediaItemImage(media = media)
            if (showDetails) {
                ShortDetails(media.description)
            }
            IconButton(onClick = { showDetails = !showDetails }) {
                Icon(
                    painter =
                    if (showDetails) painterResource(R.drawable.ic_baseline_expand_less)
                    else painterResource(R.drawable.ic_baseline_expand_more),
                    contentDescription = stringResource(R.string.show_details)
                )
            }
        }
    }
}

@Composable
private fun MediaItemImage(
    media: SeasonalMedia,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(media.coverImageExtraLarge)
                .crossfade(true)
                .build(),
            contentScale = ContentScale.FillWidth,
            contentDescription = media.titleRomaji,
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)

        )

        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.getDefault())
        val dateInMillis = Date(media.nextAiringEpisodeAiringAt.toLong().times(1000))
        val formattedAiringAt = formatter.format(dateInMillis)
        if (media.nextAiringEpisodeEpisode >= 0) {
            Text(
                text = stringResource(
                    R.string.episode_number_time,
                    media.nextAiringEpisodeEpisode,
                    formattedAiringAt
                ),
                color = Color.White,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.caption,
                modifier = Modifier
                    .background(color = Color(0f, 0f, 0f, 0.5f))
                    .fillMaxWidth()
            )
        }
    }
}


@Composable
private fun ShortDetails(description: String, modifier: Modifier = Modifier) {
    val parsedDescription = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(description, Html.FROM_HTML_MODE_LEGACY).toString()
    } else {
        Html.fromHtml(description).toString()
    }
    Text(
        text = parsedDescription,
        textAlign = TextAlign.Left,
        modifier = modifier.padding(2.dp)
    )
}
