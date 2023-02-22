package com.enn.base.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.enn.base.util.PayResult
import com.enn.network.toast
import com.enn.network.utils.LogUtils
import com.kongzue.dialogx.dialogs.WaitDialog

abstract class BaseActivity(@LayoutRes contentLayoutId: Int) : AppCompatActivity(contentLayoutId),
    IUiView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun showLoading() {
        LogUtils.e("thread-showLoading:${Thread.currentThread().name}");
        WaitDialog.show("加载中...");
    }

    override fun dismissLoading() {
        WaitDialog.dismiss();
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
    }


}