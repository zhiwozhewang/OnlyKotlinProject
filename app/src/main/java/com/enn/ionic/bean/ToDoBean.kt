package com.enn.ionic.bean

data class ToDoBean(
    val endRow: Int,
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean,
    val isFirstPage: Boolean,
    val isLastPage: Boolean,
    var list: List<ToDoListBean>,
    val navigateFirstPage: Int,
    val navigateLastPage: Int,
    val navigatePages: Int,
    val navigatepageNums: List<Any>,
    val nextPage: Int,
    val pageNum: Int,
    val pageSize: Int,
    val pages: Int,
    val prePage: Int,
    val size: Int,
    val startRow: Int,
    val total: Int,
    var isShow: Boolean
)

data class ToDoListBean(
    val content: String? = null,
    val createTime: String? = null,
    val id: Int? = null,
    val infoUrl: String? = null,
    val isDeal: Int? = null,
    val knBcDictionaryId: Any? = null,
    val messageClass: Int? = null,
    val title: String? = null
)