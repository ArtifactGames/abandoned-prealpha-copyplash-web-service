package com.artifactgames.copyplash.controller

import com.artifactgames.copyplash.model.*
import com.artifactgames.copyplash.type.CommandAction
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class ChannelController: TextWebSocketHandler() {


    @Autowired
    lateinit var messageSource: MessageSource

    var host: WebSocketSession? = null
    val playerList: HashMap<Player, WebSocketSession> = HashMap()
    var gameSettings: GameSettings? = null

    override fun handleTransportError(session: WebSocketSession?, exception: Throwable?) {
        println("Error: ${exception.toString()}")
    }

    override fun afterConnectionClosed(session: WebSocketSession?, status: CloseStatus?) {
        println("ConnectionClosed: ${session.toString()}")
        session?.apply {
            playerList.remove(Player(id))
            sendPlayerListToHost()
        }
    }

    override fun afterConnectionEstablished(session: WebSocketSession?) {
        println("ConnectionStablished: ${session.toString()}")
        if (host == null) {
            host = session
        } else {
            session?.apply {
                playerList[Player(id)] = this
            }
        }
    }

    override fun handleMessage(session: WebSocketSession?, message: WebSocketMessage<*>?) {
        message
                ?.mapMessageToCommandRequest()
                ?.process(session)
                ?.send(session)
    }

    private fun CommandRequest.process(session: WebSocketSession?): CommandResponse? = run {
        when(action) {
            CommandAction.SET_NICK -> processSetNick(this, session)
            CommandAction.START_GAME -> processStartGame(this)
            else -> { null }
        }
    }

    private fun processSetNick(command: CommandRequest, session: WebSocketSession?) =
        session?.run {
            val player = Player(id, command.payload ?: throw Exception())
            playerList.remove(player)
            playerList[player] = session

            sendPlayerListToHost()
            CommandResponse(CommandAction.SET_NICK_SUCCESS)
        }


    private fun processStartGame(command: CommandRequest): CommandResponse? {
        gameSettings = command.deserialize<GameSettings>()
        return null
    }

    private fun sendPlayerListToHost() {
        host?.apply {
            val updatePlayersResponse = CommandResponse(
                    CommandAction.UPDATE_PLAYERS,
                    PlayerList(playerList.getValidPlayers()).toJson()
            )
            updatePlayersResponse.send(this)
        }
    }

    private fun HashMap<Player, WebSocketSession>.getValidPlayers(): List<Player> = this
            .filterKeys { key -> key.nick.isNotEmpty() }
            .keys
            .toList()

    private fun CommandResponse.send(session: WebSocketSession?) = serialize()?.apply {
        session?.sendMessage(this)
    }
}
