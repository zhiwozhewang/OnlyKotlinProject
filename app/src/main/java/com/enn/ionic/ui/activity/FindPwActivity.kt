package com.enn.ionic.ui.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import com.dylanc.viewbinding.binding
import com.enn.base.base.BaseActivity
import com.enn.base.util.*
import com.enn.base.util.statusbar.StatusBarCompat
import com.enn.ionic.ConstantObject.Companion.Result_Code_Sucess
import com.enn.ionic.R
import com.enn.ionic.bean.ToFindPwBean
import com.enn.ionic.databinding.ActivityFindpwBinding
import com.enn.ionic.utils.launchWithLoadingNoBase
import com.enn.ionic.utils.seConfiguration
import com.enn.ionic.vm.LoginViewModel
import com.enn.network.toast


class FindPwActivity : BaseActivity(R.layout.activity_findpw) {

    val mBinding by binding<ActivityFindpwBinding>()
    val mViewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarCompat.steepStatusBarText(this, false)
        initView()
    }

    private fun initView() {

        mBinding.findpwBack.setOnClickListener { finish() }
        //        获取验证码
        mBinding.findpwPhoneBtn.setOnClickListener {
            //          发送验证码
            sendCode(mBinding.findpwPhone.text.toString(), it as TextView)
        }
        mBinding.findpwPhone.seConfiguration(mBinding.findpwPhoneDelete)

        mBinding.findpwCode.seConfiguration(mBinding.findpwCodeDelete)

        mBinding.findpwNewEdit.seConfiguration(mBinding.findpwPwDelete, mBinding.findpwIshide)

        /*    //        监听密码输入
            mBinding.findpwNewEdit.addTextChangedListener { text: Editable? ->
                mBinding.findpwIshide.visibility =
                    if (text?.isEmpty() == true) View.GONE else View.VISIBLE
            }
            mBinding.findpwIshide.setOnCheckedChangeListener { compoundButton, b ->
                if (b) {
                    //                密码可见
                    mBinding.findpwNewEdit.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                } else
                //                密码不可见
                    mBinding.findpwNewEdit.transformationMethod =
                        PasswordTransformationMethod.getInstance()
            }*/

        mBinding.findpwBtn.setOnClickListener {
            findPw(
                mBinding.findpwPhone.text.toString(),
                mBinding.findpwCode.text.toString(),
                mBinding.findpwNewEdit.text.toString()
            )
        }
    }

    private fun findPw(phone: String, code: String, newpw: String) {
        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(code) || TextUtils.isEmpty(newpw)) {
            toast("手机号/验证码/密码不能为空")
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
        if (!checkPwd(newpw, 6, 20, 1)) {
            toast("请输入正确格式密码")
            return
        }
        launchWithLoadingNoBase({
            mViewModel.findPw(ToFindPwBean(phone, newpw, code))
        }) {
            onSuccess = {
                if (TextUtils.equals(Result_Code_Sucess, it?.body?.resultCode)) {
                    toast("设置成功")
                    finish()
                } else
                    toast("设置失败:${it?.body?.resultDesc}")
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
                if (TextUtils.equals(Result_Code_Sucess, it?.body?.resultCode)) {
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