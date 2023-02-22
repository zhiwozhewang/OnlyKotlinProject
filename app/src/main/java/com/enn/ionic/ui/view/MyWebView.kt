package com.enn.ionic.ui.view

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.webkit.WebView
import me.jessyan.autosize.AutoSize

class MyWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = Resources.getSystem().getIdentifier("webViewStyle", "attr", "android")
) : WebView(context, attrs, defStyleAttr) {


    override fun setOverScrollMode(mode: Int) {
        super.setOverScrollMode(mode)
        AutoSize.autoConvertDensityOfGlobal(context as Activity?)
    }

}