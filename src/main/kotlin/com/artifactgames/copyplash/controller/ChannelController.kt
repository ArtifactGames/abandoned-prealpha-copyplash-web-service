package com.artifactgames.copyplash.controller

import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.*

@Component
class ChannelController: TextWebSocketHandler() {

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
        message?.let {
            println("OnMessage: ${it.payload}")
        }

        session!!.sendMessage(message)
    }
}