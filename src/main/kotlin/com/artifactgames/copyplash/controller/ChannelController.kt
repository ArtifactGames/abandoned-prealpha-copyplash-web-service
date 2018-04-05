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


    val gson = Gson()
    var host: WebSocketSession? = null
    val playerList: HashMap<Player, WebSocketSession> = HashMap()

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
                ?.mapToCommandResponse()
                ?.sendResponse(session)
    }

    private fun CommandRequest.process(session: WebSocketSession?) = apply {
        getProcessor()(session)
    }

    private fun CommandRequest.getProcessor(): (session: WebSocketSession?) -> Any? =
        when(action) {
            CommandAction.SET_NICK -> {session: WebSocketSession? ->
                processSetNick(this, session)
                sendPlayerListToHost()
            }
            CommandAction.START_GAME -> { session: WebSocketSession? ->

            }
            else -> { _ -> }
        }


    val processSetNick = {command: CommandRequest, session: WebSocketSession? ->
        session?.run {
            val player = Player(id, command.payload ?: throw Exception())
            playerList.remove(player)
            playerList[player] = session
        }
    }

    private fun CommandRequest.mapToCommandResponse(): CommandResponse? =
        when(action) {
            CommandAction.SET_NICK -> CommandResponse(CommandAction.SET_NICK_SUCCESS)
            else -> {
                null
            }
        }

    private fun sendPlayerListToHost() {
        val updatePlayersResponse = CommandResponse(CommandAction.UPDATE_PLAYERS, PlayerList(playerList.getValidPlayers()).toJson())
        host!!.sendCommand(updatePlayersResponse)
    }

    private fun HashMap<Player, WebSocketSession>.getValidPlayers(): List<Player> = this
            .filterKeys { key -> key.nick.isNotEmpty() }
            .keys
            .toList()


    private fun WebSocketSession.sendCommand(command: CommandResponse) = command.serialize()?.apply { sendMessage(this) }

    private fun CommandResponse.sendResponse(session: WebSocketSession?) = apply {
        session?.sendMessage(serialize())
    }
}
