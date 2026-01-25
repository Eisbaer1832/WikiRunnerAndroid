package com.capputinodevelopment.wikirunner.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.capputinodevelopment.wikirunner.WebView

@Composable
fun Game(modifier: Modifier) {
    Column (
        modifier = modifier
    ) {
        WebView("https://wikirunner.tbwebtech.de/proxy?url=https://de.wikipedia.org/wiki/Messung")
    }

}