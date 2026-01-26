package com.capputinodevelopment.wikirunner.api

import com.capputinodevelopment.wikirunner.screens.ScreenStates
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import kotlin.contracts.contract


class WebSocket {
    val url = "https://wikirunner.tbwebtech.de"
    val socket: Socket = IO.socket(url)

    fun registerVoteListener(goalChanged: (String) -> Unit) {
        socket.on("reviewItems", Emitter.Listener { args ->
            val data = args.getOrNull(0)
            println("Received reviewItems: $data")
            goalChanged(data.toString())
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
        println("joining lobby")
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
                    // nvmd this needs to change, it causes a loop :rofl:
                    socket.emit("getNextItems", room.toString())
                }
            }
        })
    }
}
