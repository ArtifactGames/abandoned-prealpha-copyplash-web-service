package com.artifactgames.copyplash.type

import com.google.gson.annotations.SerializedName

enum class CommandAction {

    @SerializedName("0")
    SEND,
    @SerializedName("1")
    RETRIEVE,
    @SerializedName("2")
    SET_NICK,
    @SerializedName("3")
    SET_NICK_SUCCESS,

}