package com.enn.ionic.net

import com.enn.base.base.BaseApp
import com.enn.base.data.getValue
import com.enn.ionic.ConstantObject
import com.enn.ionic.ConstantObject.Companion.MORE_ID
import com.enn.ionic.bean.*
import com.enn.ionic.utils.CommonUtil
import com.enn.network.base.BaseRepository
import com.enn.network.entity.ApiResponse
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody


class FirstRepository : RefreshInfoRepository() {

    suspend fun fetchFirstUtilsData(phone: String, roleType: String): ApiResponse<List<UtilsBean>> {
        return executeHttp {
            mService.getFirstUtilsData(phone, roleType).apply {

                when (BaseApp.instance.getValue("usertype", ConstantObject.USER_TYPE0)) {
//                    工程用戶不添加更多按鈕
                    ConstantObject.USER_TYPE2 -> {
                        return@apply
                    }
                }
                val utilsBean = result?.get(0) as UtilsBean

                val arrayList = ArrayList<HqwModelVO>()
                utilsBean.hqwModelVOList?.let { arrayList.addAll(it.take(7)) }
                val hqwModelVO = HqwModelVO(
                    "",
                    "file:///android_asset/icon_more.png",
                    "",
                    "",
                    MORE_ID,
                    ConstantObject.UNITLS_MORE_NAME,
                    0
                )
                arrayList.add(hqwModelVO)
                utilsBean.hqwModelVOList = arrayList
//                result = arrayList
            }
        }
    }

    suspend fun setUtilsOrder(
        list: MutableList<PostChangeInnerBean>,
        phone: String
    ): ApiResponse<String> {
        return executeHttp {

            val postChangeBean = PostChangeBean(list, phone)
            val gson = Gson()
//            String转RequestBody String、ByteArray、ByteString都可以用toRequestBody()
            val stringBody =
                gson.toJson(postChangeBean)
                    .toRequestBody("application/json;charset=utf-8".toMediaType())
//            val request: Request = Request
//                .Builder()
//                .post(stringBody)
//                .build()
            mService.setUtilsOrder(stringBody)
        }
    }

    suspend fun fetchBanners(sysFlag: String, type: String): ApiResponse<List<FirstBannerBean>> {
        return executeHttp {
            mService.getBannerData(sysFlag, type)
        }
    }

    suspend fun isDongGuan(bpstr: String): ApiResponse<List<Boolean>> {
        return executeHttp {
            mService.isDongGuan(bpstr)
        }
    }

    suspend fun getCfCode(customerBp: String): ApiResponse<CfCodeBean> {
        return executeHttp {
            mService.getCfCode(customerBp)
        }
    }

    suspend fun getGasList(
        token: String,
        toGetGasList: ToGetGasList
    ): LoginBean<GasListBean>? {
        return executeHttpLogin {
            mService.getGasList(token, toGetGasList)
        }
    }

    suspend fun getWeather(
        city: String
    ): LoginBean<WeatherBean>? {
        return executeHttpLogin {
            mService.getWeather(city)
        }
    }

    suspend fun getTime(): LoginBean<String>? {
        return executeHttpLogin {
            mService.getTime()
        }
    }

    suspend fun getCityByCustomerBp(
        bp: String
    ): ApiResponse<List<BpCityBean>> {
        return executeHttp {
            mService.getCityByCustomerBp(bp)
        }
    }


    suspend fun checkUpdate(): UpdateResultBean? {
        return executeHttpLogin {
            mService.checkUpdate()
        }
    }

    suspend fun getMessageNum(bp: String, icLoginId: Int): ApiResponse<List<Int>> {
        return executeHttp {
            mService.getMessageNum(bp, icLoginId)
        }
    }

    suspend fun getToDoList(
        pageNum: Int,
        pageSize: Int,
        bp: String
    ): ApiResponse<List<ToDoBean>> {
        return executeHttp {
            mService.getToDoList(pageNum, pageSize, bp)
        }
    }

    suspend fun getToDoListHead(
        bp: String,
        icLoginId: Int?
    ): ApiResponse<List<ToDoHeadBean>> {
        return executeHttp {
            mService.getToDoListHead(bp, icLoginId)
        }
    }

    suspend fun getToDoCount(
        bp: String
    ): ApiResponse<List<Int>> {
        return executeHttp {
            mService.getToDoCount(bp)
        }
    }

    suspend fun getSysTimeIsLimit(): Boolean {
        val backBean = executeHttpLogin {
            mService.getSysTime()
        } as LoginBean<String>
        return when (backBean.body.resultCode) {
            ConstantObject.Result_Code_Sucess -> {
                if (backBean.body.data.isNullOrEmpty()) {
                    false
                } else {
                    CommonUtil.checkTimeIsLimit(backBean.body.data)
                }
            }
            else -> {
                false
            }
        }
    }

}