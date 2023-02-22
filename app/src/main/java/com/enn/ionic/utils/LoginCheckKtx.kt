package com.enn.ionic.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.enn.base.base.IUiView
import com.enn.base.data.getValue
import com.enn.base.eventbus.Event
import com.enn.base.eventbus.FlowEventBus
import com.enn.base.ktx.startActivity
import com.enn.ionic.ConstantObject
import com.enn.ionic.R
import com.enn.ionic.ui.activity.CompanyBindActivity
import com.enn.ionic.ui.activity.LoginActivity
import com.enn.ionic.ui.activity.QRCodeScanningActivity
import com.enn.ionic.ui.activity.WebActivityNew
import com.enn.network.ResultBuilder
import com.enn.network.toast
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.youth.banner.util.LogUtils
import kotlinx.coroutines.launch


/**
 * 登录状态监察
 */
fun Context.clickState(toClick: () -> Unit) {
    //用户类型

    val usertype = getValue("usertype", ConstantObject.USER_TYPE0).toString()

    when (usertype) {
        ConstantObject.USER_TYPE0 -> {
//                未登录
            startActivity(Intent(this, LoginActivity::class.java))
        }
//        ConstantObject.USER_TYPE1 -> {
////                1：用气客户，
//        }
//        ConstantObject.USER_TYPE2 -> {
////               2：工程客户
//        }
        ConstantObject.USER_TYPE3 -> {
//                3：游客  判断绑定状态
            //游客登录判断绑定状态
            val bindstate = getValue("bindstate", ConstantObject.USER_BIND_STATUS_LOGIN_NOBIND)
            when (bindstate) {
                ConstantObject.USER_BIND_STATUS_LOGIN_NOBIND -> {
//                去绑定
                    startActivity(Intent(this, CompanyBindActivity::class.java))
                }
                ConstantObject.USER_BIND_STATUS_LOGIN_BINDING -> {
                    toast("等待主账号审核")
                }
                ConstantObject.USER_BIND_STATUS_LOGIN_BIND -> {
                    toClick()
                }
                else -> {}
            }
        }
        else -> {
            toClick()
        }
    }
}

/**
 * 绑定状态监察
 */
fun Context.clickBindState(toClick: () -> Unit) {
    val usertype = getValue("usertype", ConstantObject.USER_TYPE0).toString()
    when (usertype) {
        ConstantObject.USER_TYPE3 -> {
            //游客登录判断绑定状态
            val bindstate = getValue("bindstate", ConstantObject.USER_BIND_STATUS_LOGIN_NOBIND)
            when (bindstate) {
                ConstantObject.USER_BIND_STATUS_LOGIN_NOBIND -> {
//                去绑定
                    startActivity(Intent(this, CompanyBindActivity::class.java))
                }
                ConstantObject.USER_BIND_STATUS_LOGIN_BINDING -> {
                    //认证审核中弹框
                    MyDialogUtils.showBindCheckingDialog()
                }
                ConstantObject.USER_BIND_STATUS_LOGIN_BIND -> {
                    toClick()
                }
                else -> {}
            }
        }
        else -> {
            toClick()
        }
    }

}

/**
 * 接收到全局刷新登录状态eventbus  则退出当前activity
 */
fun AppCompatActivity.toCheckFinish() {
    FlowEventBus.observe<Event.AfterLoginOrOut>(this) { it ->
        LogUtils.e("接受刷新广播,销毁${this.localClassName}")
        finish()
    }
}
