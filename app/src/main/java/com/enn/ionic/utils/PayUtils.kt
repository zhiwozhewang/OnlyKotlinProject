package com.enn.ionic.utils

import android.text.TextUtils
import androidx.lifecycle.lifecycleScope
import com.alipay.sdk.app.PayTask
import com.enn.base.base.BaseActivity
import com.enn.base.util.PayResult
import com.enn.ionic.ConstantObject.Companion.WECHAT_APP_ID
import com.enn.ionic.bean.webbean.PayBean
import com.enn.network.utils.LogUtils
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


fun BaseActivity.toWechatPay(payBean: PayBean) {
    //        微信支付
    val api = WXAPIFactory.createWXAPI(this, null);
    api.registerApp(WECHAT_APP_ID)
    //以下这些都应该从服务器去获取
    val payRequest = PayReq()
    payRequest.appId = payBean.appid;//你的微信appid
    payRequest.partnerId = payBean.partnerid;//微信支付分配的商户号
    payRequest.prepayId = payBean.prepayid;//微信返回的支付交易会话ID
    payRequest.packageValue = payBean.`package`;//固定值
    payRequest.nonceStr = payBean.noncestr;//随机字符串
    payRequest.timeStamp = payBean.timestamp;//时间戳
    payRequest.sign = payBean.sign;//签名
    //在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
    //发起请求，调起微信前去支付
    api.sendReq(payRequest)
}

fun BaseActivity.toAliPay2(orderInfo: String, sCallback: () -> Unit, fCallback: () -> Unit) {
    lifecycleScope.launch {

        val payResult = PayResult(startPayTask(orderInfo))
        val resultStatus = payResult.resultStatus
        // 判断resultStatus 为9000则代表支付成功
        when {
            TextUtils.equals(resultStatus, "9000") -> {
                sCallback()
                LogUtils.d("支付成功")
            }
            TextUtils.equals(resultStatus, "6001") -> {
                fCallback()
                LogUtils.d("您未完成付款")
            }
            else -> {
                fCallback()
                LogUtils.d("付款失败")
            }

        }
    }
}

private suspend fun BaseActivity.startPayTask(orderInfo: String): Map<String, String> {
    return withContext(Dispatchers.IO) {
        var alipay = PayTask(this@startPayTask)
        alipay.payV2(
            orderInfo,
            true //传入true表示用户在商户app内部点击付款，是否需要一个 loading 做为在钱包唤起之前的过渡，这个值设置为 true，将会在调用 pay 接口的时候直接唤起一个 loading，直到唤起H5支付页面或者唤起外部的钱包付款页面 loading 才消失
        )
    }

}
