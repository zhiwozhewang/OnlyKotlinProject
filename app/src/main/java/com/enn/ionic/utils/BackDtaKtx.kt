package com.enn.ionic.utils

import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.enn.base.base.BaseApp
import com.enn.base.base.IUiView
import com.enn.base.data.WebBaseBean
import com.enn.base.data.clearDataStore
import com.enn.base.eventbus.Event
import com.enn.base.eventbus.FlowEventBus
import com.enn.ionic.bean.LoginBean
import com.enn.ionic.ui.activity.LoginActivity
import com.enn.network.entity.ApiResponse
import com.enn.network.ResultBuilder
import com.enn.network.parseData
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*


/**
 * 处理非ApiResponse
 */
fun <T> launchFlowNoBase(
    requestBlock: suspend () -> T,
    startCallback: (() -> Unit)? = null,
    completeCallback: (() -> Unit)? = null,
): Flow<T> {
    return flow {
        emit(requestBlock())
    }.onStart {
        startCallback?.invoke()
    }.onCompletion {
        completeCallback?.invoke()
    }
}

/**
 * 请求带Loading&&不需要声明LiveData  返回非ApiResponse
 */
fun <T> IUiView.launchWithLoadingNoBase(
    requestBlock: suspend () -> T,
    listenerBuilder: ResultBuilder<T>.() -> Unit
) {
    lifecycleScope.launch {
        launchFlowNoBase(
            requestBlock,
            { showLoading() },
            { dismissLoading() }).collect { response ->
            checkCode(response, listenerBuilder)

        }
    }
}

private fun <T> checkCode(response: T, listenerBuilder: ResultBuilder<T>.() -> Unit) {
//    同意处理登录错误码
    if (response is LoginBean<*>) {
        when (response.body.resultCode) {
            "1888", "1889", "1999" -> {
                toLogin()
            }
            else -> {
                //            返回数据判断交给回调
                ResultBuilder<T>().also(listenerBuilder).onSuccess(response)
            }
        }
    } else {
        ResultBuilder<T>().also(listenerBuilder).onSuccess(response)
    }
}

fun toLogin() {
    loginOut()
    BaseApp.instance.startActivity(
        Intent(
            BaseApp.instance,
            LoginActivity::class.java
        ).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
}

/**
 * 退出登录操作
 */
fun loginOut() {
    BaseApp.instance.clearDataStore { FlowEventBus.post(Event.AfterLoginOrOut(1)) }
}

/**
 * 请求不带Loading  返回非ApiResponse
 */
fun <T> IUiView.launchNoLoadingNoBase(
    requestBlock: suspend () -> T,
    listenerBuilder: ResultBuilder<T>.() -> Unit
) {
    lifecycleScope.launch {
        launchFlowNoBase(requestBlock).collect { response ->
            checkCode(response, listenerBuilder)
        }
    }
}
