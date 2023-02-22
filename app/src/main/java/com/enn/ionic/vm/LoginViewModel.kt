package com.enn.ionic.vm

import android.text.TextUtils
import androidx.lifecycle.viewModelScope
import com.enn.base.base.BaseApp
import com.enn.base.base.BaseViewModel
import com.enn.base.data.clearDataStore
import com.enn.base.data.getValue
import com.enn.base.data.putValue
import com.enn.base.eventbus.Event
import com.enn.base.eventbus.FlowEventBus
import com.enn.ionic.ConstantObject
import com.enn.ionic.ConstantObject.Companion.USER_TYPE2
import com.enn.ionic.bean.*
import com.enn.ionic.net.LoginRepository
import com.enn.network.entity.ApiResponse
import com.enn.network.entity.ApiSuccessResponse
import kotlinx.coroutines.launch


class LoginViewModel : BaseViewModel() {

    private val repository by lazy { LoginRepository() }

    /**
     * 场景：不需要监听数据变化
     */
    suspend fun login(logician: ToLoginBean): LoginBean<Data>? {
        return repository.login(logician)
    }

    /**
     * 验证码登录
     */
    suspend fun loginByCode(logician: ToLoginBean): LoginBean<Data>? {
        return repository.loginByCode(logician)
    }

    /**
     * 找回密码
     */
    suspend fun findPw(logician: ToFindPwBean): LoginBean<Data>? {
        return repository.findPw(logician)
    }

    /**
     * 发送验证码
     */
    suspend fun sendCode(phone: String): LoginBean<String>? {
        return repository.sendCode(phone)
    }

    /**
     * 获取用户类型
     */
    @Deprecated("弃用")
    suspend fun getUserType(token: String): LoginBean<String>? {
        return repository.getUserType(token)
    }

    /**
     * 微信登录
     */
    suspend fun toWechatLogin(code: String?): LoginBean<Data>? {
        val toWechatLoginBean = ToWechatLoginBean(
            ConstantObject.channelType, code, "",
            ConstantObject.WECHAT_APP_ID, ""
        )

        return repository.toWechatLogin(toWechatLoginBean)
    }

    /**
     * 微信绑定手机
     */
    suspend fun toWechatLoginBind(username: String?, smsCode: String?): LoginBean<Data>? {
        val toWechatLoginBean = ToWechatLoginBean(
            ConstantObject.channelType, "", username,
            ConstantObject.WECHAT_APP_ID, smsCode
        )
        return repository.toWechatLoginBind(toWechatLoginBean)
    }

    /**
     * 退出登录
     */
    suspend fun toLogout(token: String, username: String): LoginBean<Data>? {
        return repository.toLogout(token, username)
    }

    /**
     * 存储登录数据
     */
    fun saveLoginData(it: LoginBean<Data>?) {
        BaseApp.instance.putValue("token", it?.body?.data?.token)
        // FIXME: 旧有逻辑暂时保留
//                putValue("id", it?.id)
//        BaseApp.instance.putValue("phone", it?.body?.data?.userInfo?.username)
//        BaseApp.instance.putValue("bp", it?.body?.data?.userInfo?.bp)
//        BaseApp.instance.putValue("username", it?.body?.data?.userInfo?.username)
////        BaseApp.instance.putValue("job", it?.body?.data?.userInfo?.job)
//        BaseApp.instance.putValue("icLoginId", it?.body?.data?.userInfo?.userId)
//        BaseApp.instance.putValue("isMaster", it?.body?.data?.userInfo?.isMaster)
//        BaseApp.instance.putValue("reviewStatus", it?.body?.data?.userInfo?.reviewStatus)
////        BaseApp.instance.putValue("headim", it?.body?.data?.userInfo?.headImageUrl)
//        BaseApp.instance.putValue("transferid", it?.body?.data?.userInfo?.icLoginRelationId)
////        BaseApp.instance.putValue("nickname", it?.body?.data?.userInfo?.name)
//        BaseApp.instance.putValue("company", it?.body?.data?.userInfo?.customerName)

    }

    /**
     * 存储用户类型
     */
    @Deprecated("弃用")
    fun saveUserType(usertype: String?) {
        // FIXME: 旧有逻辑暂保留
//        BaseApp.instance.putValue("usertype", usertype)
//        if (TextUtils.equals(ConstantObject.USER_TYPE3, usertype)) {
//
//            var bindstate = ConstantObject.USER_BIND_STATUS_LOGIN_NOBIND
//
//            val bp = BaseApp.instance.getValue("bp", "")
//            val isMaster = BaseApp.instance.getValue("isMaster", -1)
//            val reviewStatus = BaseApp.instance.getValue("reviewStatus", -1)
//            if (TextUtils.isEmpty(bp)) {
////                bp为空游客未绑定
//                bindstate = ConstantObject.USER_BIND_STATUS_LOGIN_NOBIND
//            } else {
//                if (isMaster == 0 && reviewStatus == 2) {
////                   非主账号 待审核
//                    bindstate = ConstantObject.USER_BIND_STATUS_LOGIN_BINDING
//                } else if (isMaster == 1 || (isMaster == 0 && reviewStatus == 1)) {
////主账号  或者是非主账号但审核通过
//                    bindstate = ConstantObject.USER_BIND_STATUS_LOGIN_BIND
//                }
//
//            }
//            //游客下存储绑定状态
//            BaseApp.instance.putValue("bindstate", bindstate)
//        }
//        viewModelScope.launch {
//            val apiResponse = getInfo(BaseApp.instance.getValue("username", ""))
//
//            if (apiResponse is ApiSuccessResponse) {
//                val toUpdateInfoBean = apiResponse.result?.get(0) as ToUpdateInfoBean
//                //保存个人信息数据
//                with(BaseApp) {
//                    instance.putValue("job", toUpdateInfoBean.position)
//                    instance.putValue("headim", toUpdateInfoBean.headImageUrl)
//                    instance.putValue("nickname", toUpdateInfoBean.nickname)
//                }
//            }
//            FlowEventBus.post(Event.AfterLoginOrOut(0))
//
//        }

    }

//    /**
//     *获取个人信息
//     */
//    suspend fun getInfo(loginId: String?): ApiResponse<List<ToUpdateInfoBean>> {
//        return repository.getInfo(loginId.toString())
//    }

//    fun loginOut() {
//        BaseApp.instance.clearDataStore { FlowEventBus.post(Event.AfterLoginOrOut(1)) }
//    }

    /**
     *刷新个人信息
     */
    suspend fun refreshInfo() {
        repository.refreshInfo()
    }


}