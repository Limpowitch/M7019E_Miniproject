package com.example.spotifysonglistapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import com.example.spotifysonglistapp.models.Song
import com.example.spotifysonglistapp.viewmodel.SongViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.spotifysonglistapp.SongAppScreen
import androidx.compose.runtime.getValue

@Composable
fun SongList(navController: NavHostController, songViewModel: SongViewModel) {

    val songs by songViewModel.songList.collectAsState(initial = emptyList())

    LazyColumn {
        items(songs) { song ->
            SongItemCard(
                navController,
                song = song,
                modifier = Modifier,
                songViewModel = songViewModel
            )
        }
    }
}

@Composable
fun SongItemCard(
    navController: NavHostController,
    song: Song?,
    modifier: Modifier,
    songViewModel: SongViewModel
) {
    song?.let { song ->
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .clickable {
                    songViewModel.selectSong(song)
                    navController.navigate(SongAppScreen.SongInformation.name)
                }
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = song?.title ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

}