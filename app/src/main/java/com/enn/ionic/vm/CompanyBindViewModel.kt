package com.enn.ionic.vm

import androidx.lifecycle.viewModelScope
import com.enn.base.base.BaseApp
import com.enn.base.base.BaseViewModel
import com.enn.base.data.putValue
import com.enn.ionic.ConstantObject
import com.enn.ionic.bean.*
import com.enn.ionic.net.LoginRepository
import com.enn.ionic.net.RefreshInfoRepository
import com.enn.ionic.net.RetrofitClient
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.http.Query


class CompanyBindViewModel : BaseViewModel() {


    private val repository by lazy { RefreshInfoRepository() }


    /**
     * 认证- 查询客户信息
     */
    suspend fun toQueryCustomerInfo(token: String, code: String): LoginBean<List<BindCompanyQuryBean>>? {
        val stringBody =
            code.toRequestBody("text/plain".toMediaTypeOrNull())
        return repository.executeHttpLogin {
            repository.mService.toQueryCustomerInfo(token, stringBody)
        }
    }

    /**
     * 认证-绑定客户信息
     */
    suspend fun toBindCustomer(
        token: String,
        bindCustomerDto: ToBindCustomerBean
    ): LoginBean<BindCustomerBean>? {

        val stringBody =
            Gson().toJson(bindCustomerDto).toRequestBody("application/json".toMediaTypeOrNull())

        return repository.executeHttpLogin {
            repository.mService.toBindCustomer(token, stringBody)
        }
    }

    /**
     * 绑定后刷新状态
     */
    suspend fun toRefreshInfo() {
        repository.refreshInfo()
    }

}