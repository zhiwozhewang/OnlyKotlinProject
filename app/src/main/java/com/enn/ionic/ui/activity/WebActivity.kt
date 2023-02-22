package com.enn.ionic.ui.activity

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import com.dylanc.viewbinding.binding
import com.enn.base.base.BaseActivity
import com.enn.ionic.ConstantObject
import com.enn.ionic.R
import com.enn.ionic.databinding.ActivityWebBinding
import com.enn.network.toast


class WebActivity : BaseActivity(R.layout.activity_web) {

    private val binding by binding<ActivityWebBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        intent.getStringExtra("url")?.let { initWebView(it) }

        intent.let {
            it.getStringExtra("url")?.let { it1 -> initWebView(it1, it.getStringExtra("token")!!) }
        }
    }

    private fun initWebView(WEB_URL: String, token: String) {

//        binding.web?.loadUrl(WEB_URL)

        val webClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return false
            }
        }
        val webChromeClient = object : WebChromeClient() {
            override fun onJsAlert(
                view: WebView?,
                url: String?,
                message: String?,
                result: JsResult?
            ): Boolean {
                return super.onJsAlert(view, url, message, result)
            }
        }

        //下面这些直接复制就好
        binding.web?.webViewClient = webClient
        binding.web?.webChromeClient = webChromeClient
        var webSettings = binding.web!!.settings
        webSettings.javaScriptEnabled = true  // 开启 JavaScript 交互
        webSettings.setAppCacheEnabled(true) // 启用或禁用缓存
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT // 只要缓存可用就加载缓存, 哪怕已经过期失效 如果缓存不可用就从网络上加载数据
        webSettings.setAppCachePath(cacheDir.path) // 设置应用缓存路径

        // 缩放操作
        webSettings.setSupportZoom(false) // 支持缩放 默认为true 是下面那个的前提
        webSettings.builtInZoomControls = false // 设置内置的缩放控件 若为false 则该WebView不可缩放
        webSettings.displayZoomControls = false // 隐藏原生的缩放控件

        webSettings.blockNetworkImage = false // 禁止或允许WebView从网络上加载图片
        webSettings.loadsImagesAutomatically = true // 支持自动加载图片

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webSettings.safeBrowsingEnabled = true // 是否开启安全模式
        }

        webSettings.javaScriptCanOpenWindowsAutomatically = true // 支持通过JS打开新窗口
        webSettings.domStorageEnabled = true // 启用或禁用DOM缓存
        webSettings.setSupportMultipleWindows(true) // 设置WebView是否支持多窗口

        // 设置自适应屏幕, 两者合用
        webSettings.useWideViewPort = true  // 将图片调整到适合webview的大小
        webSettings.loadWithOverviewMode = true  // 缩放至屏幕的大小
        webSettings.allowFileAccess = true // 设置可以访问文件
        webSettings.setGeolocationEnabled(true) // 是否使用地理位置
        binding.web.addJavascriptInterface(this, ConstantObject.JSOBJECT)
        binding.web?.fitsSystemWindows = true
        binding.web?.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        synCookies(this, WEB_URL, "token=$token")
        binding.web?.loadUrl(WEB_URL)
    }

    @JavascriptInterface
    fun setTitle(title: String) {

    }

    //设置返回键的监听
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (binding.web!!.canGoBack()) {
                binding.web!!.goBack()  //返回上一个页面
                return true
            } else {
                finish()
                return true
            }
        }
        return false
    }

    /**
     * 设置Cookie
     *
     * @param url
     */
    private fun setCookies(url: String) {
//        if (!TextUtils.isEmpty(strCookies)) {
//            val arrayCookies: Array<String> = strCookies.split(";")
//            if (arrayCookies != null && arrayCookies.size > 0) {
//                for (cookie in arrayCookies) {
//                    // synCookies(url, cookie);
//                    synCookies(this, url, cookie)
//                }
//            }
//        }
    }

    /**
     * 同步Cookie
     *
     * @param url
     * @param cookie 格式：uid=21233 如需设置多个，需要多次调用
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun synCookies(url: String, cookie: String) {
        try {
            val cookieManager = CookieManager.getInstance()
            cookieManager.setAcceptCookie(true)
            cookieManager.setCookie(url, cookie) //cookies是在HttpClient中获得的cookie
            cookieManager.flush()
        } catch (e: Exception) {
            e.message?.let { toast(it) }
        }
    }


    /**
     * 设置Cookie
     *
     * @param context
     * @param url
     * @param cookie  格式：uid=21233 如需设置多个，需要多次调用
     */
    private fun synCookies(context: Context?, url: String?, cookie: String?) {
        try {
            CookieSyncManager.createInstance(context)
            val cookieManager = CookieManager.getInstance()
            cookieManager.setAcceptCookie(true)
            cookieManager.setCookie(
                url,
                "$cookie;Domain=great-gas-h5.ipaas.enncloud.cn;Path=/"
            ) //cookies格式自定义
            CookieSyncManager.getInstance().sync()
        } catch (e: Exception) {
//            e.message?.let { toast(it) }
        }
    }

    /**
     * 清除Cookie
     *
     * @param context
     */
    fun removeCookie(context: Context?) {
        CookieSyncManager.createInstance(context)
        val cookieManager = CookieManager.getInstance()
        cookieManager.removeAllCookie()
        CookieSyncManager.getInstance().sync()
    }

}