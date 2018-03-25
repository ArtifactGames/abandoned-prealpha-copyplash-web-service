package com.artifactgames.copyplash.model

data class Player(val id: String, val nick: String = "") {
    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return (other is Player) && other.id == id
    }
}


data class PlayerList(val players: List<Player>)