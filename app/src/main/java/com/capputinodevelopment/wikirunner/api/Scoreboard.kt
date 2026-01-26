package com.capputinodevelopment.wikirunner.api

data class Scoreboard(
    val users: List<String> = listOf(),
    val times: List<String> = listOf(),
    val linkList: List<String> = listOf()
)
