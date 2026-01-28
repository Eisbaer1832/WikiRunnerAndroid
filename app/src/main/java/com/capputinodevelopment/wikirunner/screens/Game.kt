package com.capputinodevelopment.wikirunner.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.preference.PreferenceManager
import com.capputinodevelopment.wikirunner.api.Pages
import com.capputinodevelopment.wikirunner.api.WebSocket

@Composable
fun Game(modifier: Modifier, pages: MutableState<Pages>, socket: WebSocket, room: Int, gaveUp: Boolean, goalReached: () -> Unit) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(LocalContext.current)
    val username =  prefs.getString("username", "")?:""
    Column (
        modifier = modifier
    ) {
        WebView(pages.value, gaveUp = gaveUp) {
            var uname = username
            if (username == "DEBUG") {
                uname = "I'm a cheater"
            }
            socket.goalReached(room, uname, it, !gaveUp)
            goalReached()
        }
    }

}
