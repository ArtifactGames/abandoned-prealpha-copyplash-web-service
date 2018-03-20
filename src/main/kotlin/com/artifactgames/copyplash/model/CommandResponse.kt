package com.artifactgames.copyplash.model

import com.artifactgames.copyplash.type.CommandAction

data class CommandResponse(val action: CommandAction, val payload: String? = null)