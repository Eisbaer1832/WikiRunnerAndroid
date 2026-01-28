package com.capputinodevelopment.wikirunner.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsCard( content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth()
            .padding(16.dp)
    ) {
        Column() {
            content()
        }
    }
}