package com.example.spotifysonglistapp

import android.app.Application
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spotifysonglistapp.ui.SongInformation
import com.example.spotifysonglistapp.ui.SongList
import com.example.spotifysonglistapp.ui.Welcome
import com.example.spotifysonglistapp.viewmodel.SongViewModel
import com.example.spotifysonglistapp.auth.TokenManager
import com.example.spotifysonglistapp.models.RecentlyPlayedSong
import com.example.spotifysonglistapp.network.SpotifyApiService
import com.example.spotifysonglistapp.repository.SpotifyRepository
import com.example.spotifysonglistapp.ui.RecentlyPlayedScreen
import com.example.spotifysonglistapp.viewmodel.SongViewModelFactory


enum class SongAppScreen(@StringRes val title: Int) {
    Welcome(title = R.string.app_name),
    SongList(title = R.string.song_list),
    SongInformation(title = R.string.song_information), // Placeholder, should display actual song name
    RecentlyPlayedSongs(title = R.string.recently_played)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongAppBar(
    currentScreen: SongAppScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit = {},
    modifier: Modifier = Modifier,
    customTitle: String? = null
) {
    TopAppBar(
        title = { Text(text = customTitle ?: stringResource(id = currentScreen.title)) }, // We currently dont have custom titles
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(id = R.string.back_button),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    )

}

@Composable
fun SongAppNavigator(
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val route = backStackEntry?.destination?.route ?: SongAppScreen.Welcome.name
    val currentScreenName = route.substringBefore("/")
    val currentScreen = SongAppScreen.valueOf(currentScreenName)

    val context = LocalContext.current
    val application = context.applicationContext as Application
    val token = TokenManager(context).getToken()
    val tokenManager = TokenManager(context)
    val apiService = SpotifyApiService.create { tokenManager.getToken() ?: "" }
    val repository = SpotifyRepository(context, SpotifyApiService.create { tokenManager.getToken() ?: "" }, tokenManager::getToken)





    // ✅ Determine start destination based on token

    LaunchedEffect(token) {
        if (token != null) {
            Log.d("SongAppNavigator", "Found saved access token on launch")
        }
    }
    val startDestination = if (token != null) {
        Log.d("Navigator", "Token found. Starting at SongListScreen")
        SongAppScreen.SongList.name
    } else {
        Log.d("Navigator", "No token found. Starting at WelcomeScreen")
        SongAppScreen.Welcome.name
    }


    val songViewModel: SongViewModel = viewModel(
        factory = SongViewModelFactory(context)
    )

    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = SongAppScreen.Welcome.name) {
                Welcome(navController)
            }

            composable(route = SongAppScreen.SongList.name) {
                SongList(navController, songViewModel)
            }

            composable(route = SongAppScreen.SongInformation.name) {
                SongInformation(navController, songViewModel)
            }

            composable(route = SongAppScreen.RecentlyPlayedSongs.name) {
                RecentlyPlayedScreen(navController, repository)
            }
        }
    }
}

