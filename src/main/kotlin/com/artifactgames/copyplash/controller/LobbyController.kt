package com.artifactgames.copyplash.controller

import com.artifactgames.copyplash.WebSocketConfig
import com.artifactgames.copyplash.model.Question
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import org.eclipse.jgit.api.Git
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.io.File
import java.io.FileReader
import java.util.*
import javax.annotation.PostConstruct

@RestController
class LobbyController {

    @Autowired
    lateinit var websocketManager: WebSocketConfig

    @Autowired
    lateinit var messageSource: MessageSource

    val questionsDir = "copyplash-archive"
    val repoUrl = "https://github.com/ArtifactGames"
    val locale = "en_US"
    var questionList: List<Question> = Collections.emptyList()

    @PostConstruct
    fun init() {
        fetchQuestionRepository()
        questionList = fetchQuestionsList()
    }

    fun fetchQuestionRepository() {
        try {
            Git.open(File("$questionsDir/.git"))
                    .pull()
                    .call()
        } catch (e: Exception) {
            Git.cloneRepository()
                    .setURI("$repoUrl/$questionsDir.git")
                    .call()
        }
    }

    fun fetchQuestionsList(): List<Question> {
        val reader = JsonReader(FileReader("$questionsDir/$locale/questions.json"))
        val questions: Map<String, String> = Gson().fromJson(reader, Map::class.java)
        return questions.map {
            Question(it.key, it.value)
        }
    }


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