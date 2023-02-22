package com.enn.ionic.ui.activity

import android.os.Bundle
import android.text.TextUtils
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.dylanc.viewbinding.binding
import com.enn.base.base.BaseActivity
import com.enn.base.data.putValue
import com.enn.base.ktx.startActivity
import com.enn.base.util.*
import com.enn.base.util.statusbar.StatusBarCompat
import com.enn.ionic.ConstantObject
import com.enn.ionic.R
import com.enn.ionic.bean.ToLoginBean
import com.enn.ionic.databinding.ActivityBindPhoneBinding
import com.enn.ionic.utils.launchNoLoadingNoBase
import com.enn.ionic.utils.launchWithLoadingNoBase
import com.enn.ionic.vm.LoginViewModel
import com.enn.network.toast
import kotlinx.coroutines.launch


class BindPhoneActivity : BaseActivity(R.layout.activity_bind_phone) {

    val mBinding by binding<ActivityBindPhoneBinding>()
    private val mViewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarCompat.steepStatusBarText(this, false)
        initView()
    }

    private fun initView() {

        mBinding.bindPhoneBack.setOnClickListener { finish() }
        //        获取验证码
        mBinding.bindPhoneCodeBtn.setOnClickListener {
            //            发送验证码
            sendCode(mBinding.bindPhoneEdit.text.toString(), it as TextView)
        }


        mBinding.bindPhoneBtn.setOnClickListener {
            findPw(
                mBinding.bindPhoneEdit.text.toString(),
                mBinding.bindPhoneCode.text.toString(),
            )
        }
    }

    private fun findPw(phone: String, code: String) {
        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(code)) {
            toast("手机号/验证码不能为空")
            return
        }
        if (!checkPhoneNum(phone)) {
            toast("请输入正确格式手机号")
            return
        }
        if (!checkPwd(code, 6, 6, 1)) {
            toast("请输入正确格式验证码")
            return
        }
        toLogin(phone, code)
    }

    private fun toLogin(phone: String?, smsCode: String?) {
        launchWithLoadingNoBase({
//                    验证码登录
            mViewModel.toWechatLoginBind(phone, smsCode)

        }) {
            onSuccess = {

                when (it?.body?.resultCode) {
                    ConstantObject.Result_Code_Sucess -> {
                        mViewModel.saveLoginData(it)
                        lifecycleScope.launch {
                            mViewModel.refreshInfo()
                            toast("绑定成功")
                            finish()
                        }
//                 fixme   旧有逻辑  获取用户类型

//                        it?.body?.data?.token?.let { it1 -> toGetUserType(it1) }
                    }
                    else -> {
                        toast("绑定失败:${it?.body?.resultDesc}")
                    }
                }
            }
        }


    }

    @Deprecated("弃用")
    private fun toGetUserType(token: String) {
        launchNoLoadingNoBase({
            mViewModel.getUserType(token)
        }) {
            onSuccess = {
                if (TextUtils.equals(ConstantObject.Result_Code_Sucess, it?.body?.resultCode)) {
                    toast("登录成功")
                    mViewModel.saveUserType(it?.body?.data)
                    finish()
                } else
                    toast("${it?.body?.resultDesc}")
            }
        }

    }

    private fun sendCode(phone: String, view: TextView) {
        if (TextUtils.isEmpty(phone)) {
            toast("请输入手机号")
            return
        }
        if (!checkPhoneNum(phone)) {
            toast("请输入正确格式手机号")
            return
        }
        view.isClickable = false
        launchWithLoadingNoBase({ mViewModel.sendCode(phone) }) {
            onSuccess = {
                if (TextUtils.equals(ConstantObject.Result_Code_Sucess, it?.body?.resultCode)) {
                    toast("发送成功")
                    countDownCoroutines(60, { i: Int ->
                        view.text = "重新发送($i)"
                    }, {
                        view.isClickable = true
                        view.text = "获取验证码"
                    })
                } else {
                    view.isClickable = true
                    toast("发送失败:${it?.body?.resultDesc}")
                }
            }

        }
    }

}