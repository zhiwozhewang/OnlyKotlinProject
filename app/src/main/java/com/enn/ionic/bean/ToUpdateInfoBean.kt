package com.enn.ionic.bean

import java.io.Serializable

data class ToUpdateInfoBean(
    val createTime: String,
    var headImageUrl: String,
    val id: String,
    val loginId: String,
    var nickname: String,
    var position: String,
    val sysLoginRelId: Int,
    val updateTime: String
) : Serializable