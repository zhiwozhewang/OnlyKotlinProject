package com.enn.ionic.vm

import android.widget.Toast
import com.enn.base.base.BaseViewModel
import com.enn.ionic.bean.*
import com.enn.ionic.net.LoginRepository
import com.enn.ionic.net.RetrofitClient
import com.tencent.mm.opensdk.modelmsg.SendAuth
import retrofit2.http.Query


class ScanViewModel : BaseViewModel() {

    private val mService by lazy {
        RetrofitClient.service
    }


    /**
     * 拿到id后获取临时token
     */
    suspend fun getScanToken(token: String, id: String): LoginBean<String>? {
        return executeHttpLogin {
            mService.getScanToken(token, id)
        }
    }

    /**
     * 拿到id后获取临时token
     */
    suspend fun toScanBind(
        token: String, tempToken: String,
        id: String,
        companyId: String, username: String
    ): LoginBean<Boolean>? {
        return executeHttpLogin {
            mService.toScanBind(
                token,
                tempToken,
                id,
                companyId,
                username
            )
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