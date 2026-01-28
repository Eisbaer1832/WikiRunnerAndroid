package com.capputinodevelopment.wikirunner.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.preference.PreferenceManager
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.capputinodevelopment.wikirunner.api.Pages
import com.capputinodevelopment.wikirunner.api.fetchPageTitle


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebView(pages: Pages, static: Boolean = false, gaveUp: Boolean = false, goalReached:(linksClicked: MutableList<String>) -> Unit){
    val linksClicked = remember { mutableStateListOf(fetchPageTitle(pages.startPage, false), fetchPageTitle(pages.endPage, false)) }
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorOccurred by remember { mutableStateOf(false) }
    val prefs = PreferenceManager.getDefaultSharedPreferences(LocalContext.current)
    val username = prefs.getString("username", "") ?: ""

    LaunchedEffect(gaveUp) {
        if (gaveUp) {
            linksClicked.removeAt(linksClicked.size - 1)
            goalReached(linksClicked)
        }
    }
    val customWebViewClient = object : WebViewClient() {
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            isLoading = true
            errorOccurred = false
        }
        override fun onPageFinished(view: WebView?, url: String?) {
            isLoading = false
        }
        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            errorOccurred = true
            isLoading = false
        }

        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            if (username == "DEBUG") {
                goalReached(linksClicked)
            }
            val url = request?.url.toString()
            linksClicked.add(linksClicked.size - 1, fetchPageTitle(url, false))
            if (url.contains(pages.endPage)) {
                goalReached(linksClicked)
            }
            if(!url.contains("wikipedia")) {
                return true
            }
            return static
        }
    }
    val context = LocalContext.current
    var webView = remember { WebView(context) }

    BackHandler(enabled = webView.canGoBack()) {
        webView.goBack()
    }
    val isDarkTheme = isSystemInDarkTheme()
    AndroidView(factory = { context ->
        WebView(context).apply {
            webViewClient = customWebViewClient
            loadUrl(pages.startPage)
            webView = this
            with(settings) {
                javaScriptEnabled = true
                domStorageEnabled = true
                isAlgorithmicDarkeningAllowed = true
                if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK) && isDarkTheme) {
                    WebSettingsCompat.setForceDark(
                        this,
                        WebSettingsCompat.FORCE_DARK_ON
                    )
                }
            }
        }
    }, update = { view ->
        view.settings.isAlgorithmicDarkeningAllowed = true
        webView = view
    })
}
