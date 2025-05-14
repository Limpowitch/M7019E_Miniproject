package com.example.spotifysonglistapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

// 1) Dark color-scheme using Spotify’s palette:
private val SpotifyDarkColors = darkColorScheme(
    primary           = SpotifyLightGray,
    secondary         = SpotifyDarkGray,
    background        = SpotifyDarkGray,
    surface           = SpotifyLightGray,
    onPrimary         = SpotifyWhite,
    onSecondary       = SpotifyWhite,
    onBackground      = SpotifyWhite,
    onSurface         = SpotifyWhite,
    // you can also assign container colors if you like:
    primaryContainer = SpotifyGreen,
    onPrimaryContainer = SpotifyBlack
)

@Composable
fun SpotifySongListAppTheme(
    content: @Composable () -> Unit
) {


    MaterialTheme(
        colorScheme = SpotifyDarkColors,
        typography = Typography,
        content = content
    )
}