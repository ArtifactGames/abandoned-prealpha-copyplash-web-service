package com.artifactgames.copyplash.controller

import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class ChannelController: TextWebSocketHandler() {

    override fun handleTransportError(session: WebSocketSession?, exception: Throwable?) {
        println("Error: ${exception.toString()}")
    }

    override fun afterConnectionClosed(session: WebSocketSession?, status: CloseStatus?) {
        println("ConnectionClosed: ${session.toString()}")
    }

    override fun afterConnectionEstablished(session: WebSocketSession?) {
        println("ConnectionStablished: ${session.toString()}")
    }

    override fun handleMessage(session: WebSocketSession?, message: WebSocketMessage<*>?) {
        message?.let {
            println("OnMessage: ${it.payload}")
        }

        session!!.sendMessage(message)
    }
}