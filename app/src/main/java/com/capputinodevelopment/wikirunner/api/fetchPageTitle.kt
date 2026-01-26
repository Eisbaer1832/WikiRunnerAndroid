package com.capputinodevelopment.wikirunner.api

import android.os.Build
import androidx.annotation.RequiresApi
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun fetchPageTitle(url: String, fetchNetworkTitle: Boolean = false): String {
    val rawTitleArr = url.split("/")
    val rawTitle = rawTitleArr[rawTitleArr.size -1]
    val apiURL = "https://api.wikimedia.org/core/v1/wikipedia/de/page/$rawTitle";

    if (fetchNetworkTitle) {
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
    }
    return URLDecoder.decode(rawTitle, StandardCharsets.UTF_8).replace("_" ," ")
}
