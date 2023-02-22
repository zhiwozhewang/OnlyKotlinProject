package com.enn.ionic.ui.activity

import androidx.activity.viewModels
import com.dylanc.viewbinding.binding
import com.enn.base.base.BaseViewModelActivity
import com.enn.ionic.R
import com.enn.ionic.databinding.ActivityPayBinding
import com.enn.ionic.vm.PayViewModel

/*
* 支付宝接入 https://blog.51cto.com/u_15361941/4375954
*
* */
class PayActivity : BaseViewModelActivity<PayViewModel,ActivityPayBinding>(R.layout.activity_pay) {


    private val mViewModel  by viewModels<PayViewModel>()
    private val mBinding: ActivityPayBinding by binding()
    override fun initData() {
        TODO("Not yet implemented")
    }

}