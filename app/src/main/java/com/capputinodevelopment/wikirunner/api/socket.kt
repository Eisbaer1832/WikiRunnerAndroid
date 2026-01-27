package com.capputinodevelopment.wikirunner.api

import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject


class WebSocket {
    val url = "https://wikirunner.tbwebtech.de"
    val socket: Socket = IO.socket(url)

    fun registerVoteListener(goalChanged: (String) -> Unit, votesChanged: (votes: Votes) -> Unit, startGame: (pages: Pages) -> Unit) {
        socket.on("reviewItems", Emitter.Listener { args ->
            val data = args.getOrNull(0)
            println("Received reviewItems: $data")
            goalChanged(data.toString())
        })

        socket.on("updateVotingStats", Emitter.Listener { args ->
            val data = args.getOrNull(0) as JSONObject
            println("Received reviewItems: $data")
            val votes = Votes(
                data.getInt("positive"),
                data.getInt("negative"),
                data.getInt("needed")
            )
            votesChanged(votes)
        })

        socket.on("starting", Emitter.Listener {args ->
            val data = args.getOrNull(0) as JSONObject
            println("starting with $data")
            val startURL = data.getString("startURL")
            val endURL = data.getString("endURL")
            startGame(Pages(startURL, endURL))
        })
    }

    fun registerScoreBoardListener(onUpdate:(scoreboard: Scoreboard) -> Unit) {
        socket.on("updateScoreBoard", Emitter.Listener {args ->
            val data = args.getOrNull(0) as JSONObject
            println(data)
            val scoreboard = Scoreboard(
                parseJSONArray(data.getJSONArray("users")),
                parseJSONArray(data.getJSONArray("times")),
                parseJSONArray(data.getJSONArray("linksClickedList"))
            )
            onUpdate(scoreboard)
        })
    }


    fun init() {
        socket.connect()
    }
    fun createLobby(onSuccess: (room: Int) -> Unit ) {
        println("creating lobby")
        socket.emit("createLobby", Ack { args ->
            val response = args[0] as JSONObject
            val room = response.getString("room")
            println("room $room")
            onSuccess(room.toInt())
        })
    }
    fun joinLobby(room: Int, onSuccess: (goalURL: String) -> Unit) {
        println("joining lobby $room")
        socket.emit("joinLobby", room.toString(), Ack { args ->
            val response = args[0] as JSONObject
            val status = response.getBoolean("status")
            println(response)
            if (status) {
                val state = response.getString("ScreenState")
                if (state == "running") { // game is running, therefore data is being loaded
                    val goalURL = response.getString("endURL")
                    onSuccess(goalURL)
                }else {
                    //no game running, therefore instantly starting one
                    onSuccess("???")
                    socket.emit("getNextItems", room.toString())
                }
            }
        })
    }

    fun voteForSubject(room: Int, votePositive: Boolean, username: String) {
        socket.emit("voteUseItem", room.toString(), votePositive, username)
    }

    fun goalReached(room: Int, username: String, linksClicked: List<String>, success: Boolean = true) {
        println(linksClicked)
        socket.emit("UserFinished", room.toString(), username, linksClicked.toList(), success)
    }
}
