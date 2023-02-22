package com.enn.ionic.ui.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import com.dylanc.viewbinding.binding
import com.enn.base.base.BaseActivity
import com.enn.base.data.getValue
import com.enn.base.eventbus.Event
import com.enn.base.eventbus.FlowEventBus
import com.enn.base.ktx.startActivity
import com.enn.base.util.statusbar.StatusBarCompat
import com.enn.ionic.ConstantObject
import com.enn.ionic.ConstantObject.Companion.Result_Code_Sucess
import com.enn.ionic.R
import com.enn.ionic.adapter.CompanyAdapter
import com.enn.ionic.bean.*
import com.enn.ionic.databinding.ActivitySwitchCompanyBinding
import com.enn.ionic.utils.launchWithLoadingNoBase
import com.enn.ionic.utils.toCheckFinish
import com.enn.ionic.vm.SwitchCompanyViewModel
import com.enn.network.toast
import com.youth.banner.util.LogUtils


class SwitchCompanyActivity : BaseActivity(R.layout.activity_switch_company) {

    private var nowindex: Int = 0
    val mBinding by binding<ActivitySwitchCompanyBinding>()
    private val mViewModel by viewModels<SwitchCompanyViewModel>()
    private lateinit var companyAdapter: CompanyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarCompat.themeAndColorStatusBar(this)
        initView()
    }

    private fun initView() {
        mBinding.switchCompanyBack.setOnClickListener { finish() }
        mBinding.switchCompanyBtn.setOnClickListener { startActivity<CompanyBindActivity>() }
        FlowEventBus.observe<Event.AfterLoginOrOut>(this) { it ->
            when (it.loginstate) {
                0 -> {
//                    登录
                    LogUtils.e("切换公司状态刷新")
//            发送跳转首页
                    FlowEventBus.post(Event.ToOtherPage(0))
                    finish()
                }
                else -> {}
            }

        }
        queryCompanys()
    }


    private fun queryCompanys() {
        launchWithLoadingNoBase({
            mViewModel.queryCompanys(
                getValue("token", ""),
                ToSwitchCompanyBean(userName = getValue("username", ""))
            )
        }) {
            onSuccess = {
                when (it?.body?.resultCode) {
                    Result_Code_Sucess -> {
                        it?.body?.data?.let { it1 ->
                            initCompanys(it1)
                        }
                    }
                    else -> {
                        toast("查询公司:${it?.body?.resultDesc}")

                    }
                }

            }
        }
    }

    private fun initCompanys(companys: List<QueryCompanyBackBean>) {

        companys.forEachIndexed { index, queryCompanyBackBean ->
            if (queryCompanyBackBean.isCurrent == 1) {
                mBinding.switchCompanyNowcompany.text = queryCompanyBackBean.name
                nowindex = index
                return@forEachIndexed
            }
        }
        companyAdapter = CompanyAdapter(R.layout.item_switch_company, companys)
        mBinding.switchCompanyRecy.adapter = companyAdapter.apply {
            setOnItemClickListener { adapter, view, position ->
//               去切换
                changeCompany((adapter.data[position] as QueryCompanyBackBean).bp, position)
            }


        }
    }

    private fun changeCompany(bp: String, position: Int) {
        launchWithLoadingNoBase({
            mViewModel.changeCompany(
                getValue("token", ""),
                ToSwitchCompanyBean(bp, userName = getValue("username", ""))
            )
        }) {
            onSuccess = {
                if (TextUtils.equals(ConstantObject.Result_Code_Sucess, it?.body?.resultCode)) {

                    companyAdapter.data[nowindex].isCurrent = 0
                    companyAdapter.data[position].isCurrent = 1
                    companyAdapter.notifyItemChanged(nowindex)
                    companyAdapter.notifyItemChanged(position)
                    mBinding.switchCompanyNowcompany.text = companyAdapter.data[position].name
                    nowindex = position
                    toast("切换成功")
//
                    mViewModel.toRefreshInfo(1000)
                } else {
                    toast("切换公司失败:${it?.body?.resultDesc}")

                }
            }
        }
    }

}