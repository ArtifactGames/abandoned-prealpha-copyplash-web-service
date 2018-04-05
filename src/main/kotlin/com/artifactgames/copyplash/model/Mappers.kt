package com.artifactgames.copyplash.model

import com.google.gson.Gson
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketMessage
import java.io.File
import java.io.FileReader


val gson = Gson()


fun <T> T.toJson(): String = gson.toJson(this)
inline fun <reified T> String.fromJson(): T = gson.fromJson(this, T::class.java)
inline fun <reified T> File.fromJsonFile(): T = gson.fromJson(FileReader(this), T::class.java)

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