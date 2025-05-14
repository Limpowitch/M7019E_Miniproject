package com.example.spotifysonglistapp.ui

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.spotifysonglistapp.SongAppScreen
import androidx.compose.runtime.getValue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import com.example.spotifysonglistapp.auth.TokenManager
import androidx.compose.ui.platform.LocalContext



@Composable
fun SongList(navController: NavHostController, songViewModel: SongViewModel) {

    val songs by songViewModel.songList.collectAsState(initial = emptyList())



    LaunchedEffect(Unit) {
        songViewModel.fetchTopTracks()
    }

    val shouldRedirect by songViewModel.shouldRedirectToLogin.collectAsState()

    LaunchedEffect(shouldRedirect) {
        if (shouldRedirect) {
            navController.navigate(SongAppScreen.Welcome.name) {
                popUpTo(SongAppScreen.SongList.name) { inclusive = true }
            }
        }
    }

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

    // testing
//    val context = LocalContext.current
//    val tokenManager = remember { TokenManager(context) }
//
//    Button(onClick = {
//        tokenManager.saveToken("invalid_or_expired_token")
//        Log.d("WelcomeScreen", "Fake expired token set.")
//    }) {
//        Text("Simulate Expired Token")
//    }
    // testing

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
                .padding(4.dp)
                .clickable {
                    songViewModel.selectSong(song)
                    navController.navigate(SongAppScreen.SongInformation.name)
                }
        ) {
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

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

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }

}