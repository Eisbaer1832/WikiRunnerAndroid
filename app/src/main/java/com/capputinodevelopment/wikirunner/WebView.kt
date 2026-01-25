package com.capputinodevelopment.wikirunner

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.viewinterop.AndroidView


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebView(url: String){
    var isLoading by remember { mutableStateOf(false) }
    var errorOccurred by remember { mutableStateOf(false) }

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
            //TODO Implement checking for destination url
            val url = request?.url.toString()
            return if (url.startsWith("https://wikirunner.tbwebtech.de")) {
                // Allow loading inside WebView
                false
            } else {
                // Block or handle differently (e.g., open in external browser)
                true
            }
        }
    }

    var webView: WebView? = remember { null }

    BackHandler(enabled = webView?.canGoBack() == true) {
        webView?.goBack()
    }

    AndroidView(factory = { context ->
        WebView(context).apply {
            webViewClient = customWebViewClient
            settings.javaScriptEnabled = true
            loadUrl(url)
            webView = this
        }
    }, update = {
        webView = it
    })
}
