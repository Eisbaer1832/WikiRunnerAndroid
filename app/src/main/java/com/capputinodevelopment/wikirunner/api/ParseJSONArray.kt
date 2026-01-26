package com.capputinodevelopment.wikirunner.api

import org.json.JSONArray

fun parseJSONArray(jsonArray: JSONArray): List<String> {
    return List(jsonArray.length()) {
        jsonArray.getString(it)
    }
}