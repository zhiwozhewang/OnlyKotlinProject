package com.enn.ionic.ui.activity

import android.R.attr.banner
import android.media.AudioTrack
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.dylanc.viewbinding.binding
import com.enn.base.base.BaseActivity
import com.enn.base.data.getValue
import com.enn.base.eventbus.Event
import com.enn.base.eventbus.FlowEventBus
import com.enn.base.ktx.startActivity
import com.enn.base.util.*
import com.enn.base.util.statusbar.StatusBarCompat
import com.enn.ionic.ConstantObject
import com.enn.ionic.ConstantObject.Companion.Result_Code_Sucess
import com.enn.ionic.R
import com.enn.ionic.bean.AccountBean
import com.enn.ionic.bean.DataBean
import com.enn.ionic.bean.ToUpdateInfoBean
import com.enn.ionic.databinding.ActivityBindCompanyBinding
import com.enn.ionic.databinding.ActivityMyaccountBinding
import com.enn.ionic.databinding.ActivityMyinfoBinding
import com.enn.ionic.utils.MyDialogUtils
import com.enn.ionic.utils.launchWithLoadingNoBase
import com.enn.ionic.utils.toCheckFinish
import com.enn.ionic.vm.AccountViewModel
import com.enn.ionic.vm.LoginViewModel
import com.enn.network.toast
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import com.youth.banner.indicator.CircleIndicator
import kotlinx.coroutines.launch


class MyAccountActivity : BaseActivity(R.layout.activity_myaccount), View.OnClickListener {

    val mBinding by binding<ActivityMyaccountBinding>()
    private val mViewModel by viewModels<AccountViewModel>()
    private lateinit var accountBean: AccountBean

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarCompat.themeAndColorStatusBar(this)
        toCheckFinish()
        initView()
    }

    override fun onResume() {
        super.onResume()
//        刷新数据
        getAccount()
    }

    private fun initView() {
        initListener()
    }

    private fun initListener() {
        mBinding.accountLostpw.setOnClickListener(this)
        mBinding.accountTransfer.setOnClickListener(this)
        mBinding.accountBack.setOnClickListener { finish() }
        mBinding.accountBtn.setOnClickListener(this)
        mBinding.accountCheck.setOnClickListener(this)
        mBinding.accountUnbind.setOnClickListener(this)
    }

    private fun getAccount() {

        launchWithLoadingNoBase({
            mViewModel.getAccountInfo(getValue("token", ""))
        }) {
            onSuccess = {
                if (TextUtils.equals(ConstantObject.Result_Code_Sucess, it?.body?.resultCode)) {
                    it?.body?.data?.let { it1 ->
                        initAccount(it1)
                    }
                } else {
                    toast("获取账号相关失败:${it?.body?.resultDesc}")

                }
            }
        }
    }

    private fun initAccount(bean: AccountBean) {
        accountBean = bean
        when (bean.isMaster) {
            0 -> {
//                非主账号
                mBinding.accountCheck.visibility = View.GONE
                mBinding.accountTransfer.visibility = View.GONE
                mBinding.accountUnbind.visibility = View.VISIBLE

            }
            1 -> {
//                主账号
                when (bean.passNumOfOrdinary > 0) {
                    true -> {
//                       有子账号
                        mBinding.accountCheck.visibility = View.VISIBLE
                        initCheckNum(accountBean.waitingPassNumOfOrdinary)
                        mBinding.accountTransfer.visibility = View.VISIBLE
                        mBinding.accountUnbind.visibility = View.GONE
                    }
                    else -> {
                        mBinding.accountCheck.visibility = View.VISIBLE
                        initCheckNum(accountBean.waitingPassNumOfOrdinary)
                        mBinding.accountTransfer.visibility = View.GONE
                        mBinding.accountUnbind.visibility = View.VISIBLE
                    }
                }

            }
        }


    }

    private fun initCheckNum(waitingPassNumOfOrdinary: Int) {
        mBinding.accountCheck.setNumText(waitingPassNumOfOrdinary)
        // TODO: onresume中刷新了  弃用eventbus
//        FlowEventBus.observe<Event.AfterTransfer>(this) { it ->
//            if (it.isdone) {
////                刷新
//                getAccount()
//            }
//        }
    }

    override fun onClick(p0: View?) {

        when (p0?.id) {
            R.id.account_btn -> {
                startActivity<CompanyBindActivity>()
            }
            R.id.account_lostpw -> {
                startActivity<FindPwActivity>()
            }
            R.id.account_check -> {
                startActivity<AccountCheckActivity>()
            }
            R.id.account_transfer -> {

                when (accountBean.waitingPassNumOfOrdinary > 0) {
                    true -> {
                        MyDialogUtils.showTransferCheckDialog {
                            startActivity<AccountCheckActivity>()
                        }
                    }
                    else -> {
                        startActivity<AccountTransferActivity>(Bundle().also {
                            it.putSerializable(
                                "bean",
                                accountBean
                            )
                        })
                    }
                }
            }
            R.id.account_unbind -> {
//                解除绑定
                when (accountBean.waitingPassNumOfOrdinary > 0) {
                    true -> {
//                        有待审核
                        MyDialogUtils.showUnbingCheckDialog({
//                            去审核
                            startActivity<AccountTransferActivity>(Bundle().also {
                                it.putSerializable(
                                    "bean",
                                    accountBean
                                )
                            })
                        }, false)
                    }
                    else -> {
//                        无待审核
                        MyDialogUtils.showUnbingCheckDialog({
                            //                        去解绑
                            toUnBind()
                        }, true, getValue("username", ""))
                    }
                }
            }

            else -> {}
        }
    }

    private fun toUnBind() {

        launchWithLoadingNoBase({
            mViewModel.toUnBind(getValue("token", ""), getValue("bp", ""), getValue("username", ""))
        }) {
            onSuccess = {
                when (it?.body?.resultCode) {
                    Result_Code_Sucess -> {
                        launchWithLoadingNoBase({
                            mViewModel.toRefreshInfo()
                        }) {
                            //            发送跳转首页
                            FlowEventBus.post(Event.ToOtherPage(0))
                            finish()
                        }
                    }
                    else -> {
                        toast("解绑失败:${it?.body?.resultDesc}")
                    }
                }
            }
        }
    }

}