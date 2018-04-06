package com.artifactgames.copyplash.controller

import com.artifactgames.copyplash.WebSocketConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class LobbyController {

    @Autowired
    lateinit var websocketManager: WebSocketConfig

    @GetMapping("/lobby-create")
    fun create(): ResponseEntity<*> {
        val lobby = websocketManager.getLobby() ?: return errorResponse(HttpStatus.NOT_ACCEPTABLE)

        return ResponseEntity.ok(lobby)
    }


    data class LobbyEnterRequest(val password: Int)

    @PostMapping("/lobby-enter")
    fun enter(@RequestBody req: LobbyEnterRequest?): ResponseEntity<*> {
        val password = req?.password ?: 0
        val lobby = websocketManager.enterLobby(password) ?: return errorResponse(HttpStatus.BAD_REQUEST)

        return ResponseEntity.ok(lobby)
    }

    private fun errorResponse(status: HttpStatus): ResponseEntity<*> = ResponseEntity.status(status).body(null)

}