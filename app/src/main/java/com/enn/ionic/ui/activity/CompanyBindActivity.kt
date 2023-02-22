package com.enn.ionic.ui.activity

import android.R.attr.banner
import android.os.Bundle
import android.text.TextUtils
import com.dylanc.viewbinding.binding
import com.enn.base.base.BaseActivity
import com.enn.base.ktx.startActivity
import com.enn.base.util.*
import com.enn.base.util.statusbar.StatusBarCompat
import com.enn.ionic.R
import com.enn.ionic.bean.DataBean
import com.enn.ionic.databinding.ActivityBindCompanyBinding
import com.enn.ionic.utils.toCheckFinish
import com.enn.network.toast
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator


class CompanyBindActivity : BaseActivity(R.layout.activity_bind_company) {

    val mBinding by binding<ActivityBindCompanyBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarCompat.steepStatusBarText(this, false)
        toCheckFinish()
        initView()
    }

    private fun initView() {


        //图片轮播
        mBinding.bindCompanyBanner.setAdapter(object :
            BannerImageAdapter<Int?>(
                listOf(
                    R.mipmap.im_bind_company_banner_im1,
                    R.mipmap.im_bind_company_banner_im2
                )
            ) {
            override fun onBindView(
                holder: BannerImageHolder?,
                data: Int?,
                position: Int,
                size: Int
            ) {
                data?.let { holder?.imageView?.setImageResource(it) }
            }

        })
            .addBannerLifecycleObserver(this) //添加生命周期观察者
            .setIndicator(CircleIndicator(this))
        //
        mBinding.bindCompanyBtn.setOnClickListener {
            findPw(
                mBinding.bindCompanyEdit.text.toString()
            )
        }
        mBinding.bindCompanyBack.setOnClickListener { finish() }
    }

    private fun findPw(code: String) {
        if (TextUtils.isEmpty(code)) {
            toast("输入不能为空")
            return
        }
        startActivity<CompanyBindResultActivity>(Bundle().also { it.putString("code", code) })
//        finish()
    }


}