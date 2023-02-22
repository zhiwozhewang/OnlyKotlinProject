package com.enn.ionic.bean

data class PostChangeBean(
    val list: List<PostChangeInnerBean>,
    val phone: String
)

data class PostChangeInnerBean(
    val modelId: Int
)