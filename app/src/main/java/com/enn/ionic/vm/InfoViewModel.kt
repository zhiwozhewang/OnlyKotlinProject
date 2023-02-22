package com.enn.ionic.vm

import android.widget.Toast
import com.enn.base.base.BaseApp
import com.enn.base.base.BaseViewModel
import com.enn.base.data.putValue
import com.enn.ionic.bean.*
import com.enn.ionic.net.InfoRepository
import com.enn.ionic.net.LoginRepository
import com.enn.ionic.net.RetrofitClient
import com.enn.network.entity.ApiEmptyResponse
import com.enn.network.entity.ApiResponse
import com.tencent.mm.opensdk.modelmsg.SendAuth
import retrofit2.http.Query


class InfoViewModel : BaseViewModel() {


    private val repository by lazy { InfoRepository() }


    /**
     *修改个人信息
     */
    suspend fun updateInfo(toUpdateInfoBean: ToUpdateInfoBean): ApiResponse<List<Boolean>> {
        return repository.updateInfo(toUpdateInfoBean)
    }

    /**
     *获取个人信息
     */
    suspend fun getInfo(loginId: String?): ApiResponse<List<ToUpdateInfoBean>> {
        return repository.getInfo(loginId.toString())
    }
    /**
     *获取个人信息
     */
    suspend fun addInfo(smartWxInfoSaveDTO: ToAddInfoBean): ApiResponse<List<Boolean>> {
        return repository.addInfo(smartWxInfoSaveDTO)
    }

    /**
     * 存储个人信息数据
     */
    fun saveInfoData(it: ToUpdateInfoBean?) {

        BaseApp.instance.putValue("job", it?.position)
        BaseApp.instance.putValue("headim", it?.headImageUrl)
        BaseApp.instance.putValue("nickname", it?.nickname)

    }
}