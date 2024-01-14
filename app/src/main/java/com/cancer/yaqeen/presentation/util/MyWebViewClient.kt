package com.cancer.yaqeen.presentation.util

import android.graphics.Bitmap
import android.view.KeyEvent
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient


class MyWebViewClient(private val onProgressChanged: (Boolean) -> Unit): WebViewClient() {

    override fun shouldOverrideKeyEvent(view: WebView?, event: KeyEvent?): Boolean {
        return true
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        if(request?.url.toString().indexOf("") > -1 ) return false


//        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
//        activity.startActivity(intent)
        return true
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        onProgressChanged(true)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        onProgressChanged(false)
    }
}