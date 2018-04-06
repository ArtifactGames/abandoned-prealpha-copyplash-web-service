package com.artifactgames.copyplash.datasource

import com.artifactgames.copyplash.model.Question
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import org.eclipse.jgit.api.Git
import org.springframework.stereotype.Component
import java.io.File
import java.io.FileReader
import java.util.*
import javax.annotation.PostConstruct

@Component
class Questions {

    val questionsDir = "copyplash-archive"
    val repoUrl = "https://github.com/ArtifactGames"

    lateinit var questionList: MutableList<Question>

    @PostConstruct
    fun init() {
        fetchQuestionRepository()
    }


    private fun fetchQuestionRepository() {
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

    fun getQuestion() = questionList.firstOrNull()?.also {
        questionList.removeAt(0)
    }


    fun loadQuestionsByLocale(locale: String) {
        val reader = JsonReader(FileReader("$questionsDir/$locale/questions.json"))
        val questions: Map<String, String> = Gson().fromJson(reader, Map::class.java)

        questionList = questions.map {
             Question(it.key, it.value)
        }.shuffled(Random(System.currentTimeMillis())).toMutableList()
    }

}