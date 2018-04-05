package com.artifactgames.copyplash.model

import com.artifactgames.copyplash.type.GameModes

data class GameMode(val mode: GameModes = GameModes.INSPIRATION, val currentRound: Int = 0, val numberOfRounds: Int = 0, val rounds: List<Round>? = null)

data class Round(val position: Int, val title: String, val description: String, val timeout: Int)

data class GameSettings(val locale:String, val gameMode: GameMode = GameMode())