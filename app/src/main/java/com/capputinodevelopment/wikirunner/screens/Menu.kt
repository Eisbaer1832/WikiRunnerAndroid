package com.capputinodevelopment.wikirunner.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.capputinodevelopment.wikirunner.api.Pages
import com.capputinodevelopment.wikirunner.api.WebSocket
import com.capputinodevelopment.wikirunner.screens.menus.SelectGoal
import com.capputinodevelopment.wikirunner.screens.menus.SelectRoom

enum class MenuLevels  {
    SELECTLOBBY, SELECTGOAL
}
@Composable
fun MenuWrapper(
    modifier: Modifier,
    currentMenuLevel: MutableState<MenuLevels>,
    updateMenuLevel: (MenuLevels) -> Unit,
    socket: WebSocket,
    changeRoom: (room: Int) -> Unit,
    startGame: (pages: Pages) -> Unit,
    room: Int = 0
) {
    var room by remember { mutableIntStateOf(room) }
    AnimatedContent(
        targetState = currentMenuLevel.value,
        transitionSpec = {
            val direction = if (targetState > initialState) 1 else -1
            slideInHorizontally { width -> width * direction }.togetherWith(
                slideOutHorizontally { width -> -width * direction }
            )
        }
    ) { menu ->
        when (menu) {
            MenuLevels.SELECTLOBBY -> SelectRoom(modifier, socket) {
                room = it
                changeRoom(room)
                updateMenuLevel(MenuLevels.SELECTGOAL)
            }

            MenuLevels.SELECTGOAL -> SelectGoal(modifier, socket, room) { startGame(it) }
        }
    }
}

