package com.capputinodevelopment.wikirunner.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DoorFront
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capputinodevelopment.wikirunner.api.WebSocket
import com.capputinodevelopment.wikirunner.api.fetchPageTitle
import com.capputinodevelopment.wikirunner.components.MenuButton

enum class MenuLevels  {
    SELECTLOBBY, SELECTGOAL
}
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MenuWrapper(modifier: Modifier, socket: WebSocket, changeRoom: (room: Int) -> Unit, startGame: () -> Unit) {

    val currentMenuLevel = remember { mutableStateOf(MenuLevels.SELECTLOBBY) }
    var room by remember { mutableStateOf(0) }
    when (currentMenuLevel.value) {
        MenuLevels.SELECTLOBBY -> SelectRoom(modifier,socket) { it ->
            room = it
            changeRoom(room)
            currentMenuLevel.value = MenuLevels.SELECTGOAL
        }
        MenuLevels.SELECTGOAL -> SelectGoal(modifier,socket,room)
    }

}

@Composable
fun SelectRoom(modifier: Modifier, socket: WebSocket, onJoinLobby:(room: Int)  -> Unit) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(style = MaterialTheme.typography.headlineLarge, fontSize = 80.sp, text = "Wikriunner")
        MenuButton("Create Lobby", Icons.Default.Create) {
            socket.createLobby() { room ->
            onJoinLobby(room)
        }}
        MenuButton("Join Lobby", Icons.Default.DoorFront) {}


    }
}


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun SelectGoal(modifier: Modifier, socket: WebSocket, room: Int) {
    val goalUrl = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        socket.registerVoteListener() { goalUrl.value =  fetchPageTitle( it)}
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
            modifier = Modifier.padding(20.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                fontSize = 40.sp,
                text = goalUrl.value,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(20.dp)
            )
        }



        Row(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.Bottom) {
            Button(
                colors = ButtonDefaults.buttonColors().copy(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier
                    .weight(1f)
                    .height(100.dp),
                shape = RoundedCornerShape(16.dp, 2.dp, 2.dp, 16.dp),
                onClick = { }
            ) { Icon(Icons.Default.ThumbDown, "Thumps down")}
            Spacer(Modifier.width(4.dp))
            //green Color(0xFF11c627)
            Button(
                colors = ButtonDefaults.buttonColors().copy(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .weight(1f)
                    .height(100.dp),
                shape = RoundedCornerShape(2.dp, 16.dp, 16.dp, 2.dp),
                onClick = {}
            ) { Icon(Icons.Default.ThumbUp, "Thumps up")}
        }

    }
}