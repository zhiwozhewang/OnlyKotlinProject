package com.enn.ionic.vm

import android.text.TextUtils
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.enn.base.base.BaseApp
import com.enn.base.base.BaseViewModel
import com.enn.base.data.getValue
import com.enn.base.data.putValue
import com.enn.base.eventbus.Event
import com.enn.base.eventbus.FlowEventBus
import com.enn.ionic.ConstantObject
import com.enn.ionic.bean.*
import com.enn.ionic.net.LoginRepository
import com.enn.ionic.net.RefreshInfoRepository
import com.enn.ionic.net.RetrofitClient
import com.enn.ionic.net.SwitchCompanyRepository
import com.enn.network.entity.ApiSuccessResponse
import com.tencent.mm.opensdk.modelmsg.SendAuth
import kotlinx.coroutines.launch
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Query


class SwitchCompanyViewModel : BaseViewModel() {


    private val repository by lazy { RefreshInfoRepository() }


    /**
     * 公司列表查询
     */
    suspend fun queryCompanys(
        token: String,
        changingParamString: ToSwitchCompanyBean
    ): LoginBean<List<QueryCompanyBackBean>>? {
        return repository.executeHttpLogin {
            repository.mService.queryCompanys(
                token,
                changingParamString
            )
        }
    }

    /**
     * 切换公司
     */
    suspend fun changeCompany(
        token: String,
        changingParamString: ToSwitchCompanyBean
    ): LoginBean<Data>? {
        return repository.executeHttpLogin {
            repository.mService.changeCompany(token, changingParamString)
        }
    }

    /**
     * 切换公司后刷新状态
     */
    fun toRefreshInfo(delay: Long = 0) {

        viewModelScope.launch {
            repository.refreshInfo(delay)
//            FlowEventBus.post(Event.AfterLoginOrOut(0))
        }

    }

}