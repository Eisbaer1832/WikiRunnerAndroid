package com.capputinodevelopment.wikirunner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.preference.PreferenceManager
import com.capputinodevelopment.wikirunner.api.Pages
import com.capputinodevelopment.wikirunner.api.Scoreboard
import com.capputinodevelopment.wikirunner.api.WebSocket
import com.capputinodevelopment.wikirunner.components.GiveUpDialog
import com.capputinodevelopment.wikirunner.components.TopBar
import com.capputinodevelopment.wikirunner.components.UsernameDialog
import com.capputinodevelopment.wikirunner.screens.Game
import com.capputinodevelopment.wikirunner.screens.MenuLevels
import com.capputinodevelopment.wikirunner.screens.MenuWrapper
import com.capputinodevelopment.wikirunner.screens.ScreenStates
import com.capputinodevelopment.wikirunner.screens.Settings
import com.capputinodevelopment.wikirunner.screens.SuccessScreenWrapper
import com.capputinodevelopment.wikirunner.ui.theme.WikirunnerTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val currentScreen = remember { mutableStateOf(ScreenStates.MENU) }
            val showGiveUpDialog = remember { mutableStateOf(false) }
            var currentMenuLevel = remember { mutableStateOf(MenuLevels.SELECTLOBBY) }
            var gaveUp = remember { mutableStateOf(false) }
            val pages = remember { mutableStateOf(Pages("", "")) }
            val currentRoom: MutableState<Int?> = remember { mutableStateOf(null) }
            val prefs = PreferenceManager.getDefaultSharedPreferences(LocalContext.current)
            val username = remember { mutableStateOf( prefs.getString("username", "")?:"")}
            var scoreboard by remember { mutableStateOf(Scoreboard()) }
            val serverInstance = prefs.getString("serverInstance", "https://wikirunner.tbwebtech.de/")?:"https://wikirunner.tbwebtech.de/"
            val socket = remember { WebSocket(serverInstance)}

            socket.init()


            if (username.value == "") {
                UsernameDialog { username.value = it }
            }
            if (showGiveUpDialog.value) {
                GiveUpDialog({showGiveUpDialog.value = false}) {
                    gaveUp.value = true
                }
            }

            WikirunnerTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        val showBackInMenu = currentMenuLevel.value == MenuLevels.SELECTGOAL
                        when (currentScreen.value) {
                            ScreenStates.MENU -> TopBar(
                                currentRoom = if (currentMenuLevel.value == MenuLevels.SELECTGOAL) currentRoom else null,
                                showSettings = !showBackInMenu,
                                showBack = showBackInMenu,
                                openSettings = { currentScreen.value = ScreenStates.SETTINGS }
                            ) {
                                    if  (currentMenuLevel.value == MenuLevels.SELECTGOAL) {
                                        currentMenuLevel.value = MenuLevels.SELECTLOBBY
                                    }
                            }
                            ScreenStates.GAME -> TopBar(goal = pages.value.endPage, showBack = false, showExit = true, exit = {showGiveUpDialog.value =true }) {}
                            ScreenStates.SETTINGS -> TopBar(goal = "Settings") {currentScreen.value = ScreenStates.MENU}
                            ScreenStates.SUCCESS -> TopBar(goal = pages.value.endPage) {currentScreen.value = ScreenStates.MENU; currentMenuLevel.value = MenuLevels.SELECTLOBBY}}
                    }
                ) { innerPadding ->
                    AnimatedContent(
                        targetState = currentScreen.value,
                        transitionSpec = {
                            slideInHorizontally { width -> width }.togetherWith(
                                slideOutHorizontally { width -> -width }
                            )
                        }
                    ) {screen ->
                        when (screen) {
                            ScreenStates.MENU -> MenuWrapper(
                                modifier = Modifier.padding(innerPadding),
                                socket = socket,
                                currentMenuLevel = currentMenuLevel,
                                updateMenuLevel = { currentMenuLevel.value = it },
                                changeRoom = { currentRoom.value = it },
                                startGame = {
                                    gaveUp.value = false
                                    currentScreen.value = ScreenStates.GAME
                                    pages.value = it
                                },
                                room = currentRoom.value ?: 0

                            )
                            ScreenStates.GAME -> Game(
                                modifier = Modifier.padding(innerPadding),
                                pages = pages,
                                socket = socket,
                                room = currentRoom.value ?: 0,
                                gaveUp = gaveUp.value
                            ) {
                                socket.getScoreboard(currentRoom.value?:0)
                                currentScreen.value = ScreenStates.SUCCESS
                            }
                            ScreenStates.SETTINGS -> Settings(modifier = Modifier.padding(innerPadding))
                            ScreenStates.SUCCESS -> SuccessScreenWrapper(
                                modifier = Modifier.padding(innerPadding),
                                pages = pages,
                                socket = socket,
                                room = currentRoom.value ?: 0,
                                gaveUp = gaveUp.value
                            ) {
                                currentScreen.value = ScreenStates.MENU; currentRoom.value = it
                                println("room on restart 2 " + currentRoom.value)
                            }
                        }
                    }
                }
            }
        }
    }
}
