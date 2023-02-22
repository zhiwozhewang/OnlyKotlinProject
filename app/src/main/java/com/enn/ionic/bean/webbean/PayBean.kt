package com.enn.ionic.bean.webbean

data class PayBean(
    val appid: String,
    val noncestr: String,
    val orderInfo: String,
    val `package`: String,
    val partnerid: String,
    val prepayid: String,
    val sign: String,
    val timestamp: String,
    val type: Int
)

data class OrderInfo(
    val addr: String,
    val area: String,
    val bcardid: String,
    val cardid: String,
    val cjdfg: String,
    val cnYear: String,
    val cnote: String,
    val fcano: String,
    val gasBukrs: String,
    val gasFee: String,
    val gmenge: String,
    val higmng: String,
    val higprice: String,
    val higval: String,
    val mormng: String,
    val morprice: String,
    val morval: String,
    val normng: String,
    val norval: String,
    val rentalBukrs: String,
    val rentalFee: String,
    val resmenge: String,
    val resprice: String,
    val resyjval: String,
    val rsoce: String,
    val serge: String,
    val sernr: Long,
    val sum: String,
    val terminalType: Int,
    val tradeChannel: String,
    val vertrag: Long,
    val vkont: String
)