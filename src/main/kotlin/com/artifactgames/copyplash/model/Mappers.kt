package com.artifactgames.copyplash.model

import com.artifactgames.copyplash.type.CommandAction
import com.google.gson.Gson
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketMessage


val gson = Gson()


fun <T> T.toJson(): String = gson.toJson(this)

fun WebSocketMessage<*>.mapMessageToCommandRequest(): CommandRequest? =
    try {
        gson.fromJson(payload.toString(), CommandRequest::class.java)
    } catch (e: Exception) {
        null
    }

inline fun <reified T> CommandRequest.deserialize(): T? =
    try {
        gson.fromJson(payload, T::class.java)
    } catch(e: Exception) {
        null
    }

fun CommandResponse.serialize(): TextMessage? =
    try {
        TextMessage(gson.toJson(this))
    } catch(e: Exception) {
        null
    }

fun CommandRequest.mapToCommandResponse(): CommandResponse? =
    when(action) {
        CommandAction.SET_NICK -> CommandResponse(CommandAction.SET_NICK_SUCCESS)
        else -> {
            null
        }
    }