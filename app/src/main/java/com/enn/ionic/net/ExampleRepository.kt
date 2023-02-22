package com.enn.ionic.net

import com.enn.ionic.ConstantObject.Companion.channelType
import com.enn.ionic.bean.DataBean
import com.enn.ionic.bean.LoginBean
import com.enn.ionic.bean.ToLoginBean
import com.enn.network.base.BaseRepository
import com.enn.network.entity.ApiResponse
import com.google.gson.JsonObject


class ExampleRepository : BaseRepository() {

    private val mService by lazy {
        RetrofitClient.service
    }


    suspend fun fetchDataFromNet(): ApiResponse<List<DataBean>> {
        return executeHttp {
            mService.getData()
        }
    }

    suspend fun fetchDataError(): ApiResponse<List<DataBean>> {
        return executeHttp {
            mService.getDataError()
        }
    }

//    suspend fun login(logician: ToLoginBean): LoginBean? {
//        return executeHttpLogin {
//            mService.login(JsonObject().apply {
//                addProperty("username", logician.username)
//                addProperty("password", logician.password)
//                addProperty("channelType", channelType)
//
//
//            })
//        }
//    }


}