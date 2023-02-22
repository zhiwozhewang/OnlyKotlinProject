package com.enn.ionic.bean

data class ToWechatLoginBean(
    val channelType: String,
    val code: String?,
    val username: String?,
    val wxOpenId: String,
    val smsCode: String?

)