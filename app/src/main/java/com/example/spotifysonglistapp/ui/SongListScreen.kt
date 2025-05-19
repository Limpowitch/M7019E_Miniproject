package com.example.spotifysonglistapp.ui

import android.util.Log
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.spotifysonglistapp.SongAppScreen
import com.example.spotifysonglistapp.auth.TokenManager
import com.example.spotifysonglistapp.models.Song
import com.example.spotifysonglistapp.viewmodel.SongViewModel
import com.example.spotifysonglistapp.viewmodel.TimeRange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongList(navController: NavHostController, songViewModel: SongViewModel) {
    val songs by songViewModel.songList.collectAsState(initial = emptyList())
    val shouldRedirect by songViewModel.shouldRedirectToLogin.collectAsState()
    val currentRange by songViewModel.timeRange.collectAsState()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        songViewModel.fetchTopTracks()
    }

    LaunchedEffect(shouldRedirect) {
        if (shouldRedirect) {
            navController.navigate(SongAppScreen.Welcome.name) {
                popUpTo(SongAppScreen.SongList.name) { inclusive = true }
            }
        }
    }

    if (isLandscape) {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .width(200.dp)
                    .padding(top = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .clickable { navController.navigate(SongAppScreen.RecentlyPlayedSongs.name) }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "Recently Played",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(26.dp)
                        )
                        Text(
                            text = "Recently Played",
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
                Spacer(modifier = Modifier.height(16.dp))

                TimeRange.values().forEach { range ->
                    Button(
                        onClick = { songViewModel.setTimeRange(range) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        enabled = currentRange != range
                    ) {
                        Text(text = range.label)
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {
                items(songs) { song ->
                    SongItemCard(
                        navController = navController,
                        song = song,
                        modifier = Modifier,
                        songViewModel = songViewModel
                    )
                }
            }
        }
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Top Tracks (${currentRange.label})") },
                    actions = {
                        IconButton(onClick = {
                            navController.navigate(SongAppScreen.RecentlyPlayedSongs.name)
                        }) {
                            Icon(Icons.Default.AccessTime, contentDescription = "Recently played songs")
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
            LazyColumn(modifier = Modifier.padding(innerPadding)) {
                items(songs) { song ->
                    SongItemCard(
                        navController = navController,
                        song = song,
                        modifier = Modifier,
                        songViewModel = songViewModel
                    )
                }
            }
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
    song?.let {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .clickable {
                    songViewModel.selectSong(it)
                    navController.navigate(SongAppScreen.SongInformation.name)
                }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(8.dp)
            ) {
                AsyncImage(
                    model = it.albumArtUrl,
                    contentDescription = "Album art for ${it.title}",
                    placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                    error = painterResource(id = android.R.drawable.ic_menu_report_image),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(80.dp)
                )

                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(
                        text = it.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = it.artist,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

