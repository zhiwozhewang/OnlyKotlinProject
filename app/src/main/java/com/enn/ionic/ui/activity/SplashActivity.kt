package com.enn.ionic.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.enn.base.base.BaseActivity
import com.enn.base.ktx.startActivity
import com.enn.base.util.statusbar.StatusBarCompat
import com.enn.ionic.R


class SplashActivity : BaseActivity(R.layout.activity_splash) {

    private val mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            startActivity<HomeActivity>()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT != 0) {
            finish()
            return
        }

        StatusBarCompat.steepStatusBarText(this)
        mHandler.sendEmptyMessageDelayed(1, 1000)
    }
}