package com.enn.ionic.net

import com.enn.ionic.ConstantObject.Companion.channelType
import com.enn.ionic.bean.*
import com.enn.network.base.BaseRepository
import com.enn.network.entity.ApiResponse
import com.google.gson.JsonObject
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody


class InfoRepository : BaseRepository() {

    private val mService by lazy {
        RetrofitClient.service
    }


    suspend fun updateInfo(toUpdateInfoBean: ToUpdateInfoBean): ApiResponse<List<Boolean>> {
        return executeHttp {
            mService.updateInfo(toUpdateInfoBean)
        }
    }

    suspend fun getInfo(loginId: String): ApiResponse<List<ToUpdateInfoBean>> {
        return executeHttp {
            mService.getInfo(loginId)
        }
    }
    suspend fun addInfo(smartWxInfoSaveDTO: ToAddInfoBean): ApiResponse<List<Boolean>> {
        return executeHttp {
            mService.addInfo(smartWxInfoSaveDTO)
        }
    }


}