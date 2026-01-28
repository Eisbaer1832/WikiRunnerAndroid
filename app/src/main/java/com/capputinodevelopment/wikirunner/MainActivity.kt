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
import com.capputinodevelopment.wikirunner.api.Pages
import com.capputinodevelopment.wikirunner.api.WebSocket
import com.capputinodevelopment.wikirunner.components.TopBar
import com.capputinodevelopment.wikirunner.screens.Game
import com.capputinodevelopment.wikirunner.screens.MenuLevels
import com.capputinodevelopment.wikirunner.screens.MenuWrapper
import com.capputinodevelopment.wikirunner.screens.ScreenStates
import com.capputinodevelopment.wikirunner.screens.Settings
import com.capputinodevelopment.wikirunner.ui.theme.WikirunnerTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val socket = WebSocket()
        socket.init()
        enableEdgeToEdge()
        setContent {
            val currentScreen = remember { mutableStateOf(ScreenStates.MENU) }
            var currentMenuLevel = remember { mutableStateOf(MenuLevels.SELECTLOBBY) }
            val pages = remember { mutableStateOf(Pages("", "")) }
            val currentRoom: MutableState<Int?> = remember { mutableStateOf(null) }


            WikirunnerTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        val showBackInMenu = currentMenuLevel.value == MenuLevels.SELECTGOAL
                        when (currentScreen.value) {
                            ScreenStates.MENU -> TopBar(currentRoom, showSettings = !showBackInMenu,
                                showBack = showBackInMenu,
                                openSettings = {currentScreen.value = ScreenStates.SETTINGS}) {
                                    if  (currentMenuLevel.value == MenuLevels.SELECTGOAL) {
                                        currentMenuLevel.value = MenuLevels.SELECTLOBBY
                                    }
                            }
                            ScreenStates.GAME -> TopBar(goal = pages.value.endPage) {}
                            ScreenStates.SETTINGS -> TopBar(goal = "Settings") {currentScreen.value = ScreenStates.MENU}
                        }
                    }
                ) { innerPadding ->
                    when (currentScreen.value) {
                        ScreenStates.MENU -> MenuWrapper(modifier = Modifier.padding(innerPadding),socket = socket,
                            currentMenuLevel = currentMenuLevel,
                            updateMenuLevel = {currentMenuLevel.value = it},
                            changeRoom = {currentRoom.value = it},
                            startGame =  {
                                currentScreen.value = ScreenStates.GAME
                                pages.value = it
                            },
                            room = currentRoom.value?:0

                        )
                        ScreenStates.GAME -> Game(
                            modifier = Modifier.padding(innerPadding), pages, socket, currentRoom.value?:0) {
                                currentScreen.value = ScreenStates.MENU; currentRoom.value = it
                                println("room on restart 2 " + currentRoom.value)
                            }
                        ScreenStates.SETTINGS -> Settings(modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}
