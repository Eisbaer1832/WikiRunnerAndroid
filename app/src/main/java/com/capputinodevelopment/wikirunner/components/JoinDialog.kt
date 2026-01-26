package com.capputinodevelopment.wikirunner.components
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType


@Composable
fun JoinDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (code: Int) -> Unit,
) {
    var room by remember { mutableStateOf("") }

    AlertDialog(
        icon = {
            Icon(Icons.Filled.MeetingRoom, contentDescription = "Example Icon")
        },
        title = {
            Text(text = "Join Room")
        },
        text = {
            TextField(
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                value = room,
                onValueChange = {
                    room = it
                },
                label = { Text("Raumcode") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Raumcode"
                    )
                },
                singleLine = true,
            )},
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation(room.toInt())
                }
            ) {
                Text("Bestätigen")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Abbrechen")
            }
        }
    )
}