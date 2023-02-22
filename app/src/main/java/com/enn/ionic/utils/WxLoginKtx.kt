package com.enn.ionic.utils

import android.content.Context
import android.widget.Toast
import com.enn.ionic.ConstantObject
import com.enn.network.toast
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory


fun Context.initWx(): IWXAPI {
    val api = WXAPIFactory.createWXAPI(this, null)
    api.registerApp(ConstantObject.WECHAT_APP_ID)
    return api
}

/**
 * 微信登录
 */
fun Context.loginWx() {
    val api = initWx()
    if (!api.isWXAppInstalled) {
        toast("您还未安装微信客户端")
        return
    }
    val req = SendAuth.Req()
    req.scope = "snsapi_userinfo"
    req.state = ConstantObject.WECHAT_LOGIN_STATE //自定义
    api.sendReq(req)
}
