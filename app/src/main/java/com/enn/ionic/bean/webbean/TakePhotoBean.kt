package com.enn.ionic.bean.webbean

data class TakePhotoBean(
    val type: Int, // 0 代表拍照，1代表选择图片
    val maxSelectNum: Int,
    val minSelectNum: Int
)
