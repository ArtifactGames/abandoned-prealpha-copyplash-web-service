package com.artifactgames.copyplash.model

import com.artifactgames.copyplash.type.GameModes

data class GameMode(val mode: GameModes = GameModes.INSPIRATION, val rounds: List<Round>) {
    companion object {
        fun popRound(gameMode: GameMode): Pair<GameMode, Round?> {
            val round  = gameMode.rounds.take(1)
            return if (round.isEmpty()) {
                GameMode(gameMode.mode, emptyList()) to null
            } else {
                GameMode(gameMode.mode, gameMode.rounds.drop(1)) to round.first()
            }
        }
    }
}

data class Round(val position: Int, val title: String, val description: String, val timeout: Int)

data class GameSettings(val locale:String)