// app/src/main/java/com/example/spotifysonglistapp/ui/SongInformationScreen.kt
package com.example.spotifysonglistapp.ui

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.spotifysonglistapp.models.ArtistResponse
import com.example.spotifysonglistapp.models.Song
import com.example.spotifysonglistapp.models.Track
import com.example.spotifysonglistapp.ui.util.Responsive
import com.example.spotifysonglistapp.viewmodel.SongViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongInformationScreen(
    navController: NavHostController,
    songViewModel: SongViewModel
) {
    // Collect state from ViewModel
    val song by songViewModel.selectedSong.collectAsState()
    val artistInfo by songViewModel.artist.collectAsState()
    val topTracks by songViewModel.artistTopTracks.collectAsState()

    song?.let { currentSong ->
        // Trigger fetching of artist data when the song changes
        LaunchedEffect(currentSong.artistId) {
            songViewModel.fetchArtistData(currentSong.artistId)
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(currentSong.title) },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Responsive(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                portrait = {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AlbumArt(currentSong.albumArtUrl, size = 250.dp)
                        SongDetails(currentSong)
                        PreviewOrButton(currentSong)
                        artistInfo?.let { info ->
                            Spacer(Modifier.height(24.dp))
                            ArtistSection(info, topTracks)
                        }
                    }
                },
                landscape = {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            AlbumArt(currentSong.albumArtUrl, size = 200.dp)
                            SongDetails(currentSong)
                            PreviewOrButton(currentSong)
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            artistInfo?.let { info ->
                                ArtistSection(info, topTracks)
                            }
                        }
                    }
                }
            )
        }
    }
}

// ---------- Helper composables ----------

@Composable
private fun AlbumArt(url: String, size: Dp) {
    AsyncImage(
        model = url,
        contentDescription = "Album Art",
        placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
        error = painterResource(id = android.R.drawable.ic_menu_report_image),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(8.dp))
    )
}

@Composable
private fun SongDetails(song: Song) {
    Text(text = song.title, style = MaterialTheme.typography.headlineSmall)
    Text(text = song.artist, style = MaterialTheme.typography.titleMedium)
}

@Composable
private fun PreviewOrButton(song: Song) {
    val context = LocalContext.current
    if (song.previewUrl != null) {
        AudioPlayer(previewUrl = song.previewUrl)
    } else {
        Button(onClick = {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(song.spotifyUrl)))
        }) {
            Text("Play on Spotify")
        }
        Spacer(Modifier.height(8.dp))
        Text("Preview not available.")
    }
}

@Composable
private fun ArtistSection(
    artist: ArtistResponse,
    topTracks: List<Track>
) {
    Text("Artist: ${artist.name}", style = MaterialTheme.typography.titleMedium)
    Text("Followers: ${artist.followers.total}", style = MaterialTheme.typography.bodyMedium)
    Spacer(Modifier.height(8.dp))
    Text("Top Tracks", style = MaterialTheme.typography.titleMedium)
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(topTracks) { trackItem ->
            Column(
                modifier = Modifier
                    .width(120.dp)
                    .clickable { /* navigate if needed */ },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = trackItem.album.images.firstOrNull()?.url,
                    contentDescription = trackItem.name,
                    placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                    error = painterResource(id = android.R.drawable.ic_menu_report_image),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Text(
                    text = trackItem.name,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun AudioPlayer(previewUrl: String) {
    var isPlaying by remember { mutableStateOf(false) }
    var isPrepared by remember { mutableStateOf(false) }
    val mediaPlayer = remember(previewUrl) {
        MediaPlayer().apply {
            setDataSource(previewUrl)
            setOnPreparedListener { isPrepared = true }
            prepareAsync()
        }
    }
    DisposableEffect(previewUrl) {
        onDispose {
            if (mediaPlayer.isPlaying) mediaPlayer.stop()
            mediaPlayer.release()
        }
    }
    Button(
        onClick = {
            if (!isPrepared) return@Button
            if (mediaPlayer.isPlaying) mediaPlayer.pause() else mediaPlayer.start()
            isPlaying = mediaPlayer.isPlaying
        },
        enabled = isPrepared
    ) {
        Text(if (isPlaying) "Pause Preview" else "Play Preview")
    }
}
