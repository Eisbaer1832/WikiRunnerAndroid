package com.capputinodevelopment.wikirunner.components

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(currentRoom: MutableState<Int?>) {
    var title = ""
    if (currentRoom.value != null) {
        title = "Raumcode " + currentRoom.value?.toString()
    }
    CenterAlignedTopAppBar(
        title = { Text(title) }
    )
}