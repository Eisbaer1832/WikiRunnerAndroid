package com.capputinodevelopment.wikirunner.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.preference.PreferenceManager

@Composable
fun Settings(modifier: Modifier) {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LocalContext.current)
    val username = sharedPreferences.getString("username", "")
    val editor = sharedPreferences.edit()
    editor.putString("username", "bebop")
    println("username: " + username)
}