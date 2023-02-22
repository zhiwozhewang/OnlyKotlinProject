package com.enn.ionic.vm

import android.text.TextUtils
import androidx.lifecycle.viewModelScope
import com.enn.base.base.BaseApp
import com.enn.base.base.BaseViewModel
import com.enn.base.data.getValue
import com.enn.ionic.ConstantObject
import com.enn.ionic.bean.*
import com.enn.ionic.net.FirstRepository
import com.enn.network.entity.ApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.enn.network.entity.ApiSuccessResponse as ApiSuccessResponse1

class FirstViewModel : BaseViewModel() {

    private val repository by lazy { FirstRepository() }

//    private val _uiState = MutableStateFlow<ApiResponse<List<UtilsBean>>>(ApiResponse())
//    val uiState: StateFlow<ApiResponse<List<UtilsBean>>> = _uiState.asStateFlow()

    suspend fun requestFirstUtilsData(
        phone: String? = null,
        roleType: String? = null
    ): ApiResponse<List<UtilsBean>> {
//        _uiState.value =
        return repository.fetchFirstUtilsData(
            BaseApp.instance.getValue("phone", ""),
            BaseApp.instance.getValue("usertype", ConstantObject.USER_TYPE3)
        )
    }

    //    上传移动后的utils模块顺序
    fun changeUtilsOrder(list: MutableList<HqwModelVO>, phone: String) {
        viewModelScope.launch {
            val innerBeans = ArrayList<PostChangeInnerBean>()
            for (hqwModelVO in list) {
                if (!TextUtils.equals("其他", hqwModelVO.modelName)) {
                    innerBeans.add(PostChangeInnerBean(hqwModelVO.modelId))
                }
            }
            repository.setUtilsOrder(innerBeans, phone)
        }
    }

    suspend fun getBanners(
        sysFlag: String,
        type: String
    ): ApiResponse<List<FirstBannerBean>> {

        return repository.fetchBanners(sysFlag, type)
    }

    suspend fun isDongGuan(bpstr: String): ApiResponse<List<Boolean>> {

        return repository.isDongGuan(bpstr)
    }

    suspend fun getCfCode(bpstr: String): ApiResponse<CfCodeBean> {

        return repository.getCfCode(bpstr)
    }

    suspend fun getGasList(
        token: String,
        toGetGasList: ToGetGasList
    ): LoginBean<GasListBean>? {

        return repository.getGasList(token, toGetGasList)
    }

    suspend fun getWeather(
        city: String
    ): LoginBean<WeatherBean>? {
        return repository.getWeather(city)
    }

    suspend fun getTime(): LoginBean<String>? {
        return repository.getTime()
    }

    suspend fun getCityByCustomerBp(
        bp: String
    ): ApiResponse<List<BpCityBean>> {
        return repository.getCityByCustomerBp(bp)
    }

    suspend fun checkUpdate(): UpdateResultBean? {
        return repository.checkUpdate()
    }

    suspend fun getMessageNum(bp: String, icLoginId: Int): ApiResponse<List<Int>> {
        return repository.getMessageNum(
            bp,
            icLoginId
        )
    }

    suspend fun getToDoList(
        pageNum: Int = 1,
        pageSize: Int = 100,
        bp: String, icLoginId: Int?
    ): ApiResponse<List<ToDoBean>> {
        val todocount = repository.getToDoCount(bp)
        var isshow = true
        if (todocount.result?.isNullOrEmpty() != true) {
            todocount.result?.get(0)?.let {
                isshow = it > 0
            }
        }
        val todolisthead = repository.getToDoListHead(bp, icLoginId)

        if (todolisthead is ApiSuccessResponse1) {
            return if (todolisthead.result?.get(0)?.isShow == 1) {

                val todobean = repository.getToDoList(pageNum, pageSize, bp)
                todobean.also { it ->
                    if (it is ApiSuccessResponse1) {
                        val todolistbean = ArrayList<ToDoListBean>()

                        todolisthead.result?.get(0)?.let { it2 ->
                            todolistbean.add(ToDoListBean(title = it2.title, isDeal = it2.isDeal))
                        }

                        it.result?.get(0)?.list?.let { it1 -> todolistbean.addAll(it1) }
                        it.result?.get(0)?.list = todolistbean
                        it.result?.get(0)?.isShow = isshow
                    }
                }
            } else {
                repository.getToDoList(pageNum, pageSize, bp).also {
                    it.result?.get(0)?.isShow = isshow
                }
            }

        } else {
            return repository.getToDoList(pageNum, pageSize, bp).also {
                it.result?.get(0)?.isShow = isshow
            }

        }

    }

    /**
     *  下拉刷新  调用userinfo接口  靠eventbus来刷新数据
     */
    suspend fun toRefreshInfo() {

        repository.refreshInfo()

    }

    //充值缴费时间
    suspend fun getSysTimeIsLimit(): Boolean {

        return repository.getSysTimeIsLimit()
    }
}