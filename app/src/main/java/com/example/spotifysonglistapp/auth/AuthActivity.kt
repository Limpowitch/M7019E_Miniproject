package com.example.spotifysonglistapp.auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.example.spotifysonglistapp.MainActivity
import com.example.spotifysonglistapp.util.SpotifySecrets
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val data: Uri? = intent?.data
        if (data != null && data.toString().startsWith(SpotifySecrets.REDIRECT_URI)) {
            val code = data.getQueryParameter("code")

            if (code != null) {
                Log.d("AuthActivity", "Authorization code: $code")

                val tokenManager = TokenManager(this)
                val verifier = tokenManager.getCodeVerifier()

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val service = SpotifyAuthService.create()
                        val response = service.exchangeCodeForToken(
                            clientId = SpotifySecrets.CLIENT_ID,
                            code = code,
                            redirectUri = SpotifySecrets.REDIRECT_URI,
                            codeVerifier = verifier ?: ""
                        )

                        if (response.isSuccessful) {
                            val tokenResponse = response.body()
                            val token = tokenResponse?.access_token

                            if (token != null) {
                                Log.d("AuthActivity", "Access token received: $token")
                                tokenManager.saveToken(token)
                                tokenManager.clearCodeVerifier()

                                Log.d("AuthActivity", "Token saved — restarting MainActivity")

                                val mainIntent = Intent(this@AuthActivity, MainActivity::class.java).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                                startActivity(mainIntent)
                            } else {
                                Log.e("AuthActivity", "Token response body was null or malformed")
                            }
                        } else {
                            val error = response.errorBody()?.string()
                            Log.e("AuthActivity", "Token exchange failed: $error")
                        }
                    } catch (e: Exception) {
                        Log.e("AuthActivity", "Exception during token exchange", e)
                    }

                    finish()
                }
            } else {
                Log.e("AuthActivity", "No authorization code found")
                finish()
            }
        } else {
            Log.e("AuthActivity", "Unexpected redirect URI: $data")
            finish()
        }
    }
}
