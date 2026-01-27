package com.capputinodevelopment.wikirunner.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.capputinodevelopment.wikirunner.api.fetchPageTitle

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(currentRoom: MutableState<Int?>? = null, goal: String? = null, showSettings: Boolean= false, showBack: Boolean = true, openSettings:() -> Unit = {}, goBack:() -> Unit) {
    var title = ""
    if (currentRoom != null) {
        if (currentRoom.value != null) {
            title = "Raumcode " + currentRoom.value?.toString()
        }
    }else if (goal != null) {
        title = fetchPageTitle(goal)
    }
    CenterAlignedTopAppBar(
        navigationIcon = {
            if(showBack) IconButton({goBack()}) {Icon(Icons.AutoMirrored.Filled.ArrowBack, "back") }},
        title = { Text(title) },
        actions = {if (showSettings) {
                IconButton({openSettings()}) { Icon(Icons.Default.Settings, "Settings")}
            }
        }
    )

}