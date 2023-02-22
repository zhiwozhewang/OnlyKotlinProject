package com.enn.ionic.bean

import java.io.Serializable

data class CodeBackBean(
    val body: CodeBody,
    val encryptSwitch: String,
    val header: CodeHeader,
    val requestSource: Any
)

data class CodeBody(
    val `data`: String,
    val dataVersion: Any,
    val page: Any,
    val resultCode: String,
    val resultDesc: String
)

data class CodeHeader(
    val appVersionCode: Any,
    val loginUserId: Any,
    val macNo: Any,
    val requestType: String
)