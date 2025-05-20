package com.example.spotifysonglistapp.ui.util

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Väljer automatiskt mellan portrait- och landscape-layout
 * beroende på förhållandet mellan maxWidth och maxHeight.
 */
@Composable
fun Responsive(
    modifier: Modifier = Modifier,
    portrait: @Composable () -> Unit,
    landscape: @Composable () -> Unit
) {
    // Om du vill att den alltid fyller hela skärmen, kan du
    // byta ut `modifier` mot `modifier.fillMaxSize()`
    BoxWithConstraints(modifier) {
        if (maxWidth > maxHeight) {
            landscape()
        } else {
            portrait()
        }
    }
}
