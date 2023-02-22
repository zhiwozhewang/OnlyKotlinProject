package com.enn.base.util

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.enn.base.base.IUiView
import com.enn.base.data.WebBaseBean
import com.enn.network.entity.ApiResponse
import com.enn.network.ResultBuilder
import com.enn.network.parseData
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun <T> launchFlow(
    requestBlock: suspend () -> ApiResponse<T>,
    startCallback: (() -> Unit)? = null,
    completeCallback: (() -> Unit)? = null,
): Flow<ApiResponse<T>> {
    runBlocking {
        withTimeoutOrNull(200) {}
        launch { }
    }

    return flow {
        emit(requestBlock())
    }.onStart {
        startCallback?.invoke()
    }.onCompletion {
        completeCallback?.invoke()
    }
}

/**
 * 这个方法只是简单的一个封装Loading的普通方法，不返回任何实体类
 */
fun IUiView.launchWithLoading(requestBlock: suspend () -> Unit) {
    lifecycleScope.launch {
        flow {
            emit(requestBlock())
        }.onStart {
            showLoading()
        }.onCompletion {
            dismissLoading()
        }.collect()
    }
}

/**
 * 请求不带Loading&&不需要声明LiveData
 */
fun <T> IUiView.launchAndCollect(
    requestBlock: suspend () -> ApiResponse<T>,
    listenerBuilder: ResultBuilder<T>.() -> Unit
) {
    lifecycleScope.launch {
        launchFlow(requestBlock).collect { response ->
            response.parseData(listenerBuilder)
        }
    }
}

/**
 * 请求带Loading&&不需要声明LiveData
 */
fun <T> IUiView.launchWithLoadingAndCollect(
    requestBlock: suspend () -> ApiResponse<T>,
    listenerBuilder: ResultBuilder<T>.() -> Unit
) {
    lifecycleScope.launch {
        launchFlow(requestBlock, { showLoading() }, { dismissLoading() }).collect { response ->
            response.parseData(listenerBuilder)
        }
    }
}

fun <T> Flow<ApiResponse<T>>.launchAndCollectIn(
    owner: LifecycleOwner,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    listenerBuilder: ResultBuilder<T>.() -> Unit,
) {
    if (owner is Fragment) {
        owner.viewLifecycleOwner.lifecycleScope.launch {
            owner.viewLifecycleOwner.repeatOnLifecycle(minActiveState) {
                collect { apiResponse: ApiResponse<T> ->
                    apiResponse.parseData(listenerBuilder)
                }
            }
        }
    } else {
        owner.lifecycleScope.launch {
            owner.repeatOnLifecycle(minActiveState) {
                collect { apiResponse: ApiResponse<T> ->
                    apiResponse.parseData(listenerBuilder)
                }
            }
        }
    }

}


/**
 * 把 parseWebJsonString 函数的代码直接放入调用者自己的函数体中。这就是 inline 的功效。
 *
 * js調用  不需要回調
 */
inline fun <reified T> parseWebJsonString(string: String?): T? {

    val webBaseBean = string?.let { JsonUtils().fromJson<WebBaseBean<T>>(it) }

    return webBaseBean?.data
}

/**
 *
 * js調用 需要回調
 */
inline fun <reified T> parseWebJsonStringAll(string: String?): T? {

    return string?.let { JsonUtils().fromJson<T>(it) }
}