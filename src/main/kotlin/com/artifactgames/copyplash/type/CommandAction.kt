package com.artifactgames.copyplash.type

import com.google.gson.annotations.SerializedName

enum class CommandAction {

    @SerializedName("0")
    SEND,
    @SerializedName("1")
    RETRIEVE,
    @SerializedName("2")
    UPDATE_PLAYERS,
    @SerializedName("3")
    START_GAME,
    @SerializedName("4")
    UPDATE_COUNTER,
    @SerializedName("5")
    SEND_ROUND_DETAILS,
    @SerializedName("6")
    START_ROUND,
    @SerializedName("7")
    ROUND_FINISH_HOST,
    @SerializedName("10000")
    SET_NICK,
    @SerializedName("10001")
    SET_NICK_SUCCESS,
    @SerializedName("10002")
    LAUNCH_ROUND,
    @SerializedName("10003")
    ROUND_FINISH_PLAYER,

}