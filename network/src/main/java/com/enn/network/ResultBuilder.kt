package com.enn.network

import com.enn.network.entity.*
import java.net.SocketTimeoutException

fun <T> ApiResponse<T>.parseData(listenerBuilder: ResultBuilder<T>.() -> Unit) {
    val listener = ResultBuilder<T>().also(listenerBuilder)
    when (this) {
        is ApiSuccessResponse -> listener.onSuccess(this.response)
        is ApiEmptyResponse -> listener.onDataEmpty()
        is ApiFailedResponse -> listener.onFailed(this.errorCode, this.message)
        is ApiErrorResponse -> listener.onError(this.throwable)
    }
    listener.onComplete()
}

class ResultBuilder<T> {
    var onSuccess: (data: T?) -> Unit = {}
    var onDataEmpty: () -> Unit = {}
    var onFailed: (code: Int?, message: String?) -> Unit = { _, message ->
        message?.let { toast(it) }
    }
    var onError: (e: Throwable) -> Unit = { e ->
        e.message?.let {
            if (e is SocketTimeoutException) {
                toast("网络信号差，请稍后")
            } else
                toast(it)
        }
    }
    var onComplete: () -> Unit = {}
}