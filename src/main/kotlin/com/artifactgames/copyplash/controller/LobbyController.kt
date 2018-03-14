package com.artifactgames.copyplash.controller

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import org.eclipse.jgit.api.Git
import org.springframework.web.bind.annotation.*
import java.io.File
import java.util.*
import java.io.FileReader
import javax.annotation.PostConstruct


data class Lobby(val id: UUID, val password: Int, val gameMode: GameMode)
data class GameMode(val gameMode: GameModes, val state: States, val questionList: List<Question>)
data class Question(val id: String, val description: String)

enum class GameModes {
    INSPIRATION,
}

enum class States(state: Int) {
    START(0),

}

@RestController("lobby/")
class LobbyController {

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

    fun getInitialGameMode() = GameMode(GameModes.INSPIRATION, States.START, questionList)

    @GetMapping("/lobby-create")
    fun create(): Lobby {
        val id = UUID.randomUUID()
        val password = Math.abs(Random().nextInt(8999) + 1000)
        return Lobby(id, password, getInitialGameMode())
    }

}