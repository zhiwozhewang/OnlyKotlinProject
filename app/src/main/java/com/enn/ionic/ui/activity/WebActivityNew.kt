package com.enn.ionic.ui.activity

import android.os.Bundle
import android.view.KeyEvent
import com.dylanc.viewbinding.binding
import com.enn.base.base.BaseActivity
import com.enn.base.eventbus.Event
import com.enn.base.eventbus.FlowEventBus
import com.enn.base.util.statusbar.StatusBarCompat
import com.enn.ionic.ConstantObject
import com.enn.ionic.R
import com.enn.ionic.databinding.ActivityWebNewBinding
import com.enn.ionic.js.CommonFunc
import me.jessyan.autosize.internal.CancelAdapt


class WebActivityNew : BaseActivity(R.layout.activity_web_new) {

    val binding by binding<ActivityWebNewBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarCompat.themeAndColorStatusBar(this)
        binding.utilsIm.setOnClickListener { finish() }
        intent.let {
            it.getStringExtra("url")?.let { it1 ->
                initWebView(
                    it1,
                    it.getStringExtra("token"),
                    it.getStringExtra("title")
                )
            }
        }
        FlowEventBus.observe<Event.ShowPayCode>(this) { it ->
            when (it.code) {
                0 -> {
//                  支付成功
                    binding.webWrapper.toCallJs(ConstantObject.SUCESS_CALLBACK_METHOD)
                }
                -1 -> {
//               支付错误
                    binding.webWrapper.toCallJs(ConstantObject.FAILD_CALLBACK_METHOD)
                }
                -2 -> {
                    binding.webWrapper.toCallJs(ConstantObject.FAILD_CALLBACK_METHOD)
//                   用户取消
                }
            }
        }
    }

    private fun initWebView(WEB_URL: String, token: String?, title: String?) {

        title?.let {
            binding.utilsTitle.text = it
        }
        binding.webWrapper.addJavascriptInterface(
            CommonFunc(this, binding),
            ConstantObject.JSOBJECT
        )
//        js调用Android 测试
//        binding.webWrapper.loadUrl("file:///android_asset/test.html");
        binding.webWrapper.loadUrl(WEB_URL, token)
    }

    //设置返回键的监听
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (!(binding.webWrapper.goBack())) {
                finish()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onResume() {
        super.onResume()
        binding.webWrapper.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.webWrapper.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.webWrapper.onDestroy()
    }

}