package com.enn.base.base

import android.app.Application
import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.enn.base.R
import com.hjq.toast.ToastUtils
import com.kongzue.dialogx.DialogX
import com.kongzue.dialogx.style.IOSStyle
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshFooter
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.DefaultRefreshFooterCreator
import com.scwang.smart.refresh.layout.listener.DefaultRefreshHeaderCreator
import com.tencent.mmkv.MMKV


open class BaseApp : Application() {


    override fun onCreate() {
        super.onCreate()
        ToastUtils.init(this)
        MMKV.initialize(this)
        DialogX.init(this)
        DialogX.globalStyle = IOSStyle()
        instance = this
        ProcessLifecycleOwner.get().lifecycle.addObserver(ApplicationLifecycleObserver())
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
            layout.setPrimaryColorsId(R.color.purple_40, R.color.white)
             MaterialHeader(context)
        }
        //设置全局的Footer构建器
//        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout -> TODO("Not yet implemented") }
    }

    companion object {
        lateinit var instance: BaseApp
            private set
    }

    private inner class ApplicationLifecycleObserver : DefaultLifecycleObserver {

        override fun onStart(owner: LifecycleOwner) {
            super.onStart(owner)
            // TODO: "ApplicationObserver: app moved to foreground"
        }

        override fun onStop(owner: LifecycleOwner) {
            super.onStop(owner)
            // TODO: "ApplicationObserver: app moved to background"
        }
    }
}