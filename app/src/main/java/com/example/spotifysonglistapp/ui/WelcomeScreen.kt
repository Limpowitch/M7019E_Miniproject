package com.example.spotifysonglistapp.ui

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.spotifysonglistapp.SongAppScreen
import com.example.spotifysonglistapp.auth.AuthActivity
import android.app.Activity
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.example.spotifysonglistapp.auth.PKCEUtil
import com.example.spotifysonglistapp.auth.TokenManager
import com.example.spotifysonglistapp.util.SpotifySecrets

@Composable
fun Welcome(navController: NavHostController) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    // ✅ Generate these once and remember them across recompositions
    val codeVerifier = remember { PKCEUtil.generateCodeVerifier() }
    val codeChallenge = remember { PKCEUtil.generateCodeChallenge(codeVerifier) }

    LaunchedEffect(codeVerifier) {
        tokenManager.saveCodeVerifier(codeVerifier)
    }

    val token = tokenManager.getToken()

    LaunchedEffect(token) {
        if (token != null) {
            Log.d("Welcome", "Detected saved token. Navigating to SongList.")
            navController.navigate(SongAppScreen.SongList.name) {
                popUpTo(SongAppScreen.Welcome.name) { inclusive = true }
            }
        }
    }


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val token = result.data?.getStringExtra("access_token")
            if (token != null) {
                tokenManager.saveToken(token)
                Log.d("Welcome", "Token saved successfully, navigating to SongList")
                navController.navigate(SongAppScreen.SongList.name) {
                    popUpTo(SongAppScreen.Welcome.name) { inclusive = true }
                }
            } else {
                Log.e("Welcome", "Access token was null after AuthActivity")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to Spotify Stats!", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))
        if (token != null) {
            Button(onClick = {
                val authUri = Uri.Builder()
                    .scheme("https")
                    .authority("accounts.spotify.com")
                    .appendPath("authorize")
                    .appendQueryParameter("client_id", SpotifySecrets.CLIENT_ID)
                    .appendQueryParameter("response_type", "code")
                    .appendQueryParameter("redirect_uri", SpotifySecrets.REDIRECT_URI)
                    .appendQueryParameter("code_challenge_method", "S256")
                    .appendQueryParameter("code_challenge", codeChallenge)
                    .appendQueryParameter("scope", "user-read-recently-played user-top-read")
                    .build()

                val intent = Intent(Intent.ACTION_VIEW, authUri)
                context.startActivity(intent)
            }) {
                Text("Login with Spotify")
            }
        }
    }
}

