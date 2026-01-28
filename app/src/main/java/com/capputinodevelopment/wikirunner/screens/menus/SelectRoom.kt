package com.capputinodevelopment.wikirunner.screens.menus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DoorFront
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.capputinodevelopment.wikirunner.R
import com.capputinodevelopment.wikirunner.api.WebSocket
import com.capputinodevelopment.wikirunner.components.JoinDialog
import com.capputinodevelopment.wikirunner.components.MenuButton
import com.capputinodevelopment.wikirunner.ui.theme.libertinoFontFamily


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

        Text(fontFamily = libertinoFontFamily, fontSize = 70.sp, text = "Wikiriunner", modifier = Modifier.padding(20.dp))
        MenuButton(text = stringResource(R.string.create_lobby), Icons.Default.Create) {
            socket.createLobby { room ->
                onJoinLobby(room)
            }}
        MenuButton(stringResource(R.string.join_lobby), Icons.Default.DoorFront) {
            joinRoomDialog.value = true
        }


    }
}
