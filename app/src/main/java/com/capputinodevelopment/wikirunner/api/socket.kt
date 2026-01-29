package com.capputinodevelopment.wikirunner.api

import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject


class WebSocket(url:String) {
    val socket: Socket = IO.socket(url)

    fun init() {
        if (!socket.connected()) {
            socket.connect()
        }
    }
    fun closeGame(room:Int) {
        socket.emit("closeGame", room.toString())
    }
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
            println("reading scoreboard")

            val data = args.getOrNull(0) as JSONObject
            println("data" + data)
            val scoreboard = Scoreboard(
                parseJSONArray(data.getJSONArray("users")),
                parseJSONArray(data.getJSONArray("times")),
                parseJSONArray(data.getJSONArray("linksClickedList"))
            )
            println(scoreboard)
            onUpdate(scoreboard)
        })
    }
    fun registerUserFinishListener(onUpdate:(user: String, clicks: Int) -> Unit) {
        socket.on("finishNotification", Emitter.Listener {args ->
            val user = args.getOrNull(0) as String
            val clicks = args.getOrNull(1) as Int
            onUpdate(user, clicks)
        })
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
            }else{
                println("cant join room, probably already in room")
                socket.emit("getNextItems", room.toString())
            }
        })
    }
    fun getNewItem(room:Int){
        socket.emit("getNextItems", room.toString())
    }

    fun getScoreboard(room: Int) {
        socket.emit("getScoreboard", room.toString(), Ack {args ->
            val response = args[0] as JSONObject
            println("scoreboard: " + response)
        })
    }

    fun voteForSubject(room: Int, votePositive: Boolean, username: String) {
        socket.emit("voteUseItem", room.toString(), votePositive, username)
    }

    fun goalReached(room: Int, username: String, linksClicked: List<String>, success: Boolean = true) {
        println(linksClicked)
        println("success" + success)
        socket.emit("UserFinished", room.toString(), username, linksClicked.toList().toString().replace("[", "").replace("]", ""), success)
    }
}
