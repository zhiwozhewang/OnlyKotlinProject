package com.enn.ionic.ui.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.dylanc.viewbinding.binding
import com.enn.base.base.BaseActivity
import com.enn.base.base.BaseApp
import com.enn.base.data.clearDataStore
import com.enn.base.data.getValue
import com.enn.base.ktx.startActivity
import com.enn.base.util.*
import com.enn.base.util.statusbar.StatusBarCompat
import com.enn.ionic.ConstantObject
import com.enn.ionic.R
import com.enn.ionic.databinding.ActivitySetBinding
import com.enn.ionic.utils.*
import com.enn.ionic.vm.LoginViewModel
import com.enn.network.toast


class MySetActivity : BaseActivity(R.layout.activity_set) {

    val mBinding by binding<ActivitySetBinding>()
    private val mViewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarCompat.themeAndColorStatusBar(this)
        toCheckFinish()
        initView()
    }

    private fun initView() {
        mBinding.setBack.setOnClickListener { finish() }
        mBinding.setAccount.setOnClickListener {
            clickBindState {
                startActivity<MyAccountActivity>()
            }
        }
        mBinding.setAbout.setOnClickListener { startActivity<MyAboutActivity>() }

//
        when (getValue("usertype", ConstantObject.USER_TYPE0)) {
            ConstantObject.USER_TYPE0 -> {
                mBinding.setBtn.visibility = View.GONE
            }
            else -> {
                mBinding.setBtn.setOnClickListener {
                    MyDialogUtils.showLoginoutDialog {
                        toLoginout()
                    }
                }
            }
        }
    }

    private fun toLoginout() {
        launchWithLoadingNoBase({
//                    退出登录
            mViewModel.toLogout(getValue("token", ""), getValue("username", ""))

        }) {
            onSuccess = {

                when (it?.body?.resultCode) {
                    ConstantObject.Result_Code_Sucess -> {
                        loginOut()
                        toast("退出成功")
                        finish()
                    }
                    else -> {
                        toast("退出失败:${it?.body?.resultDesc}")
                    }
                }
            }
        }


    }

}