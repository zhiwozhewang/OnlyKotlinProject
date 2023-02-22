package com.enn.ionic.download

import java.io.File

fun <T> DownloadState.parseData(listenerBuilder: DownLoadListener<T>.() -> Unit) {
    val listener = DownLoadListener<T>().also(listenerBuilder)
    when (this) {
        is DownloadState.Success -> listener.onSuccess(file)
        is DownloadState.Error -> listener.onError(throwable)
        is DownloadState.InProgress -> listener.onInProgress(progress)
    }
    listener.onComplete()
}

class DownLoadListener<T> {
    var onSuccess: (file: File) -> Unit = {}
    var onError: (throwable: Throwable) -> Unit = {}
    var onInProgress: (progress: Int) -> Unit = {}
    var onComplete: () -> Unit = {}

}