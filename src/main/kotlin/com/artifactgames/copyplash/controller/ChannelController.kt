package com.artifactgames.copyplash.controller

import com.artifactgames.copyplash.model.CommandRequest
import com.artifactgames.copyplash.model.CommandResponse
import com.artifactgames.copyplash.model.Player
import com.artifactgames.copyplash.model.PlayerList
import com.artifactgames.copyplash.type.CommandAction
import com.google.gson.Gson
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import kotlin.collections.HashMap

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
                ?.mapMessageToCommandResponse()
                ?.also {
                    process(it, session)
                }
                ?.mapToCommandResponse()
                ?.serialize()
                ?.let {
                    session?.sendMessage(it)
                }
    }

    private fun process(commandRequest: CommandRequest, session: WebSocketSession?) {
        try {
            commandRequest.getProcessor()(session)
        } catch (e:Exception) {
            e.printStackTrace();
        }
    }


    private fun WebSocketMessage<*>.mapMessageToCommandResponse(): CommandRequest? =
        try {
            gson.fromJson(payload.toString(), CommandRequest::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    private fun CommandRequest.getProcessor(): (session: WebSocketSession?) -> Any? =
        when(action) {
            CommandAction.SET_NICK -> {session: WebSocketSession? ->
                processSetNick(this, session)
                sendPlayerListToHost()
            }
            else -> { _ -> }
        }

    private fun sendPlayerListToHost() {
        val updatePlayersResponse = CommandResponse(CommandAction.UPDATE_PLAYERS, PlayerList(playerList.getValidPlayers()).toJson())
        val playerListTextMessage = TextMessage(updatePlayersResponse.toJson())
        host!!.sendMessage(playerListTextMessage)
    }

    private fun HashMap<Player, WebSocketSession>.getValidPlayers(): List<Player> = this
        .filterKeys { key -> key.nick.isNotEmpty() }
        .keys
        .toList()

    private fun <T> T.toJson(): String = gson.toJson(this)

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

    private fun CommandResponse.serialize(): TextMessage? =
        try {
            TextMessage(gson.toJson(this))
        } catch(e: Exception) {
            null
        }

}
