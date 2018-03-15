package com.artifactgames.copyplash.model

import java.util.*


data class Lobby(val id: UUID, var password: Int, val gameMode: GameMode?) {
    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return (other is Lobby) && other.id == id
    }

}
