package com.example.spotifysonglistapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.spotifysonglistapp.repository.SpotifyRepository
import com.example.spotifysonglistapp.viewmodel.RecentlyPlayedViewModel
import com.example.spotifysonglistapp.viewmodel.RecentlyPlayedViewModelFactory
import com.example.spotifysonglistapp.models.Song
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalContext
import com.example.spotifysonglistapp.SongAppScreen
import com.example.spotifysonglistapp.viewmodel.SongViewModel
import com.example.spotifysonglistapp.viewmodel.SongViewModelFactory
import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Divider
import com.example.spotifysonglistapp.models.RecentlyPlayedSong


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentlyPlayedScreen(
    navController: NavHostController,
    repository: SpotifyRepository,
    songViewModel: SongViewModel
) {
    val viewModel: RecentlyPlayedViewModel = viewModel(
        factory = RecentlyPlayedViewModelFactory(repository)
    )
    val songs by viewModel.songs.collectAsState()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    LaunchedEffect(Unit) {
        viewModel.fetchRecentlyPlayed()
    }

    if (isLandscape) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Sidebar
            Column(
                modifier = Modifier
                    .width(200.dp)
                    .padding(top = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .clickable { navController.navigate(SongAppScreen.SongList.name) } // ✅ Make clickable
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "Top Tracks",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(26.dp)
                        )
                        Text(
                            text = "Top Tracks",
                            modifier = Modifier.padding(start = 8.dp),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, start = 24.dp, end = 24.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                )

            }




            // Song list
            LazyColumn(modifier = Modifier.weight(1f).padding(8.dp)) {
                items(songs) { song ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                songViewModel.selectSong(song.toSong())
                                navController.navigate(SongAppScreen.SongInformation.name)
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            AsyncImage(
                                model = song.albumArtUrl,
                                contentDescription = "Album Art",
                                modifier = Modifier.size(64.dp),
                                placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                                error = painterResource(id = android.R.drawable.ic_menu_report_image),
                                contentScale = ContentScale.Crop
                            )
                            Column(modifier = Modifier.padding(start = 16.dp)) {
                                Text(song.title, fontWeight = FontWeight.Bold)
                                Text(song.artists)
                                Text(
                                    "Played at: ${song.playedAt.take(19).replace("T", " ")}",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }
        }
    } else {
        // Portrait fallback — your original layout
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Recently Played") },
                    actions = {
                        IconButton(onClick = {
                            navController.navigate(SongAppScreen.SongList.name)
                        }) {
                            Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Go back")
                        }
                    }
                )
            }
        ) { innerPadding ->
            LazyColumn(modifier = Modifier.padding(innerPadding)) {
                items(songs) { song ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                songViewModel.selectSong(song.toSong())
                                navController.navigate(SongAppScreen.SongInformation.name)
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            AsyncImage(
                                model = song.albumArtUrl,
                                contentDescription = "Album Art",
                                modifier = Modifier.size(64.dp),
                                placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                                error = painterResource(id = android.R.drawable.ic_menu_report_image),
                                contentScale = ContentScale.Crop
                            )
                            Column(modifier = Modifier.padding(start = 16.dp)) {
                                Text(song.title, fontWeight = FontWeight.Bold)
                                Text(song.artists)
                                Text(
                                    "Played at: ${song.playedAt.take(19).replace("T", " ")}",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


fun RecentlyPlayedSong.toSong(): Song = Song(
    id = id,
    title = title,
    artist = artists,
    albumArtUrl = albumArtUrl,
    previewUrl = null,
    spotifyUrl = spotifyUrl,
    artistId = ""
)
