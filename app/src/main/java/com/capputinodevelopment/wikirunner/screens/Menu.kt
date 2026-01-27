package com.capputinodevelopment.wikirunner.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DoorFront
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capputinodevelopment.wikirunner.api.Pages
import com.capputinodevelopment.wikirunner.api.Votes
import com.capputinodevelopment.wikirunner.api.WebSocket
import com.capputinodevelopment.wikirunner.api.fetchPageTitle
import com.capputinodevelopment.wikirunner.components.JoinDialog
import com.capputinodevelopment.wikirunner.components.MenuButton
import com.capputinodevelopment.wikirunner.ui.theme.libertinoFontFamily

enum class MenuLevels  {
    SELECTLOBBY, SELECTGOAL
}
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MenuWrapper(
    modifier: Modifier,
    currentMenuLevel: MutableState<MenuLevels>,
    updateMenuLevel: (MenuLevels) -> Unit,
    socket: WebSocket,
    changeRoom: (room: Int) -> Unit,
    startGame: (pages: Pages) -> Unit,
) {
    var room by remember { mutableIntStateOf(0) }
    when (currentMenuLevel.value) {
        MenuLevels.SELECTLOBBY -> SelectRoom(modifier,socket) {
            room = it
            changeRoom(room)
            updateMenuLevel(MenuLevels.SELECTGOAL)
        }
        MenuLevels.SELECTGOAL -> SelectGoal(modifier,socket,room) { startGame(it)}
    }

}

@Composable
fun SelectRoom(modifier: Modifier, socket: WebSocket, onJoinLobby:(room: Int)  -> Unit) {
    val joinRoomDialog = remember { mutableStateOf(false) }
    if (joinRoomDialog.value) {
        JoinDialog({joinRoomDialog.value = false}, { it ->
            joinRoomDialog.value = false
            socket.joinLobby(it){room: String ->
                println("joining $room")
                onJoinLobby( it)
            }
        })
    }
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(fontFamily = libertinoFontFamily, fontSize = 80.sp, text = "Wikiriunner")
        MenuButton("Create Lobby", Icons.Default.Create) {
            socket.createLobby { room ->
            onJoinLobby(room)
        }}
        MenuButton("Join Lobby", Icons.Default.DoorFront) {
            joinRoomDialog.value = true
        }


    }
}


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun SelectGoal(modifier: Modifier, socket: WebSocket, room: Int, startGame: (pages: Pages) -> Unit) {
    val goalUrl = remember { mutableStateOf("???") }
    var alreadyVoted by remember { mutableStateOf(true) }
    var votes by remember { mutableStateOf(Votes(0,0,0)) }
    LaunchedEffect(Unit) {
        socket.registerVoteListener(
            goalChanged = {
                goalUrl.value =  fetchPageTitle( it)
                alreadyVoted = false
            },
            votesChanged = {votes = it},
            startGame = {startGame(it)}
        )
        socket.joinLobby(room) {goalUrl.value = it}
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            fontSize = 40.sp,
            color = MaterialTheme.colorScheme.secondary,
            text = "Ziel:"
        )
        Surface(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(20.dp).wrapContentHeight(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                fontSize = 40.sp,
                text = goalUrl.value,
                lineHeight = 50.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(20.dp)
            )
        }

        Text("Hier könnte ihre Nutzerliste stehen")

        Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
            Row{
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp),
                    modifier = Modifier.fillMaxWidth().weight(1f).height(50.dp),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            fontSize = 25.sp,
                            textAlign = TextAlign.Center,
                            text = "${votes.negative}"
                        )
                    }
                }
                Spacer(Modifier.width(4.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(16.dp, 16.dp, 0.dp, 2.dp),
                    modifier = Modifier.fillMaxWidth().weight(1f).height(50.dp),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            fontSize = 25.sp,
                            textAlign = TextAlign.Center,
                            text = "${votes.positive}"
                        )
                    }
                }
            }
            Row {
                Button(
                    enabled = !alreadyVoted,
                    colors = ButtonDefaults.buttonColors().copy(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp),
                    shape = RoundedCornerShape(0.dp, 0.dp, 2.dp, 16.dp),
                    onClick = {
                        socket.voteForSubject(room, false, "DEBUGNAME")
                        alreadyVoted = true
                    }
                ) { Icon(Icons.Default.ThumbDown, "Thumps down", modifier = Modifier.size(35.dp))}
                Spacer(Modifier.width(4.dp))
                Button(
                    enabled = !alreadyVoted,
                    colors = ButtonDefaults.buttonColors().copy(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp),
                    shape = RoundedCornerShape(0.dp, 0.dp, 16.dp, 2.dp),
                    onClick = {
                        socket.voteForSubject(room, true, "DEBUGNAME")
                        alreadyVoted = true
                    }
                ) { Icon(Icons.Default.ThumbUp, "Thumps up", modifier = Modifier.size(35.dp))}
            }
        }
    }
}