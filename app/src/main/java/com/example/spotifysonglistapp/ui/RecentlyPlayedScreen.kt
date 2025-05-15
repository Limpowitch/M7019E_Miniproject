package com.example.spotifysonglistapp.ui

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
import androidx.compose.material3.MaterialTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentlyPlayedScreen(
    navController: NavHostController,
    repository: SpotifyRepository
) {
    val viewModel: RecentlyPlayedViewModel = viewModel(
        factory = RecentlyPlayedViewModelFactory(repository)
    )
    val songs by viewModel.songs.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchRecentlyPlayed()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Recently Played") })
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(songs) { song ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
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
                            Text("Played at: ${song.playedAt.take(19).replace("T", " ")}", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}
