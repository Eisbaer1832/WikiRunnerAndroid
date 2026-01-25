package com.capputinodevelopment.wikirunner

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Game(modifier: Modifier) {
    Column (
        modifier = modifier
    ) {

        WebView("https://wikirunner.tbwebtech.de/proxy?url=https://de.wikipedia.org/wiki/Messung")
    }

}