package com.example.spotifysonglistapp.ui

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.navigation.NavHostController
import com.example.spotifysonglistapp.viewmodel.SongViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongInformation(navController: NavHostController, songViewModel: SongViewModel) {
    val song by songViewModel.selectedSong.collectAsState()
    val context = LocalContext.current

    song?.let {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(it.title) },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = it.albumArtUrl,
                    contentDescription = "Album Art",
                    modifier = Modifier
                        .size(250.dp)
                        .padding(16.dp),
                    placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                    error = painterResource(id = android.R.drawable.ic_menu_report_image),
                    contentScale = ContentScale.Crop
                )

                Text(text = it.title, style = MaterialTheme.typography.headlineSmall)
                Text(text = it.artist, style = MaterialTheme.typography.titleMedium)

                Spacer(modifier = Modifier.height(24.dp))

                if (it.previewUrl != null) {
                    AudioPlayer(previewUrl = it.previewUrl)
                } else {
                    song?.let { song ->
                        Button(onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(song.spotifyUrl))
                            context.startActivity(intent)
                        }) {
                            Text("Play on Spotify")
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Preview not available.")
                }
            }
        }
    }
}

@Composable
fun AudioPlayer(previewUrl: String) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var isPrepared by remember { mutableStateOf(false) }

    val mediaPlayer = remember(previewUrl) {
        MediaPlayer().apply {
            setDataSource(previewUrl)
            setOnPreparedListener {
                isPrepared = true
            }
            prepareAsync()
        }
    }

    DisposableEffect(previewUrl) {
        onDispose {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.release()
        }
    }

    Button(
        onClick = onClick@{
            if (!isPrepared) return@onClick

            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                isPlaying = false
            } else {
                mediaPlayer.start()
                isPlaying = true
            }
        },
        enabled = isPrepared
    ) {
        Text(if (isPlaying) "Pause Preview" else "Play Preview")
    }
}


