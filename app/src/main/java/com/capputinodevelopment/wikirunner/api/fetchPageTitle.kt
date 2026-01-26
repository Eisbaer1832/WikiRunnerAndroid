package com.capputinodevelopment.wikirunner.api

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

fun fetchPageTitle(url: String): String {
    val rawTitleArr = url.split("/")
    val rawTitle = rawTitleArr[rawTitleArr.size -1]
    val apiURL = "https://api.wikimedia.org/core/v1/wikipedia/de/page/" + rawTitle;
    //TODO fix this mes
    try {
        val connection = URL(apiURL).openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.inputStream.bufferedReader(Charsets.UTF_8).use {
            println(it.readText())
            val response = JSONObject(it.readText())
            println(response)
            return response.getString("title")
        }

    } catch (e: Exception) {
        e.printStackTrace()
    }

    return rawTitle
}
