package app.anijikan.ui.detailedMedia

import android.os.Build
import android.text.Html
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.anijikan.navigation.NavigationDestination
import app.anijikan.ui.AppViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import app.anijikan.R
import app.anijikan.data.domain.DetailedMedia
import app.anijikan.navigation.NavigateBackArrow
import app.anijikan.ui.ErrorScreen
import app.anijikan.ui.ErrorScreenWithBackArrow
import app.anijikan.ui.LoadingScreen
import coil.compose.AsyncImage
import coil.request.ImageRequest

object DetailedMediaDestination : NavigationDestination {
    override val route: String = "detailed_media"
    const val mediaIdArg = "mediaId"
    val routeWithArgs = "$route/{$mediaIdArg}"
}

@Composable
fun DetailedMediaScreen(
    navigateToDetailedMedia: (Int) -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailedMediaViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val detailedMediaUiState by viewModel.chooseDetailedMediaUiState().collectAsState()

    when (detailedMediaUiState) {
        is DetailedMediaUiState.Success -> SuccessScreen(detailedMediaUiState, navigateBack, modifier)
        is DetailedMediaUiState.Loading -> LoadingScreen()
        is DetailedMediaUiState.Error -> ErrorScreenWithBackArrow(
            refresh = { navigateToDetailedMedia(viewModel.getMediaId()) },
            navigateBack = navigateBack
        )
    }
}

@Composable
private fun SuccessScreen(
    detailedMediaUiState: DetailedMediaUiState,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val detailedMedia = (detailedMediaUiState as DetailedMediaUiState.Success).detailedMedia
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(8.dp)
            .verticalScroll(rememberScrollState())
    ) {

        DetailedMediaHeader(
            detailedMedia = detailedMedia,
            navigateBack = navigateBack
        )

        DetailsSection(detailedMedia = detailedMedia)

        Spacer(modifier = Modifier.height(8.dp))

        SynopsisSection(detailedMedia = detailedMedia)

        Spacer(modifier = Modifier.height(8.dp))

        CharacterVASection(detailedMedia = detailedMedia)
    }
}

@Composable
private fun DetailedMediaHeader(
    detailedMedia: DetailedMedia,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {

        NavigateBackArrow(
            navigateBack = navigateBack,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = detailedMedia.titleRomaji,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h6,
            modifier = Modifier.weight(24f)
        )
    }
}

@Composable
private fun CharacterVASection(
    detailedMedia: DetailedMedia,
    modifier: Modifier = Modifier
) {

    LazyRow(
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        items(items = detailedMedia.characters) { item ->
            CharacterVAItem(character = item)
        }
    }

}

@Composable
private fun CharacterVAItem(
    character: app.anijikan.data.domain.Character,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .width(IntrinsicSize.Max)
    )
    {
        Card(
            elevation = 8.dp,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.size(120.dp, 170.dp)
        ) {
            Box {
                AsyncImage(
                    model = ImageRequest.Builder(context = LocalContext.current)
                        .data(character.image)
                        .crossfade(true)
                        .build(),
                    contentScale = ContentScale.FillBounds,
                    contentDescription = character.name,
                    modifier = Modifier.fillMaxHeight()
                )

                Text(
                    text = character.role,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier
                        .background(color = Color(0f, 0f, 0f, 0.5f))
                        .align(Alignment.TopStart)
                        .padding(4.dp)
                )
                Text(
                    text = character.name,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier
                        .background(color = Color(0f, 0f, 0f, 0.5f))
                        .align(Alignment.BottomCenter)
                        .padding(4.dp)
                        .fillMaxWidth()
                )
            }
        }

        Card(
            elevation = 8.dp,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.size(120.dp, 170.dp)
        ) {

            Box {
                AsyncImage(
                    model = ImageRequest.Builder(context = LocalContext.current)
                        .data(character.voiceActorImage)
                        .crossfade(true)
                        .build(),
                    contentScale = ContentScale.FillBounds,
                    contentDescription = character.voiceActorName,
                    modifier = Modifier.fillMaxHeight()
                )
                Text(
                    text = character.voiceActorName,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier
                        .background(color = Color(0f, 0f, 0f, 0.5f))
                        .align(Alignment.BottomCenter)
                        .padding(4.dp)
                        .fillMaxWidth()
                )

            }
        }
    }
}


