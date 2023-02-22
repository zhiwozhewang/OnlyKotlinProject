package com.enn.ionic.wxapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.enn.base.base.BaseActivity
import com.enn.base.base.IUiView
import com.enn.base.data.putValue
import com.enn.base.eventbus.Event
import com.enn.base.eventbus.FlowEventBus
import com.enn.base.ktx.startActivity
import com.enn.ionic.ConstantObject
import com.enn.ionic.vm.LoginViewModel
import com.enn.network.toast
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.youth.banner.util.LogUtils
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject


class WXEntryActivity : AppCompatActivity(), IWXAPIEventHandler, IUiView {
    private var mWeixinAPI: IWXAPI? = null

    //    private val id_tv: TextView? = null
//    private val nickname_tv: TextView? = null
//    private val sex_tv: TextView? = null
//    private val imageView: ImageView? = null
//    private var handler: Handler? = null
    private var refresh_token: String? = null
    private var expires_in: String? = null
    private val mViewModel by viewModels<LoginViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        handler = Handler()
        mWeixinAPI = WXAPIFactory.createWXAPI(this, ConstantObject.WECHAT_APP_ID)
        mWeixinAPI?.handleIntent(this.intent, this) //必须调用，否则微信不回调Resp
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        mWeixinAPI!!.handleIntent(intent, this) //必须调用，否则微信不回调Resp
    }

    override fun onReq(baseReq: BaseReq) {
        LogUtils.e("微信登录onReq: $baseReq")
        finish()
    }

    override fun onResp(baseResp: BaseResp) {
        LogUtils.e("errStr: " + baseResp.errStr)
        LogUtils.e("openId: " + baseResp.openId)
        LogUtils.e("transaction: " + baseResp.transaction)
        LogUtils.e("errCode: " + baseResp.errCode)
        LogUtils.e("getType: " + baseResp.type)
        LogUtils.e("checkArgs: " + baseResp.checkArgs())
        when (baseResp.errCode) {
            BaseResp.ErrCode.ERR_AUTH_DENIED, BaseResp.ErrCode.ERR_USER_CANCEL ->
                if (RETURN_MSG_TYPE_SHARE == baseResp.type)
                    toast("分享失败")
                else
                    toast("登录失败")
            BaseResp.ErrCode.ERR_OK ->
                when (baseResp.type) {
                    RETURN_MSG_TYPE_LOGIN -> {
                        //拿到了微信返回的code,回登录页登录
                        val code = (baseResp as SendAuth.Resp).code
                        LogUtils.e("微信登录返回code:$code")
                        FlowEventBus.post(Event.ShowWxLoginCode(code))
                        finish()
//                        id_tv!!.text = "code:$code"
//                        toast("登陆成功")
                    }
                    RETURN_MSG_TYPE_SHARE -> {
                        toast("微信分享成功")
                        finish()
                    }
                }
        }
    }

    /**
     * 获取openid accessToken值用于后期操作
     *
     * @param code 请求码
     */
    @Deprecated("本地调用")
    private fun getAccess_token(code: String) {
        val path = (("https://api.weixin.qq.com/sns/oauth2/access_token?appid="
                + ConstantObject.WECHAT_APP_ID
                ) + "&secret="
                + ConstantObject.WECHAT_APP_SECRET
            .toString() + "&code=" + code + "&grant_type=authorization_code")
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder()
            .url(path)
            .get() //默认就是GET请求，可以不写
            .build()
        val call: Call = okHttpClient.newCall(request)
        call.enqueue(object : Callback {

            override fun onFailure(call: Call, e: java.io.IOException) {
                LogUtils.e("获取token失败：${e.localizedMessage}")
            }

            override fun onResponse(call: Call, response: Response) {
                val res: String = response?.body?.string().toString()
                LogUtils.e("onResponse: $res")
                var jsonObject: JSONObject? = null
                try {
                    jsonObject = JSONObject(res)
                    val openid = jsonObject.getString("openid").toString().trim { it <= ' ' }
                    val access_token =
                        jsonObject.getString("access_token").toString().trim { it <= ' ' }
                    refresh_token =
                        jsonObject.getString("refresh_token").toString().trim { it <= ' ' }
                    expires_in = jsonObject.getString("expires_in").toString().trim { it <= ' ' }
                    //                    Toast.makeText(WXEntryActivity.this, "access_token："+access_token, Toast.LENGTH_SHORT).show();
                    getUserMesg(access_token, openid)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }


        })
    }

    /**
     * 获取微信的个人信息
     *
     * @param access_token
     * @param openid
     */
    @Deprecated("本地调用")
    private fun getUserMesg(access_token: String, openid: String) {
        val path = ("https://api.weixin.qq.com/sns/userinfo?access_token="
                + access_token
                + "&openid="
                + openid)
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder()
            .url(path)
            .get() //默认就是GET请求，可以不写
            .build()
        val call: Call = okHttpClient.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: java.io.IOException) {
                LogUtils.e("获取微信个人信息失败：${e.localizedMessage}")

            }

            override fun onResponse(call: Call, response: Response) {
                val res: String = response.body?.string().toString()
                LogUtils.e("全部数据: $res")
                var jsonObject: JSONObject? = null
                try {
                    jsonObject = JSONObject(res)
                    val nickname = jsonObject.getString("nickname")
                    val sex = jsonObject["sex"].toString()
                    val headimgurl = jsonObject.getString("headimgurl")
                    val country = jsonObject.getString("country")
                    val openid1 = jsonObject.getString("openid")
                    val province = jsonObject.getString("province")
                    val unionid = jsonObject.getString("unionid")
                    val city = jsonObject.getString("city")
                    LogUtils.e("用户基本信息:")
                    LogUtils.e("nickname:$nickname")
                    LogUtils.e("sex:       $sex")
                    LogUtils.e("headimgurl:$headimgurl")
                    LogUtils.e("openid:$openid1")
                    LogUtils.e("country:$country")
                    LogUtils.e("province:$province")
                    LogUtils.e("unionid:$unionid")
                    LogUtils.e("city:$city")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }


        })
    }

    companion object {
        private const val RETURN_MSG_TYPE_LOGIN = 1
        private const val RETURN_MSG_TYPE_SHARE = 2
        private const val TAG = "WXEntryActivity"
    }

    override fun showLoading() {

    }

    override fun dismissLoading() {

    }


}