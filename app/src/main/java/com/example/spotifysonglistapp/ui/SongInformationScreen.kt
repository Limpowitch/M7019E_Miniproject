package com.example.spotifysonglistapp.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.spotifysonglistapp.viewmodel.SongViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun SongInformation(navController: NavHostController, songViewModel: SongViewModel) {

    val song by songViewModel.selectedSong.collectAsState(initial = null)

    song?.let { song ->
        LazyColumn {
            item {
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