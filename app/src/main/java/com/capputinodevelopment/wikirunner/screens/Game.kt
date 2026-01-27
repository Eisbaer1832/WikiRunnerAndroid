package com.capputinodevelopment.wikirunner.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.capputinodevelopment.wikirunner.WebView
import com.capputinodevelopment.wikirunner.api.Pages
import com.capputinodevelopment.wikirunner.api.Scoreboard
import com.capputinodevelopment.wikirunner.api.WebSocket
import com.capputinodevelopment.wikirunner.api.fetchPageTitle
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit


enum class Tabs(
    val route: String,
) {
    WIKI("Wikipedia"),
    RESULT("Ergebnis"),
}

@Composable
fun GameNavHost(
    navController: NavHostController,
    startDestination: Tabs,
    modifier: Modifier = Modifier,
    pages: Pages,
    socket: WebSocket,
    scoreboard: Scoreboard,
    room: Int,
    exitGame: () -> Unit
) {

    NavHost(
        navController,
        startDestination = startDestination.route
    ) {
        Tabs.entries.forEach { destination ->
            composable(destination.route) {
                when (destination) {
                    Tabs.WIKI -> WebView(pages.copy(startPage = pages.endPage), true){}
                    Tabs.RESULT -> SuccessScreen(pages, socket, scoreboard, room,exitGame)
                }
            }
        }
    }
}

@Composable
fun Game(modifier: Modifier, pages: MutableState<Pages>, socket: WebSocket, room: Int, exitGame: () -> Unit) {
    val goalReached = remember{ mutableStateOf(false) }
    var scoreboard by remember { mutableStateOf(Scoreboard()) }
    LaunchedEffect(Unit) {
        socket.registerScoreBoardListener {
            scoreboard= it
        }
    }
    if (!goalReached.value) {
        Column (
            modifier = modifier
        ) {
            WebView(pages.value) {
                socket.goalReached(room, "DEBUG", it)
                goalReached.value = true
                println("goal reached")
            }
        }
    }else {
        val navController = rememberNavController()
        val startDestination = Tabs.RESULT
        var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

        Column {
            PrimaryTabRow(
                selectedTabIndex = selectedDestination,
                modifier = modifier
            ) {
                Tabs.entries.forEachIndexed { index, destination ->
                    Tab(
                        selected = selectedDestination == index,
                        onClick = {
                            navController.navigate(route = destination.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                            selectedDestination = index
                        },
                        text = {
                            Text(
                                text = destination.route,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    )
                }
            }
            GameNavHost(navController, startDestination, modifier = modifier, pages.value, socket, scoreboard, room, exitGame)
        }
    }
}

@Composable
fun SuccessScreen(pages: Pages, socket: WebSocket, scoreboard: Scoreboard, room: Int, exitGame: () -> Unit) {

    val partyLeft = Party(
        speed = 0f,
        maxSpeed = 30f,
        damping = 0.9f,
        spread = 60,
        emitter = Emitter(duration = 1, TimeUnit.SECONDS).perSecond(120),
        position = Position.Relative(0.0, 0.3)
    )
    val partyRight = partyLeft.copy(
        angle = 180,
        position = Position.Relative(1.0, 0.3)
    )
    Box(modifier = Modifier.fillMaxSize(),) {
        KonfettiView(
            modifier = Modifier.fillMaxSize(),
            parties = listOf(partyLeft, partyRight),
        )
        Column() {
            Text(
                color = MaterialTheme.colorScheme.primary,
                fontSize = 50.sp,
                modifier = Modifier.fillMaxWidth().padding(top = 30.dp),
                textAlign = TextAlign.Center,
                text = "Glückwunsch!"
            )
            Text(
                modifier = Modifier.fillMaxWidth().padding(bottom = 30.dp),
                textAlign = TextAlign.Center,
                text = "Du bist bei \"${fetchPageTitle(pages.endPage, false)}\" angekommen \uD83C\uDF89"
            )
            HorizontalDivider(thickness = 3.dp, modifier = Modifier.padding(15.dp))
            println("displaying scoreboard: " + scoreboard)
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                scoreboard.users.forEachIndexed {i, user  ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                    ) {
                        Surface(
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            var displayScore = scoreboard.times[i]
                            if (displayScore != "DNF") {
                                val scoreSecondsTotal = scoreboard.times[i].toFloat()
                                val secs = scoreSecondsTotal % 60
                                val mins = (scoreSecondsTotal - secs) / 60
                                displayScore = "$mins M., $secs S."
                            }
                            Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Person, "person", modifier = Modifier.padding(start = 16.dp))
                                Text(user)
                                Spacer(modifier = Modifier.weight(1f))
                                Text(text = displayScore, modifier = Modifier.padding(end = 16.dp, top = 16.dp, bottom = 16.dp))
                            }
                        }
                        Text(text = scoreboard.linkList[i].replace("[", "").replace("]", "").replace("\"", ""), modifier = Modifier.padding(16.dp))

                    }

                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(modifier = Modifier.fillMaxWidth().padding(16.dp).height(80.dp),
                onClick = {
                    exitGame()
                }
            ) { Text("New round") }
        }
    }
}