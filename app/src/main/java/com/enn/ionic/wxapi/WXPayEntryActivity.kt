package com.enn.ionic.wxapi

import android.app.Activity
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.IWXAPI
import android.os.Bundle
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import android.content.Intent
import com.enn.base.eventbus.Event
import com.enn.base.eventbus.FlowEventBus
import com.enn.ionic.ConstantObject.Companion.WECHAT_APP_ID
import com.enn.network.toast
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.enn.network.utils.LogUtils
import com.tencent.mm.opensdk.modelbase.BaseReq

//        https://juejin.cn/post/6978010182039273479#heading-1
class WXPayEntryActivity : Activity(), IWXAPIEventHandler {


    private var api: IWXAPI? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        api = WXAPIFactory.createWXAPI(this, WECHAT_APP_ID)
        api?.handleIntent(intent, this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        api?.handleIntent(intent, this)
    }

    /**
     * 处理结果回调
     *
     * @param resp
     */
    override fun onResp(resp: BaseResp) {

//        LogUtils.d("onPayFinish: errCode = ${resp.errCode}")
        if (resp.type == ConstantsAPI.COMMAND_PAY_BY_WX) {
            FlowEventBus.post(Event.ShowPayCode(resp.errCode))
            when (resp.errCode) {
                0 -> {
                    toast("支付成功")
                    LogUtils.d("onResp: resp.errCode = 0 支付成功")
                }
                -1 -> {
                    toast("支付错误${resp.errCode}")
                    LogUtils.d("onResp: resp.errCode = -1 支付错误")

                }
                -2 -> {
                    LogUtils.d("onResp: resp.errCode = -2  用户取消")
                    toast("用户取消${resp.errCode}")
                }
            }
            finish() //这里需要关闭该页面
        }
    }

    override fun onReq(baseReq: BaseReq) {}


}