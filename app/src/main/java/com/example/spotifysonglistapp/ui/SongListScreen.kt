// src/main/java/com/example/spotifysonglistapp/ui/SongListScreen.kt
package com.example.spotifysonglistapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.spotifysonglistapp.SongAppScreen
import com.example.spotifysonglistapp.ui.util.Responsive
import com.example.spotifysonglistapp.viewmodel.SongViewModel
import com.example.spotifysonglistapp.viewmodel.TimeRange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongListScreen(
    navController: NavHostController,
    songViewModel: SongViewModel
) {
    // 1) Läs av state
    val songs by songViewModel.songList.collectAsState(initial = emptyList())
    val currentRange by songViewModel.timeRange.collectAsState()
    val shouldRedirect by songViewModel.shouldRedirectToLogin.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    // 2) Ladda data första gången
    LaunchedEffect(Unit) {
        songViewModel.fetchTopTracks()
    }

    // 3) Redirect vid ogiltig token
    LaunchedEffect(shouldRedirect) {
        if (shouldRedirect) {
            navController.navigate(SongAppScreen.Welcome.name) {
                popUpTo(SongAppScreen.SongList.name) { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Top Tracks (${currentRange.label})") },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(SongAppScreen.RecentlyPlayedSongs.name)
                    }) {
                        Icon(Icons.Default.AccessTime, contentDescription = "Recently played")
                    }
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter by time range")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        TimeRange.values().forEach { range ->
                            DropdownMenuItem(
                                text = { Text(range.label) },
                                onClick = {
                                    songViewModel.setTimeRange(range)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        // 4) Responsive body
        Responsive(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            portrait = {
                // Den ”vanliga” listvyn i porträttläge
                LazyColumn(
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(songs) { song ->
                        SongItemCard(
                            navController = navController,
                            song = song,
                            modifier = Modifier.fillMaxWidth(),
                            songViewModel = songViewModel
                        )
                    }
                }
            },
            landscape = {
                // Dela upp i två kolumner i landskap
                Row(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(0.4f)
                            .fillMaxHeight()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(songs) { song ->
                            SongItemCard(
                                navController = navController,
                                song = song,
                                modifier = Modifier.fillMaxWidth(),
                                songViewModel = songViewModel
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(0.6f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Select a track to see details")
                    }
                }
            }
        )
    }
}

@Composable
fun SongItemCard(
    navController: NavHostController,
    song: com.example.spotifysonglistapp.models.Song,
    modifier: Modifier = Modifier,
    songViewModel: SongViewModel
) {
    Card(
        modifier = modifier
            .clickable {
                songViewModel.selectSong(song)
                navController.navigate(SongAppScreen.SongInformation.name)
            }
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Bild
            AsyncImage(
                model = song.albumArtUrl,
                contentDescription = "Album art for ${song.title}",
                placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                error = painterResource(id = android.R.drawable.ic_menu_report_image),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(80.dp)
                    .height(80.dp)
            )

            // Titel
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
