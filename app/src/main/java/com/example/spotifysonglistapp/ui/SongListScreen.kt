package com.example.spotifysonglistapp.ui

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

                // The below is just a placeholder AsyncImage
                AsyncImage(
                    model = "https://via.placeholder.com/300x180.png?text=Cover+Art",
                    contentDescription = "Album art placeholder for ${song.title}",
                    placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                    error       = painterResource(id = android.R.drawable.ic_menu_report_image),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(80.dp)
                        .height(80.dp)
                )

                // When API is fixed, uncomment and use AsyncImage below
//                AsyncImage(
//                    model = posterUrl,
//                    contentDescription = song?.title,
//                    contentScale = ContentScale.Crop,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(180.dp)
//                )

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