package com.capputinodevelopment.wikirunner.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.capputinodevelopment.wikirunner.R
import com.capputinodevelopment.wikirunner.components.SettingsCard

@Composable
fun Settings(modifier: Modifier) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(LocalContext.current)
    val usernameState = rememberTextFieldState(prefs.getString("username", "") ?: "")
    val serverInstance = rememberTextFieldState(prefs.getString("serverInstance", "https://wikirunner.tbwebtech.de/") ?: "https://wikirunner.tbwebtech.de/")
    Column(modifier = modifier.fillMaxSize()) {
        LaunchedEffect(usernameState.text) {
            prefs.edit {
                putString("username", usernameState.text.toString())
            }
        }
        LaunchedEffect(serverInstance.text) {
            prefs.edit {
                var server = serverInstance.text.toString()
                if (server.isEmpty()) server = "https://wikirunner.tbwebtech.de/"
                putString("serverInstance", server)
            }
        }
        SettingsCard() {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                state = usernameState,
                lineLimits = TextFieldLineLimits.SingleLine,
                label = { Text(stringResource(R.string.username)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),

            )
        }
        SettingsCard() {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                state = serverInstance,
                lineLimits = TextFieldLineLimits.SingleLine,
                label = { Text(stringResource(R.string.server_instance)) },
                )
        }
    }
}