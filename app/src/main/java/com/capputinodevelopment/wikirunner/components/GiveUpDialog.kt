package com.capputinodevelopment.wikirunner.components
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.capputinodevelopment.wikirunner.R


@Composable
fun GiveUpDialog(
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit,
    ) {

    AlertDialog(
        icon = {
            Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "give up")
        },
        title = {
            Text(text = stringResource(R.string.give_up))
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                    onConfirmRequest()
                }
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}