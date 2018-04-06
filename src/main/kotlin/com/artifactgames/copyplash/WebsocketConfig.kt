package com.artifactgames.copyplash

import com.artifactgames.copyplash.controller.ChannelController
import com.artifactgames.copyplash.model.Lobby
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import java.util.*
import kotlin.collections.HashMap


@Component
@Configuration
@EnableWebSocket
class WebSocketConfig : WebSocketConfigurer {

    val POOL_SIZE = 20

    val lobbies : HashMap<Lobby, Boolean> = HashMap()

    @Value("#{'\${allowedOrigins}'.split(',')}")
    private val allowedOrigins: List<String> = listOf()

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        for (i in 1..POOL_SIZE) {

            val id = UUID.randomUUID()
            // TODO add handshake interceptor to avoid unexpected connections or visitors to the websockets
            registry.addHandler(ChannelController(), id.toString()).setAllowedOrigins(*allowedOrigins.toTypedArray())
            lobbies[Lobby(id, 0)] = true
        }
    }

    fun getLobby(): Lobby? {
        for(lobby in lobbies.keys) {
            if (lobbies[lobby]!!) {
                lobbies[lobby] = false
                lobby.password = genPassword()
                return lobby
            }
        }
        return null
    }

    fun enterLobby(password: Int): Lobby? {
        if(password > 0) {
            return lobbies
                    .filterKeys { it.password == password }
                    .map { it.key }
                    .getOrNull(0)
        }
        return null
    }

    private fun genPassword() = Math.abs(Random().nextInt(8999) + 1000)


    fun closeChannel(id: String) {
        val lobby = Lobby(UUID.fromString(id), 0)
        if (lobbies.contains(lobby)) {
            lobbies[lobby] = false
        }
    }

}