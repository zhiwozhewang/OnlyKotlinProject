package com.enn.ionic.vm

import com.enn.base.base.BaseViewModel
import com.enn.ionic.bean.DataBean
import com.enn.ionic.bean.LoginBean
import com.enn.ionic.bean.ToLoginBean
import com.enn.ionic.net.ExampleRepository
import com.enn.network.entity.ApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ApiViewModel : BaseViewModel() {

    private val repository by lazy { ExampleRepository() }

    private val _uiState = MutableStateFlow<ApiResponse<List<DataBean>>>(ApiResponse())
    val uiState: StateFlow<ApiResponse<List<DataBean>>> = _uiState.asStateFlow()

    suspend fun requestNet() {
        _uiState.value = repository.fetchDataFromNet()
    }

    suspend fun requestNetError() {
        _uiState.value = repository.fetchDataError()
    }


}