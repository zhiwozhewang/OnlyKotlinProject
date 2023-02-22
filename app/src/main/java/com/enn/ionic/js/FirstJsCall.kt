package com.enn.ionic.js

import android.content.Intent
import android.os.Bundle
import android.webkit.JavascriptInterface
import androidx.fragment.app.FragmentActivity
import com.enn.base.base.BaseActivity
import com.enn.base.ktx.getSelfVersionCode
import com.enn.base.ktx.toCall
import com.enn.base.util.parseWebJsonString
import com.enn.ionic.bean.webbean.*
import com.enn.ionic.ui.activity.WebActivityNew
import com.enn.network.utils.LogUtils
import com.google.gson.Gson

class FirstJsCall(val activity: FragmentActivity) {


    @JavascriptInterface
    fun getAppVersion(version: String): String {
//        val webTitleBean = parseWebJsonString<WebAppVersionBean>(version)
        val webAppVersionReturnBean = WebAppVersionReturnBean(activity.getSelfVersionCode())
        return Gson().toJson(webAppVersionReturnBean)
    }


    @JavascriptInterface
    fun openNewUrl(url: String) {
        LogUtils.d("跳转新页面$url")
        val webNewUrlBean = parseWebJsonString<WebNewUrlBean>(url)

        activity.startActivity(
            Intent(activity, WebActivityNew::class.java).apply {
                putExtras(Bundle().apply {
                    putCharSequence("url", webNewUrlBean?.url)

                })
            }
        )

    }

    @JavascriptInterface
    fun phone(phone: String) {
        LogUtils.d("phone:$phone")
        val webPhoneBean = parseWebJsonString<WebPhoneBean>(phone)
        webPhoneBean?.let { activity.toCall(it.phone) }

    }


    @JavascriptInterface
    fun previewOffice(file: String) {

        val webFileBean = parseWebJsonString<WebFileBean>(file)
        //预览

    }


    @JavascriptInterface
    fun showLoading(loading: String) {

        (activity as BaseActivity).showLoading()

    }

    @JavascriptInterface
    fun dismissLoading(loading: String) {
        (activity as BaseActivity).dismissLoading()

    }


}