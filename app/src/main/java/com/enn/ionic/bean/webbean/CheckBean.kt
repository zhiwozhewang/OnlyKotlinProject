package com.enn.ionic.bean.webbean

data class CheckBean(
    val endRow: Int,
    val firstPage: Int,
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean,
    val isFirstPage: Boolean,
    val isLastPage: Boolean,
    val lastPage: Int,
    val list: List<CheckListBean>,
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

data class CheckListBean(
    val address: String,
    val addressNo: Any,
    val companyCode: Any,
    val createTime: String,
    val createUserId: Any,
    val customerName: String,
    val enableStatusId: Int,
    val icLoginId: Int,
    val icLoginRelationId: Any,
    val imgId: Any,
    val isDeleted: Int,
    val isFiled: Int,
    val isLogin: Int,
    val isMaster: Int,
    val isVisible: Any,
    val job: Any,
    val masterUsername: Any,
    val partment: Any,
    val password: Any,
    val queryTimeStamp: Long,
    val roleId: Any,
    val roleValue: Any,
    val tel: Any,
    val updateTime: String,
    val updateUserId: Any,
    val userId: Any,
//    最新返回
    val id: Int,
    val bp: String,
    val name: String,
    val headImage: String,
    val reviewStatus: Int, //审核状态（2-待审核, 1-审核通过, 0-审核未通过,3 超时----）
    val nickname: String,
    val bindingTime: Long,
    val applyBindingTimestamp: Long,
    val username: String


)