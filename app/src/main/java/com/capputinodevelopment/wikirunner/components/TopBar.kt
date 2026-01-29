package com.capputinodevelopment.wikirunner.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.capputinodevelopment.wikirunner.R
import com.capputinodevelopment.wikirunner.api.WebSocket
import com.capputinodevelopment.wikirunner.api.fetchPageTitle
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(currentRoom: MutableState<Int?>? = null, goal: String? = null, showSettings: Boolean= false, showBack: Boolean = true, showExit:Boolean = false, socket: WebSocket, exit:() -> Unit = {}, openSettings:() -> Unit = {}, goBack:() -> Unit) {
    var title by remember{ mutableStateOf("") }
    var usersFinished by remember { mutableStateOf(listOf<String>()) }
    var state by remember { mutableStateOf(title)}
    val notificationPart1 = stringResource(R.string.user_finished_1)
    val notificationPart2 = stringResource(R.string.user_finished_2)

    if (currentRoom != null) {
        if (currentRoom.value != null) {
            println("currentRoom" + currentRoom.value)
            title = "Raumcode " + currentRoom.value?.toString()
        }
    }else if (goal != null) {
        title = fetchPageTitle(goal)
    }
    state = title

    LaunchedEffect(Unit) {
        socket.registerUserFinishListener() {user, clicks ->
            usersFinished = usersFinished.plus(user + notificationPart1  + clicks + notificationPart2)
        }
    }
    LaunchedEffect(usersFinished) {
        if (usersFinished.isNotEmpty()) {
            state = usersFinished[0]
            delay(4000)
            usersFinished = usersFinished.drop(0)
            state = title
        }
    }
    CenterAlignedTopAppBar(
        navigationIcon = {
            if(showBack) IconButton({goBack()}) {Icon(Icons.AutoMirrored.Filled.ArrowBack, "back") }},
        title = {
            AnimatedContent(
                state,
                transitionSpec = {
                    fadeIn(
                        animationSpec = tween(3000)
                    ) togetherWith fadeOut(animationSpec = tween(3000))
                },
                label = "Animated Content"
            ) { state ->
                Text(state)
            }
        },
        actions = {
            if (showSettings) {
                IconButton({openSettings()}) { Icon(Icons.Default.Settings, "Settings")}
            }
            if (showExit) {
                IconButton({exit()}) { Icon(Icons.AutoMirrored.Filled.ExitToApp, "Exit")}
            }

        }
    )
}