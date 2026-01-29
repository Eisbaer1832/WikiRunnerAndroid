@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.capputinodevelopment.wikirunner.screens.menus

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.preference.PreferenceManager
import com.capputinodevelopment.wikirunner.R
import com.capputinodevelopment.wikirunner.api.Pages
import com.capputinodevelopment.wikirunner.api.Votes
import com.capputinodevelopment.wikirunner.api.WebSocket
import com.capputinodevelopment.wikirunner.api.fetchPageTitle


@Composable
fun SelectGoal(
    modifier: Modifier,
    socket: WebSocket,
    room: Int,
    goal: String,
    startGame: (pages: Pages) -> Unit
) {
    val goalUrl = remember { mutableStateOf("???") }
    var loading by remember { mutableStateOf(true) }
    var alreadyVoted by remember { mutableStateOf(true) }
    var votes by remember { mutableStateOf(Votes(0,0,0)) }
    val prefs = PreferenceManager.getDefaultSharedPreferences(LocalContext.current)
    val usernameState =  prefs.getString("username", "")?:""
    LaunchedEffect(Unit) {
        socket.registerVoteListener(
            goalChanged = {
                println("goal: " + goalUrl.value)
                goalUrl.value =  fetchPageTitle( it)
                alreadyVoted = false
            },
            votesChanged = {votes = it},
            startGame = {startGame(it)}
        )
        socket.getNewItem(room)
    }
    LaunchedEffect(goalUrl.value) {
        loading = if (goalUrl.value == "???") {
            true
        } else {
            false
        }
    }
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            fontSize = 40.sp,
            color = MaterialTheme.colorScheme.secondary,
            text = stringResource(R.string.goal_header)
        )
        if(loading) {
            LoadingIndicator()
        }
        AnimatedVisibility(
            visible = !loading,
            enter = slideInHorizontally(initialOffsetX = { it / 2 }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { it / 2 }) + fadeOut()
        ) {
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
        }

        //TODO Text("Hier könnte ihre Nutzerliste stehen")

        Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Bottom) {
            Row{
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp),
                    modifier = Modifier.width(100.dp).height(50.dp),
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
                Spacer(Modifier.weight(1f))
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp),
                    modifier = Modifier.width(100.dp).height(50.dp),
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
                        .height(120.dp),
                    shape = RoundedCornerShape(0.dp, 16.dp, 16.dp, 16.dp),
                    onClick = {
                        socket.voteForSubject(room, false, usernameState)
                        alreadyVoted = true
                    }
                ) { Icon(Icons.Default.ThumbDown, "Thumps down", modifier = Modifier.size(35.dp))}
                Spacer(Modifier.width(10.dp))
                Button(
                    enabled = !alreadyVoted,
                    colors = ButtonDefaults.buttonColors().copy(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp),
                    shape = RoundedCornerShape(16.dp, 0.dp, 16.dp, 16.dp),
                    onClick = {
                        socket.voteForSubject(room, true, usernameState)
                        alreadyVoted = true
                    }
                ) { Icon(Icons.Default.ThumbUp, "Thumps up", modifier = Modifier.size(35.dp))}
            }
        }
    }
}