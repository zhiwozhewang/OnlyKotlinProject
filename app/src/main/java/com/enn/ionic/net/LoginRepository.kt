package com.enn.ionic.net

import com.enn.ionic.ConstantObject.Companion.channelType
import com.enn.ionic.bean.*
import com.enn.network.base.BaseRepository
import com.enn.network.entity.ApiResponse
import com.google.gson.JsonObject
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.http.Query


class LoginRepository : RefreshInfoRepository() {


    suspend fun login(logician: ToLoginBean): LoginBean<Data>? {
        return executeHttpLogin {
            mService.login(logician.also { it.channelType = channelType })
        }
    }

    suspend fun loginByCode(toLoginBean: ToLoginBean): LoginBean<Data>? {
        return executeHttpLogin {
            mService.loginByCode(toLoginBean.also { it.channelType = channelType })
        }
    }

    suspend fun findPw(toFindPwBean: ToFindPwBean): LoginBean<Data>? {
        return executeHttpLogin {
            mService.findPw(toFindPwBean)
        }
    }

    suspend fun sendCode(phone: String): LoginBean<String>? {
        val stringBody =
            phone.toRequestBody("text/plain".toMediaTypeOrNull())
        return executeHttpLogin {
            mService.sendCode(stringBody)
        }
    }

    suspend fun getUserType(token: String): LoginBean<String>? {
        return executeHttpLogin {
            mService.getUserType(token)
        }
    }

    suspend fun toWechatLogin(toWechatLoginBean: ToWechatLoginBean): LoginBean<Data>? {
        return executeHttpLogin {
            mService.toWechatLogin(toWechatLoginBean)
        }
    }

    suspend fun toWechatLoginBind(toWechatLoginBean: ToWechatLoginBean): LoginBean<Data>? {
        return executeHttpLogin {
            mService.toWechatLoginBind(toWechatLoginBean)
        }
    }

    suspend fun toLogout(token: String, username: String): LoginBean<Data>? {
        return executeHttpLogin {
            mService.toLogout(token, username)
        }
    }
    // FIXME: 旧有更新头像相关   暂时保留 
//    suspend fun getInfo(loginId: String): ApiResponse<List<ToUpdateInfoBean>> {
//        return executeHttp {
//            mService.getInfo(loginId)
//        }
//    }
}