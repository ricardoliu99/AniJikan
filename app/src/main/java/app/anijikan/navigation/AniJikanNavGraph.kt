package app.anijikan.navigation

import androidx.compose.foundation.clickable
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import app.anijikan.R
import app.anijikan.ui.detailedMedia.DetailedMediaDestination
import app.anijikan.ui.detailedMedia.DetailedMediaScreen
import app.anijikan.ui.home.HomeDestination
import app.anijikan.ui.home.HomeScreen


@Composable
fun AniJikanNavHost(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToDetailedMedia = {
                    navController.navigate("${DetailedMediaDestination.route}/${it}")
                },
            )
        }
        composable(
            route = DetailedMediaDestination.routeWithArgs,
            arguments = listOf(navArgument(DetailedMediaDestination.mediaIdArg) {
                type = NavType.IntType
            })

        ) {
            DetailedMediaScreen(
                navigateToDetailedMedia = {
                    navController.popBackStack()
                    navController.navigate("${DetailedMediaDestination.route}/${it}")
                },
                navigateBack = { navController.popBackStack() }
            )
        }
    }

}

@Composable
fun NavigateBackArrow(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Icon(
        imageVector = Icons.Filled.ArrowBack,
        contentDescription = stringResource(R.string.back),
        modifier = modifier
            .clickable { navigateBack() }
    )
}