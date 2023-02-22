package com.enn.ionic.vm

import com.enn.base.base.BaseViewModel
import com.enn.ionic.bean.*
import com.enn.ionic.net.FirstRepository
import com.enn.ionic.net.RetrofitClient
import com.enn.network.entity.ApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UtilsViewModel : BaseViewModel() {

    private val mService by lazy { RetrofitClient.service }
    private val repository by lazy { FirstRepository() }
    private val _uiState = MutableStateFlow<ApiResponse<List<UtilsBean>>>(ApiResponse())
    val uiState: StateFlow<ApiResponse<List<UtilsBean>>> = _uiState.asStateFlow()
    lateinit var innerBeans: ArrayList<PostChangeInnerBean>


    suspend fun requestUtilsData(phone: String) {
        _uiState.value = repository.executeHttp {
            mService.getUtilsData(phone)
        }
    }

    //    剔除更多
    fun getUtilsHeaderLisr(list: ArrayList<HqwModelVO>): ArrayList<HqwModelVO> {

        return list.apply {
            removeAt(size - 1)
        }
    }

    //保存移动后的顺序
    suspend fun changeUtilsOrder(list: MutableList<HqwModelVO>, phone: String): ApiResponse<String> {
        innerBeans = ArrayList();
        for (hqwModelVO in list) {
            innerBeans.add(PostChangeInnerBean(hqwModelVO.modelId))
        }
        return repository.setUtilsOrder(innerBeans, phone)
    }
    //充值缴费时间
    suspend fun getSysTimeIsLimit(): Boolean {

        return repository.getSysTimeIsLimit()
    }

}