package com.capputinodevelopment.wikirunner.screens

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.capputinodevelopment.wikirunner.R
import com.capputinodevelopment.wikirunner.api.Pages
import com.capputinodevelopment.wikirunner.api.Scoreboard
import com.capputinodevelopment.wikirunner.api.WebSocket
import com.capputinodevelopment.wikirunner.api.fetchPageTitle
import kotlinx.coroutines.delay
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
    exitGame: (room: Int) -> Unit,
    gaveUp: Boolean
) {

    NavHost(
        navController,
        startDestination = startDestination.route
    ) {
        Tabs.entries.forEach { destination ->
            composable(destination.route) {
                when (destination) {
                    Tabs.WIKI -> WebView(pages.copy(startPage = pages.endPage), true){}
                    Tabs.RESULT -> SuccessScreen(pages, socket, scoreboard, room,exitGame,gaveUp)
                }
            }
        }
    }
}
@Composable
fun SuccessScreenWrapper(modifier: Modifier, pages: MutableState<Pages>, socket: WebSocket, scoreboard: Scoreboard, room: Int, gaveUp: Boolean, exitGame:(room: Int) -> Unit) {
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
        GameNavHost(navController, startDestination, modifier = modifier, pages.value, socket, scoreboard, room, exitGame, gaveUp)
    }
}
@Composable
fun SuccessScreen(
    pages: Pages,
    socket: WebSocket,
    scoreboard: Scoreboard,
    room: Int,
    exitGame: (room: Int) -> Unit,
    gaveUp: Boolean
) {

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
        Column() {
            var title = stringResource(R.string.game_success)
            var titleColor =  MaterialTheme.colorScheme.primary
            if (gaveUp) {
                title = stringResource(R.string.game_failure)
                titleColor = MaterialTheme.colorScheme.error
            }
            Column(modifier = Modifier.verticalScroll(rememberScrollState()).weight(10f)) {
                Text(
                    color = titleColor,
                    fontSize = 50.sp,
                    modifier = Modifier.fillMaxWidth().padding(top = 30.dp),
                    textAlign = TextAlign.Center,
                    text = title
                )
                Text(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 30.dp),
                    textAlign = TextAlign.Center,
                    text = "Du bist ${if(gaveUp) "nicht " else ""}bei \"${fetchPageTitle(pages.endPage, false)}\" angekommen  ${if (gaveUp) "\uD83E\uDD7A" else "\uD83C\uDF89"}"
                )
                HorizontalDivider(thickness = 3.dp, modifier = Modifier.padding(15.dp))
                scoreboard.users.forEachIndexed {i, user  ->
                    var visible by remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        delay(i * 50L) // staggered animation
                        visible = true
                    }

                    AnimatedVisibility(
                        visible = visible,
                        enter = slideInHorizontally(initialOffsetX = { it / 2 }) + fadeIn(),
                        exit = slideOutHorizontally(targetOffsetX = { it / 2 }) + fadeOut()
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
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
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(user)
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(text = displayScore, modifier = Modifier.padding(end = 16.dp, top = 16.dp, bottom = 16.dp))
                                }
                            }
                            Text(
                                text = scoreboard.linkList[i].replace("[", "").replace("]", "").replace("\"", ""),
                                modifier = Modifier.padding(start = 32.dp, end = 32.dp, bottom = 16.dp))

                        }
                    }
                }
            }
            Spacer(modifier = Modifier.padding(top=6.dp))
            Button(modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end =16.dp, bottom = 32.dp).height(80.dp),
                onClick = {
                    socket.closeGame(room)
                    exitGame(room)
                }
            ) { Text(stringResource(R.string.new_round))}
        }
        if(!gaveUp) {
            KonfettiView(
                modifier = Modifier.fillMaxSize(),
                parties = listOf(partyLeft, partyRight),
            )
        }
    }
}