@Composable
private fun SynopsisSection(
    detailedMedia: DetailedMedia,
    modifier: Modifier = Modifier
) {
    val parsedDescription = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(detailedMedia.description, Html.FROM_HTML_MODE_LEGACY).toString()
    } else {
        Html.fromHtml(detailedMedia.description).toString()
    }

    Card(
        elevation = 8.dp,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(4.dp)
        ) {
            Text(
                text = stringResource(R.string.synopsis),
                style = MaterialTheme.typography.subtitle1,
                textAlign = TextAlign.Start,
            )
            Divider(thickness = 2.dp)
            Text(
                text = parsedDescription,
                style = MaterialTheme.typography.body1
            )
        }
    }
}

@Composable
private fun DetailsSection(
    detailedMedia: DetailedMedia,
    modifier: Modifier = Modifier
) {
    Card(
        elevation = 8.dp,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.padding(4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = modifier
                .padding(4.dp)
                .wrapContentSize()
        ) {

            AsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(detailedMedia.coverImageExtraLarge)
                    .crossfade(true)
                    .build(),
                contentDescription = detailedMedia.titleRomaji,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.clip(RoundedCornerShape(8.dp))
            )

            Details(detailedMedia = detailedMedia)

        }
    }
}

@Composable
private fun Details(
    detailedMedia: DetailedMedia
) {
    val formattedStartDate =
        "${detailedMedia.startDateYear}-${detailedMedia.startDateMonth}-${detailedMedia.startDateDay}"
    val formattedEndDate = "${detailedMedia.endDateYear}-${detailedMedia.endDateMonth}-${detailedMedia.endDateDay}"

    Divider(thickness = 2.dp)
    Text(text = stringResource(R.string.alternate_name), style = MaterialTheme.typography.caption)
    Text(
        text = detailedMedia.titleEnglish,
        style = MaterialTheme.typography.caption,
        maxLines = 1,
        modifier = Modifier.horizontalScroll(rememberScrollState())
    )
    Divider(thickness = 2.dp)
    Text(text = stringResource(R.string.genre), style = MaterialTheme.typography.caption)
    Text(
        text = detailedMedia.genres,
        style = MaterialTheme.typography.caption,
        maxLines = 1,
        modifier = Modifier.horizontalScroll(rememberScrollState())
    )
    Divider(thickness = 2.dp)
    Text(text = stringResource(R.string.source), style = MaterialTheme.typography.caption)
    Text(text = detailedMedia.source, style = MaterialTheme.typography.caption)
    Divider(thickness = 2.dp)
    Text(text = stringResource(R.string.episode_count), style = MaterialTheme.typography.caption)
    Text(
        text = detailedMedia.episodes.toString(),
        style = MaterialTheme.typography.caption
    )
    Divider(thickness = 2.dp)
    Text(text = stringResource(R.string.start_date), style = MaterialTheme.typography.caption)
    Text(text = formattedStartDate, style = MaterialTheme.typography.caption)
    Divider(thickness = 2.dp)
    Text(text = stringResource(R.string.end_date), style = MaterialTheme.typography.caption)
    Text(text = formattedEndDate, style = MaterialTheme.typography.caption)
    Divider(thickness = 2.dp)
    Text(text = stringResource(R.string.studio), style = MaterialTheme.typography.caption)
    Text(text = detailedMedia.animationStudio, style = MaterialTheme.typography.caption)
    Divider(thickness = 2.dp)

}