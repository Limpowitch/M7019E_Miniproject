package com.example.spotifysonglistapp.ui.util

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun Responsive(
    modifier: Modifier = Modifier,
    portrait: @Composable () -> Unit,
    landscape: @Composable () -> Unit
) {

    BoxWithConstraints(modifier) {
        if (maxWidth > maxHeight) {
            landscape()
        } else {
            portrait()
        }
    }
}