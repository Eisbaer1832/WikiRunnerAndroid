package com.capputinodevelopment.wikirunner.components
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.capputinodevelopment.wikirunner.R


@Composable
fun UsernameDialog(
    onDismissRequest: (username:String) -> Unit,
) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(LocalContext.current)
    var username by remember { mutableStateOf("") }

    AlertDialog(
        icon = {
            Icon(Icons.Filled.Person, contentDescription = stringResource(R.string.username))
        },
        title = {
            Text(text =stringResource(R.string.set_username))
        },
        text = {
            TextField(
                value = username,
                onValueChange = {
                    username = it
                },
                label = { Text(stringResource(R.string.username)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = stringResource(R.string.username)
                    )
                },
                singleLine = true,
            )},
        onDismissRequest = {
            onDismissRequest(username)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismissRequest(username)
                    prefs.edit {
                        putString("username", username)
                    }
                }
            ) {

                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest(username)
                }
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}