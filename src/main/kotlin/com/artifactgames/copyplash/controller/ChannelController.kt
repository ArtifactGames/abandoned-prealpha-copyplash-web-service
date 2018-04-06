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
import java.util.*
import javax.annotation.PostConstruct
import kotlin.concurrent.timer

@Component
class ChannelController: TextWebSocketHandler() {


    @Autowired
    lateinit var messageSource: MessageSource

    @Autowired
    lateinit var gameModes: List<GameMode>

    var host: WebSocketSession? = null
    val playerList: HashMap<Player, WebSocketSession> = HashMap()
    var gameSettings: GameSettings? = null
    lateinit var currentGameMode: GameMode
    var currentRound: Round? = null
    var currentLocale: Locale? = null

    @PostConstruct
    fun init() {
        currentGameMode = gameModes.getOrNull(0) ?: throw Exception("Default game mode not found")
    }

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
                ?.sendTo(session)
    }

    private fun CommandRequest.process(session: WebSocketSession?): CommandResponse? = run {
        when(action) {
            CommandAction.SET_NICK -> processSetNick(session)
            CommandAction.START_GAME -> processStartGame()
            CommandAction.START_ROUND -> processStartRound()
            else -> { null }
        }
    }

    private fun CommandRequest.processSetNick(session: WebSocketSession?) =
        session?.run {
            val player = Player(id, payload ?: throw Exception())
            playerList.remove(player)
            playerList[player] = session

            sendPlayerListToHost()
            CommandResponse(CommandAction.SET_NICK_SUCCESS)
        }



    private fun CommandRequest.processStartGame(): CommandResponse? {
        gameSettings = deserialize<GameSettings>()?.apply {
            currentLocale = Locale(locale)
        }

        var response: CommandResponse? = null
        currentGameMode = GameMode.popRound(currentGameMode).apply {
            println(first)
            println(second)
            response = second?.run {
                currentRound = Round(position,
                        messageSource.getMessage(title, null, currentLocale ?: Locale.getDefault()),
                        messageSource.getMessage(description, null, currentLocale ?: Locale.getDefault()),
                        timeout
                )
                CommandResponse(CommandAction.SEND_ROUND_DETAILS, currentRound.toJson())
            }

        }.first

        return response
    }



    private fun CommandRequest.processStartRound(): CommandResponse? {
        CommandResponse(CommandAction.LAUNCH_ROUND, "[]").sendToPlayerList()

        currentRound?.apply {
            CommandResponse(CommandAction.UPDATE_COUNTER, timeout.toString()).sendTo(host)
            timer("round-time",true, timeout, timeout, {
                CommandResponse(CommandAction.ROUND_FINISH_HOST).sendTo(host)
                CommandResponse(CommandAction.ROUND_FINISH_PLAYER).sendToPlayerList()
                cancel()
            })
        }
        return null
    }

    private fun sendPlayerListToHost() {
        host?.apply {
            val updatePlayersResponse = CommandResponse(
                    CommandAction.UPDATE_PLAYERS,
                    PlayerList(playerList.getValidPlayers()).toJson()
            )
            updatePlayersResponse.sendTo(this)
        }
    }

    private fun HashMap<Player, WebSocketSession>.getValidPlayers(): List<Player> = this
            .filterKeys { key -> key.nick.isNotEmpty() }
            .keys
            .toList()

    private fun CommandResponse.sendTo(session: WebSocketSession?) = serialize()?.apply {
        session?.sendMessage(this)
    }

    private fun CommandResponse.sendToPlayerList() = serialize()?.apply {
        playerList.getValidPlayers().forEach {
            playerList[it]?.sendMessage(this)
        }
    }
}
