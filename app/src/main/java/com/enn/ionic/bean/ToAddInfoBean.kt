package com.enn.ionic.bean

import java.io.Serializable

data class ToAddInfoBean(
    var headImageUrl: String? = null,
    val loginId: String? = null,
    var nickname: String? = null,
    var position: String? = null
) : Serializable