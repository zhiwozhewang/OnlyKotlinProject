package com.enn.ionic.vm

import android.content.Context
import android.provider.UserDictionary.Words.APP_ID
import com.enn.base.base.BaseViewModel
import com.tencent.mm.opensdk.modelpay.PayReq
import com.tencent.mm.opensdk.openapi.WXAPIFactory

class PayViewModel :BaseViewModel() {


    /**
     * 调支付的方法
     * <p>
     * 注意： 每次调用微信支付的时候都会校验 appid 、包名 和 应用签名的。 这三个必须保持一致才能够成功调起微信
     */
     fun startWechatPay( context:Context,payRequest: PayReq) {

        val api = WXAPIFactory.createWXAPI(context, null);
        api.registerApp(APP_ID)
        //以下这些都应该从服务器去获取
/*        PayReq payRequest = new PayReq()
        payRequest.appId = APP_ID;//你的微信appid
        payRequest.partnerId = "1365026102";//微信支付分配的商户号
        payRequest.prepayId = "wx271754067562114fedb5565b1827663000";//微信返回的支付交易会话ID
        payRequest.packageValue = "Sign=WXPay";//固定值
        payRequest.nonceStr = "rao7iwazsddyvtv3iylhgjldj2ticjcr";//随机字符串
        payRequest.timeStamp = "1595843646";//时间戳
        payRequest.sign = "D94996F23D4542B277F35756122DEF7C";//签名*/
        //在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
        //发起请求，调起微信前去支付
        api.sendReq(payRequest)
    }


}