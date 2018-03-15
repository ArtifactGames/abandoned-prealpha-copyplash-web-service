package com.artifactgames.copyplash.controller

import com.artifactgames.copyplash.WebSocketConfig
import com.artifactgames.copyplash.model.Lobby
import com.artifactgames.copyplash.model.Question
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import org.eclipse.jgit.api.Git
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.io.File
import java.io.FileReader
import java.util.*
import javax.annotation.PostConstruct

@RestController
class LobbyController {

    @Autowired
    lateinit var websocketManager: WebSocketConfig

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
    fun create(): ResponseEntity<Lobby> {
        val lobby = websocketManager.getLobby()
        if (lobby != null) {
            return ResponseEntity.ok(lobby)
        }

        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(null)
    }

}