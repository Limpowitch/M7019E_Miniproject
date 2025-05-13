package com.example.spotifysonglistapp

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


enum class SongAppScreen(@StringRes val title: Int) {
    Welcome(title = R.string.app_name),
    SongList(title = R.string.song_list),
    SongInformation(title = R.string.song_information) // Placeholder, should display actual song name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongAppBar(

) {

}

@Composable
fun SongAppNavigator(
    navController: NavHostController = rememberNavController()
) {
    Scaffold() { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = SongAppScreen.Welcome.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = SongAppScreen.Welcome.name) {
                Welcome(navController)
            }
        }
    }
}
