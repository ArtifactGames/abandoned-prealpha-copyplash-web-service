package com.artifactgames.copyplash.controller

import com.artifactgames.copyplash.model.CommandRequest
import com.artifactgames.copyplash.model.CommandResponse
import com.artifactgames.copyplash.type.CommandAction
import com.google.gson.Gson
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.*

@Component
class ChannelController: TextWebSocketHandler() {


    val gson = Gson()
    var host: WebSocketSession? = null
    val playerList: LinkedList<WebSocketSession?> = LinkedList()

    override fun handleTransportError(session: WebSocketSession?, exception: Throwable?) {
        println("Error: ${exception.toString()}")
    }

    override fun afterConnectionClosed(session: WebSocketSession?, status: CloseStatus?) {
        println("ConnectionClosed: ${session.toString()}")
        playerList.remove(session)
    }

    override fun afterConnectionEstablished(session: WebSocketSession?) {
        println("ConnectionStablished: ${session.toString()}")
        if (host == null) {
            host = session
        } else {
            playerList.add(session)
        }
    }

    override fun handleMessage(session: WebSocketSession?, message: WebSocketMessage<*>?) {

        val response = message?.let { processMessage(message) }

        response?.let {
            session?.sendMessage(response)
        }
    }

    private fun processMessage(message: WebSocketMessage<*>): TextMessage? {
        return try {
            val command = gson.fromJson(message.payload.toString(), CommandRequest::class.java)
            processCommand(command)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun processCommand(command: CommandRequest): TextMessage? {
        return when(command.action) {
            CommandAction.SET_NICK -> TextMessage(gson.toJson(CommandResponse(CommandAction.SET_NICK_SUCCESS)))
            else -> {
                null
            }
        }
    }
}