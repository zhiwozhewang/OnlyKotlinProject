package com.enn.ionic.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.webkit.*
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.annotation.DrawableRes
import com.enn.ionic.R
import com.enn.network.utils.LogUtils

/**
 * Created by lp on 2019/6/20.
 */
@SuppressLint("SetJavaScriptEnabled")
class WebViewWrapper @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {
    lateinit var webView: WebView
        private set
    lateinit var progressBar: ProgressBar
    lateinit var fragment: FrameLayout
    var url: String? = null

    private fun initView(context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.layout_webview, this)
        webView = view.findViewById<View>(R.id.webView) as WebView
        progressBar = view.findViewById<View>(R.id.progressBar) as ProgressBar
        fragment = view.findViewById<View>(R.id.frameLayout) as FrameLayout
    }

    private fun initWebViewSettings() {
        val settings = webView.settings
        settings.javaScriptEnabled = true // 默认false，设置true后我们才能在WebView里与我们的JS代码进行交互
        settings.javaScriptCanOpenWindowsAutomatically = true // 设置JS是否可以打开WebView新窗口
        settings.setSupportZoom(true) // 支持缩放
        settings.builtInZoomControls = true // 支持手势缩放
        settings.displayZoomControls = false // 不显示缩放按钮
        settings.databaseEnabled = true //数据库存储API是否可用，默认值false。
        settings.saveFormData = true //WebView是否保存表单数据，默认值true。
        settings.domStorageEnabled = true //DOM存储API是否可用，默认false。
        settings.setGeolocationEnabled(true) //定位是否可用，默认为true。
//        settings.setAppCacheEnabled(true) //应用缓存API是否可用，默认值false, 结合setAppCachePath(String)使用。
        settings.useWideViewPort = true // 将图片调整到适合WebView的大小
        settings.loadWithOverviewMode = true // 自适应屏幕
        settings.allowFileAccess = true // 设置可以访问文件
        settings.setGeolocationEnabled(true) // 是否使用地理位置
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            settings.mixedContentMode =
                WebSettings.MIXED_CONTENT_ALWAYS_ALLOW //加上这一句可以在webview中加载https图片，否则加载不出来
        }
        webView?.fitsSystemWindows = true
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        webView.isHorizontalScrollBarEnabled = false //去掉webview的滚动条,水平不显示
        webView.isScrollbarFadingEnabled = true
        webView.scrollBarStyle = SCROLLBARS_OUTSIDE_OVERLAY
        webView.overScrollMode = OVER_SCROLL_NEVER // 取消WebView中滚动或拖动到顶部、底部时的阴影

    }

    private fun initListener() {
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                return super.shouldOverrideUrlLoading(view, request)
            }

//            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap) {
//                super.onPageStarted(view, url, favicon)
////                progressBar?.visibility = VISIBLE
//            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
//                progressBar?.visibility = GONE
                //这个是一定要加上那个的,配合scrollView和WebView的height=wrap_content属性使用
//                val w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
//                val h = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
//                //重新测量
//                webView.measure(w, h)
            }

            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
                super.onReceivedError(view, request, error)
//                progressBar?.visibility = GONE
            }
        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
//                if (newProgress >= 100) {
//                    progressBar?.visibility = GONE
//                } else {
//                    if (progressBar?.visibility == GONE) {
//                        progressBar?.visibility = VISIBLE
//                    }
//                    progressBar?.progress = newProgress
//                }
                super.onProgressChanged(view, newProgress)
            }

            override fun onJsAlert(
                view: WebView?,
                url: String?,
                message: String?,
                result: JsResult?
            ): Boolean {
                return super.onJsAlert(view, url, message, result)
            }
        }
    }

    fun loadUrl(url: String?, token: String? = null) {
        this.url = url
        this.url?.let { it1 ->
            token?.let { synCookies(context, it1, token) }
            LogUtils.d("加载url=$url")
            webView.loadUrl(it1)
        }

    }

    fun toCallJs(method: String?) {
        webView.evaluateJavascript("javascript:$method()", ValueCallback {
            //此处为 js 返回的结果
            LogUtils.d("调用方法$method")
        })
    }

    @SuppressLint("JavascriptInterface")
    fun addJavascriptInterface(objects: Any, name: String) {
        webView.addJavascriptInterface(objects, name)
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
                "token=$cookie"
            ) //cookies格式自定义  ;Domain=great-gas-h5.ipaas.enncloud.cn;Path=/
            CookieSyncManager.getInstance().sync()
        } catch (e: Exception) {
//            e.message?.let { toast(it) }
        }
    }

    fun setProgressDrawable(@DrawableRes id: Int) {
        progressBar.progressDrawable = progressBar.context?.resources?.getDrawable(id)
    }

    fun goBack(): Boolean {
        if (webView.canGoBack()) {
            webView.goBack()
            return true
        }
        return false
    }

    fun onResume() {
        webView.settings.javaScriptEnabled = true
        webView.onResume()
    }

    fun onPause() {
        webView.settings.javaScriptEnabled = false
        webView.onPause()
    }

    fun onDestroy() {
        webView.visibility = GONE
        webView.destroy()
    }

    init {
        initView(context)
        initWebViewSettings()
        initListener()
    }
}