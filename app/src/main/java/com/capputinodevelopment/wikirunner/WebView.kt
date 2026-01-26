package com.capputinodevelopment.wikirunner

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Build
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.capputinodevelopment.wikirunner.api.Pages
import com.capputinodevelopment.wikirunner.api.fetchPageTitle


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebView(pages: Pages, static: Boolean = false, goalReached:(linksClicked: MutableList<String>) -> Unit){
    val linksClicked = remember { mutableStateOf(mutableListOf<String>()) }
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

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            //TODO Implement checking for destination url
            val url = request?.url.toString()
            linksClicked.value.add(fetchPageTitle(url, false))
            if(url.contains(pages.endPage)) {
                goalReached(linksClicked.value)
            }
            return static
        }
    }
    val context = LocalContext.current
    var webView = remember { WebView(context) }

    BackHandler(enabled = webView.canGoBack()) {
        webView.goBack()
    }

    AndroidView(factory = { context ->
        WebView(context).apply {
            webViewClient = customWebViewClient
            settings.javaScriptEnabled = true
            loadUrl(pages.startPage)
            webView = this
        }
    }, update = {
        webView = it
    })
}
