package com.enn.ionic.bean

data class TransferBean(
    val endRow: Int,
    val firstPage: Int,
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean,
    val isFirstPage: Boolean,
    val isLastPage: Boolean,
    val lastPage: Int,
    val list: List<TransferListBean>,
    val navigateFirstPage: Int,
    val navigateLastPage: Int,
    val navigatePages: Int,
    val navigatepageNums: List<Int>,
    val nextPage: Int,
    val pageNum: Int,
    val pageSize: Int,
    val pages: Int,
    val prePage: Int,
    val size: Int,
    val startRow: Int,
    val total: Int
)

data class TransferListBean(
    val address: String,
    val addressNo: Any,
    val applyBindingTimestamp: Long,
    val bp: String,
    val companyCode: Any,
    val createTime: String,
    val createUserId: Any,
    val customerName: String,
    val enableStatusId: Int,
    val icLoginId: Int,
    val icLoginRelationId: Any,
    val id: Int,
    val imgId: Any,
    val headImageUrl: String,
    val isDeleted: Int,
    val isFiled: Int,
    val isLogin: Int,
    val isMaster: Int,
    val isVisible: Any,
    val job: Any,
    val masterUsername: Any,
    val name: String,
    val nickName: String,
    val partment: Any,
    val password: Any,
    val queryTimeStamp: Long,
    val reviewStatus: Int,
    val roleId: Any,
    val roleValue: Any,
    val tel: Any,
    val updateTime: String,
    val updateUserId: Any,
    val userId: Any,
    val username: String
)