package com.capputinodevelopment.wikirunner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.capputinodevelopment.wikirunner.api.WebSocket
import com.capputinodevelopment.wikirunner.components.TopBar
import com.capputinodevelopment.wikirunner.screens.Game
import com.capputinodevelopment.wikirunner.screens.MenuWrapper
import com.capputinodevelopment.wikirunner.screens.ScreenStates
import com.capputinodevelopment.wikirunner.ui.theme.WikirunnerTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val socket = WebSocket()
        socket.init()
        enableEdgeToEdge()
        setContent {
            val currentScreen = remember { mutableStateOf(ScreenStates.MENU) }
            val currentRoom: MutableState<Int?> = remember { mutableStateOf(null) }
            WikirunnerTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        when (currentScreen.value) {
                            ScreenStates.MENU -> TopBar(currentRoom)
                            ScreenStates.GAME -> TopBar(currentRoom)
                        }
                    }
                ) { innerPadding ->
                    when (currentScreen.value) {
                        ScreenStates.MENU -> MenuWrapper(modifier = Modifier.padding(innerPadding),socket = socket, {currentRoom.value = it}) {currentScreen.value = ScreenStates.GAME}
                        ScreenStates.GAME -> Game(modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}
