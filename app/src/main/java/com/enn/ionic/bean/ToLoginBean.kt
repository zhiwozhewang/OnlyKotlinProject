package com.enn.ionic.bean

data class ToLoginBean(
    val username: String?,
    val password: String?,
    val smsCode: String?,
    var channelType: String? = null
)