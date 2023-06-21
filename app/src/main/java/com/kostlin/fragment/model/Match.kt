package com.kostlin.fragment.model

import com.google.gson.annotations.SerializedName

data class Match(
    @SerializedName("areaName")
    val areaName: String,
    @SerializedName("name")
    val name: Name,
    @SerializedName("format")
    val format: Format,
    @SerializedName("type")
    val type: String,
    @SerializedName("league")
    val league: League
)

data class Name(
    @SerializedName("name")
    val name: String
)

data class Format(
    @SerializedName("format")
    val format: String
)

data class League(
    @SerializedName("name")
    val name: String
)
