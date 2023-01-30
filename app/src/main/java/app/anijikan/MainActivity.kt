package app.anijikan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import app.anijikan.navigation.AniJikanNavHost
import app.anijikan.ui.theme.AniJikanTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AniJikanTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    AniJikanNavHost()
                }
            }
        }
    }
}

@Composable
fun AniJikanApp() {
//    val coroutineScope = rememberCoroutineScope()
//    var response by remember {
//        mutableStateOf<AiringScheduleQuery.Data?>(null)
//    }
//
//    val showSchedule: () -> Unit = {
//        coroutineScope.launch {
//            try {
//                response = apolloClient()
//                    .query(
//                        AiringScheduleQuery(
//                            airedStatus = Optional.present(true),
//                            pageNum = Optional.present(1)
//                        )
//                    )
//                    .execute()
//                    .data
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//    Column {
//        Button(onClick = showSchedule) {
//            Text("show schedule")
//        }
//        Text(text = response.toString())
//    }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AniJikanTheme {
        AniJikanApp()
    }
}