package com.artifactgames.copyplash.model

import java.util.*

data class Lobby(val id: UUID, var password: Int) {
    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return (other is Lobby) && other.id == id
    }

}

data class Player(val id: String, val nick: String = "") {
    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return (other is Player) && other.id == id
    }
}


data class PlayerList(val players: List<Player>)

data class RoundDetails(val title: String, val description: String)

data class Question(val id: String, val description: String)