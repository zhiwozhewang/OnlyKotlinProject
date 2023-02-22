package com.enn.network

import com.hjq.toast.ToastUtils

const val SHOW_TOAST = "show_toast"

fun toast(msg: String) {
    ToastUtils.show(msg)
}