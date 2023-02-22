package com.enn.ionic.ui.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.dylanc.viewbinding.binding
import com.enn.base.base.BaseActivity
import com.enn.base.base.BaseApp
import com.enn.base.data.clearDataStore
import com.enn.base.data.getValue
import com.enn.base.ktx.getSelfVersionName
import com.enn.base.ktx.startActivity
import com.enn.base.util.*
import com.enn.base.util.statusbar.StatusBarCompat
import com.enn.ionic.ConstantObject
import com.enn.ionic.R
import com.enn.ionic.databinding.ActivityAboutBinding
import com.enn.ionic.databinding.ActivitySetBinding
import com.enn.ionic.utils.MyDialogUtils
import com.enn.ionic.vm.LoginViewModel
import com.enn.network.toast


class MyAboutActivity : BaseActivity(R.layout.activity_about) {

    val mBinding by binding<ActivityAboutBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarCompat.themeAndColorStatusBar(this)
        initView()
    }

    private fun initView() {
        mBinding.aboutBack.setOnClickListener { finish() }
        mBinding.aboutTelLayer.setOnClickListener { MyDialogUtils.showPhoneDialog(ConstantObject.Customer_service_PHONE) }
        mBinding.aboutImText.text = "慧用能Version ${getSelfVersionName()}"
    }


}