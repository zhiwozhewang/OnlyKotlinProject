package com.enn.ionic.bean

data class GasListBean(
    val endRow: Int,
    val firstPage: Int,
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean,
    val isFirstPage: Boolean,
    val isLastPage: Boolean,
    val lastPage: Int,
    val list: List<GasItem>,
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

data class GasItem (
    val amountLeftToday: Any,
    val amountLeftValue: Any,
    val amountLeftYesterday: Any,
    val balance: String,
    val bjTplnrTxt: String,
    val budat: Any,
    val iotMeter: Int,
    val meterType: String,
    val notFoundSapData: String,
    val partner: String,
    val partnerTxt: Any,
    val predictDays: String,
    val serge: String,
    val showIot: Boolean,
    val showIotType: String,
    val status: String,
    val vabrmenge: Any
)