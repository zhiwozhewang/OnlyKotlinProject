package com.enn.ionic.net

import android.text.TextUtils
import com.enn.base.base.BaseApp
import com.enn.base.data.getValue
import com.enn.base.data.putValue
import com.enn.base.eventbus.Event
import com.enn.base.eventbus.FlowEventBus
import com.enn.ionic.ConstantObject
import com.enn.ionic.bean.*
import com.enn.network.base.BaseRepository
import com.enn.network.entity.ApiSuccessResponse
import com.enn.network.toast
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope


open class RefreshInfoRepository : BaseRepository() {

    val mService by lazy {
        RetrofitClient.service
    }

    suspend fun refreshInfo(delay: Long = 0) {
        torefreshInfo()
//                发送刷新事件
        FlowEventBus.post(Event.AfterLoginOrOut(0), delay)
    }

    /**
     *获取个人信息  无发送eventbus事件
     */
    suspend fun torefreshInfo() {
        val logindata = executeHttpLogin {
            mService.refreshInfo(BaseApp.instance.getValue("token", ""))
        }

        when (logindata?.body?.resultCode) {
            ConstantObject.Result_Code_Sucess -> {
                //保存个人信息数据
                with(BaseApp) {
                    instance.putValue("phone", logindata?.body?.data?.username)
                    instance.putValue("bp", logindata?.body?.data?.bp)
                    instance.putValue("username", logindata?.body?.data?.username)
                    instance.putValue("icLoginId", logindata?.body?.data?.id)
                    instance.putValue("isMaster", logindata?.body?.data?.isMaster)
                    instance.putValue("reviewStatus", logindata?.body?.data?.reviewStatus)
                    instance.putValue("transferid", logindata?.body?.data?.icLoginRelationId)
                    instance.putValue("company", logindata?.body?.data?.customerName)
                    //
                    instance.putValue("usertype", logindata?.body?.data?.userType)
                }
//                处理游客类型
                if (TextUtils.equals(ConstantObject.USER_TYPE3, logindata?.body?.data?.userType)) {

                    var bindstate = ConstantObject.USER_BIND_STATUS_LOGIN_NOBIND

                    val bp = BaseApp.instance.getValue("bp", "")
                    val isMaster = BaseApp.instance.getValue("isMaster", -1)
                    val reviewStatus = BaseApp.instance.getValue("reviewStatus", -1)
                    if (TextUtils.isEmpty(bp)) {
//                bp为空游客未绑定
                        bindstate = ConstantObject.USER_BIND_STATUS_LOGIN_NOBIND
                    } else {
                        if (isMaster == 0 && reviewStatus == 2) {
//                   非主账号 待审核
                            bindstate = ConstantObject.USER_BIND_STATUS_LOGIN_BINDING
                        } else if (isMaster == 1 || (isMaster == 0 && reviewStatus == 1)) {
//主账号  或者是非主账号但审核通过
                            bindstate = ConstantObject.USER_BIND_STATUS_LOGIN_BIND
                        }

                    }
                    //游客下存储绑定状态
                    BaseApp.instance.putValue("bindstate", bindstate)
                    BaseApp.instance.putValue("checkphone", logindata?.body?.data?.masterUsername)

                }

            }
            else -> {}
        }
        //async 的结构化并发
        /*
        如果在 concurrentSum 函数内部发生了错误，并且它抛出了一个异常， 所有在作用域中启动的协程都会被取消。
        */
        coroutineScope {
            async {
                getHeadInfo()
            }
            async {
                queryCompanys()
            }
        }
    }

    /**
     *头像相关信息
     */
    private suspend fun getHeadInfo() {
        val apiResponse = executeHttp {
            mService.getInfo(BaseApp.instance.getValue("username", ""))
        }
        if (apiResponse is ApiSuccessResponse) {
            val toUpdateInfoBean = apiResponse.result?.get(0) as ToUpdateInfoBean
            //保存个人信息数据
            with(BaseApp) {
                instance.putValue("job", toUpdateInfoBean.position)
                instance.putValue("headim", toUpdateInfoBean.headImageUrl)
                instance.putValue("nickname", toUpdateInfoBean.nickname)
            }
        }
    }

    /**
     *切换公司列表信息
     */
    private suspend fun queryCompanys() {
        val apiResponse = executeHttpLogin {
            mService.queryCompanys(
                BaseApp.instance.getValue("token", ""),
                ToSwitchCompanyBean(userName = BaseApp.instance.getValue("username", ""))
            )
        }

        when (apiResponse?.body?.resultCode) {
            ConstantObject.Result_Code_Sucess -> {
                BaseApp.instance.putValue(
                    "hascompanys",
                    !(apiResponse.body.data.isNullOrEmpty() || apiResponse.body.data.size < 2)
                )
            }
            else -> {
                BaseApp.instance.putValue("hascompanys", false)
            }

        }
    }
}