package com.enn.ionic.vm

import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.enn.base.base.BaseViewModel
import com.enn.ionic.bean.*
import com.enn.ionic.net.LoginRepository
import com.enn.ionic.net.RefreshInfoRepository
import com.enn.ionic.net.RetrofitClient
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.youth.banner.util.LogUtils
import kotlinx.coroutines.launch
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Query


class AccountViewModel : BaseViewModel() {


    private val repository by lazy { RefreshInfoRepository() }


    /**
     * 账号相关
     */
    suspend fun getAccountInfo(token: String): LoginBean<AccountBean>? {
        return repository.executeHttpLogin {
            repository.mService.getAccountInfo(token)
        }
    }

    /**
     * 根据bp分页查询非主账号列表
     */
    suspend fun queryAccount(
        token: String,
        pageNum: String = "0",
        pageSize: String = "100",
        icLoginDto: ToQueryAccountBean
    ): LoginBean<TransferBean>? {
        return repository.executeHttpLogin {
            repository.mService.queryAccount(token, pageNum, pageSize, icLoginDto)
        }
    }

    /**
     * 转让主账号身份
     */
    suspend fun transferAccount(
        token: String,
        fromId: Int,
        toId: Int
    ): LoginBean<Any>? {
        LogUtils.e("转移主账号：token:${token},fromid:${fromId},toid:${toId}")
        return repository.executeHttpLogin {
            repository.mService.transferAccount(token, fromId, toId)
        }
    }

    /**
     * 解绑
     */
    suspend fun toUnBind(
        token: String,
        bp: String, username: String
    ): LoginBean<Any>? {
        return repository.executeHttpLogin {
            repository.mService.toUnBind(token, ToUnbindBean(bp, username))
        }
    }

    /**
     *  解绑后 转移主账号后  刷新状态
     */
    suspend fun toRefreshInfo() {

        repository.refreshInfo()

    }

}