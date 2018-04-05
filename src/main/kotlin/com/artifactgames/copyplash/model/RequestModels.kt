package com.artifactgames.copyplash.model

import com.artifactgames.copyplash.type.GameModes
import com.artifactgames.copyplash.type.States


data class GameMode(val gameMode: GameModes, val state: States)

data class GameSettings(val locale:String)