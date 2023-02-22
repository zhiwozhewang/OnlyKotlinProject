package com.enn.ionic.js

import android.text.TextUtils
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.enn.ionic.net.RetrofitClient
import com.enn.network.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class BaseFunc(val activity: AppCompatActivity, private val mWebView: WebView) {

    lateinit var token: String

    init {
        refreshToken()
    }

    /**
     * 回调数据给HTML端
     *
     * @param value
     */
    protected fun callBackHtml(name: String, value: String) {
        activity.lifecycleScope.launch {
            if (TextUtils.isEmpty(value)) {
                mWebView.loadUrl("javascript:$name();")
            } else {
                mWebView.loadUrl("javascript:" + name + "('" + convertChar(value) + "');")
            }
        }
    }

    private fun convertChar(str: String): String {
        var str = str
        str = str.replace("\"".toRegex(), "\\\\\"")
        str = str.replace("\'".toRegex(), "\\\\'")
        str = str.replace("\\r\\n", "")
        return str
    }


    private fun refreshToken() {
        activity.lifecycleScope.launch(Dispatchers.IO) {
            activity.runCatching {
                val response =
                    RetrofitClient.service.getToken(TokenBody("gradmin", "admin123", "serlink_h5"))
                if (response.code == 200) {
                    token = response.token
                } else {
                    toast("获取文件服务器token失败")
                }
            }.onFailure {
                toast("获取文件服务器token异常")
            }
        }
    }
}