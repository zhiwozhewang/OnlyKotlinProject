package com.enn.ionic.vm

import android.widget.Toast
import com.enn.base.base.BaseViewModel
import com.enn.ionic.bean.*
import com.enn.ionic.bean.webbean.CheckBean
import com.enn.ionic.net.LoginRepository
import com.enn.ionic.net.RetrofitClient
import com.tencent.mm.opensdk.modelmsg.SendAuth
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query


class AccoutnCheckViewModel : BaseViewModel() {

    private val mService by lazy {
        RetrofitClient.service
    }


    /**
     * 审核列表
     */
    suspend fun getCheckList(
        token: String,
        pageNum: Int,
        pageSize: Int
    ): LoginBean<CheckBean>? {
        return executeHttpLogin {
            mService.getCheckList(token, pageNum, pageSize)
        }
    }

    /**
     * 审核
     */
    suspend fun toCheck(
        token: String,
        fromId: Int,
        id: Int, reviewStatus: Int,
    ): LoginBean<Any>? {
        return executeHttpLogin {
            mService.toCheck(token, id, reviewStatus)
        }
    }

    /**
     * 审核
     */
    suspend fun deleteCheck(
        token: String,
        bp: String,
        username: String
    ): LoginBean<CheckBean>? {
        return executeHttpLogin {
            mService.deleteCheck(token, ToDeleteCheckBean(bp, username))
        }
    }


    /**
     * 处理非ApiResponse 数据返回
     */
    suspend fun <T> executeHttpLogin(block: suspend () -> T): T? {
        runCatching {
            block.invoke()
        }.onSuccess { data: T ->
            return data
        }.onFailure { e ->
            return null
        }
        return null
    }

}