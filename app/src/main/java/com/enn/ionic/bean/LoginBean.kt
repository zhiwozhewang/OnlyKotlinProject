package com.enn.ionic.bean

import java.io.Serializable

data class LoginBean<T>(
    val body: Body<T>,
    val encryptSwitch: String,
    val header: Header,
    val requestSource: Any
) : Serializable {

}

data class Body<T>(
    val `data`: T,
    val dataVersion: Any,
    val page: Any,
    val resultCode: String,
    val resultDesc: String
)

data class Header(
    val appVersionCode: Any,
    val loginUserId: Any,
    val macNo: Any,
    val requestType: String
)

data class Data(
    val token: String,
    val userInfo: UserInfo
)

data class UserInfo(
    val address: String,
    val addressNo: String,
    val bp: String,
    val channelType: Any,
    val createTime: String,
    val createUserId: Any,
    val customerName: String,
    val enableStatusId: Int,
    val id: Int,
    val imgId: Any,
    val isDeleted: Int,
    val isFiled: Int,
    val isLogin: Int,
    val isMaster: Int,
    val job: String,
    val loginTrackVO: Any,
    val masterUsername: Any,
    val name: String,
    val partment: String,
    val password: String,
    val reviewStatus: Int,
    val headImageUrl: String,
    val roleId: Int,
    val roleValue: String,
    val sourceId: Int,
    val tel: String,
    val updateTime: String,
    val updateUserId: Any,
    val userId: Int,
    val username: String,
//    转移主账号用
    val icLoginRelationId: Int
)