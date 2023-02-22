package com.enn.ionic.ui.activity

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.dylanc.viewbinding.binding
import com.enn.base.base.BaseActivity
import com.enn.base.base.BaseApp
import com.enn.base.data.getValue
import com.enn.base.data.putValue
import com.enn.base.eventbus.Event
import com.enn.base.eventbus.FlowEventBus
import com.enn.base.util.statusbar.StatusBarCompat
import com.enn.ionic.ConstantObject
import com.enn.ionic.ConstantObject.Companion.USER_BIND_STATUS_LOGIN_BIND
import com.enn.ionic.R
import com.enn.ionic.bean.BindCompanyQuryBean
import com.enn.ionic.bean.ToBindCustomerBean
import com.enn.ionic.databinding.ActivityBindCompanyResultBinding
import com.enn.ionic.databinding.DialogCompanyBindResultBinding
import com.enn.ionic.utils.MyDialogUtils
import com.enn.ionic.utils.launchWithLoadingNoBase
import com.enn.ionic.vm.CompanyBindViewModel
import com.enn.network.toast
import com.kongzue.dialogx.dialogs.CustomDialog
import com.kongzue.dialogx.interfaces.OnBindView
import kotlinx.coroutines.launch


class CompanyBindResultActivity : BaseActivity(R.layout.activity_bind_company_result) {

    val mBinding by binding<ActivityBindCompanyResultBinding>()
    private val mViewModel by viewModels<CompanyBindViewModel>()
    private lateinit var bindCompanyQuryBean: BindCompanyQuryBean
    private lateinit var code: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarCompat.steepStatusBarText(this, false)
        initView()
    }

    private fun initView() {
        code = intent.getStringExtra("code")?.also { toQuery(it) }.toString()
        mBinding.bindCompanyResultEdit.setText(code)
        mBinding.bindCompanyResultDelete.setOnClickListener {
            mBinding.bindCompanyResultEdit.text = null
        }
        mBinding.bindCompanyResultSearch.setOnClickListener {
            toQuery(mBinding.bindCompanyResultEdit.text.toString())
        }
        mBinding.bindCompanyResultBack.setOnClickListener { finish() }
        mBinding.bindCompanyResultBtn1.setOnClickListener { finish() }
        mBinding.bindCompanyResultBtn2.setOnClickListener {
            toBindCustomer(
                getValue("token", ""),
                ToBindCustomerBean(
                    bindCompanyQuryBean.companyCode,
                    bindCompanyQuryBean.customerAddress,
                    bindCompanyQuryBean.customerBp,
                    code,
                    getValue("username", ""),
                    "app"
                )
            )
        }
        mBinding.bindCompanyResultEdit.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                toQuery(textView.text.toString())
            }
            true
        }

    }

    private fun toQuery(code: String) {
        if (TextUtils.isEmpty(code)) {
            toast("输入不能为空")
            return
        }

        toQueryCustomerInfo(getValue("token", ""), code)
    }

    private fun toQueryCustomerInfo(token: String, code: String) {
        launchWithLoadingNoBase({

            mViewModel.toQueryCustomerInfo(token, code)

        }) {
            onSuccess = {

                when (it?.body?.resultCode) {
                    ConstantObject.Result_Code_Sucess -> {
//
                        initResultView(true)
                        it.body.data?.get(0).apply {
                            mBinding.bindCompanyResultHasIc.bindCompanyResultItemText1r.text =
                                getValue("phone", ConstantObject.PHONE)
                            mBinding.bindCompanyResultHasIc.bindCompanyResultItemText2r.text = city
                            mBinding.bindCompanyResultHasIc.bindCompanyResultItemText3r.text =
                                customerName
                            mBinding.bindCompanyResultHasIc.bindCompanyResultItemText4r.text =
                                customerAddress
                            mBinding.bindCompanyResultHasIc.bindCompanyResultItemText5r.text =
                                dutyParagraph
                            bindCompanyQuryBean = this
                        }

                    }
                    else -> {
                        initResultView(false)
//                        toast("查询失败:${it?.body?.resultDesc}")
                    }
                }
            }
        }


    }

    private fun initResultView(hasdata: Boolean) {
        if (hasdata) {
            mBinding.bindCompanyResultGroup.visibility = View.GONE
            mBinding.bindCompanyResultHasIc.bindCompanyResultHas.visibility = View.VISIBLE
            mBinding.bindCompanyResultNoGroup.visibility = View.GONE
            mBinding.bindCompanyResultBtnGroup.visibility = View.VISIBLE

        } else {
            mBinding.bindCompanyResultGroup.visibility = View.VISIBLE
            mBinding.bindCompanyResultHasIc.bindCompanyResultHas.visibility = View.GONE
            mBinding.bindCompanyResultNoGroup.visibility = View.VISIBLE
            mBinding.bindCompanyResultBtnGroup.visibility = View.GONE

        }
    }

    private fun toBindCustomer(token: String, bindCustomerDto: ToBindCustomerBean) {
        launchWithLoadingNoBase({

            mViewModel.toBindCustomer(token, bindCustomerDto)

        }) {
            onSuccess = {

                when (it?.body?.resultCode) {
                    ConstantObject.Result_Code_Sucess -> {
//
                        when (it?.body?.data.isMaster) {
                            "0" -> {
//                                非主账号
                                MyDialogUtils.showBindDialog {

                                    launchWithLoadingNoBase({
                                        mViewModel.toRefreshInfo()
                                    }) {
                                        //            发送跳转首页
                                        FlowEventBus.post(Event.ToOtherPage(0))
                                        finish()
                                    }

                                }
                            }
                            else -> {
                                launchWithLoadingNoBase({
                                    mViewModel.toRefreshInfo()
                                }) {
                                    //            发送跳转首页
                                    FlowEventBus.post(Event.ToOtherPage(0))
                                    toast("绑定成功")
                                    finish()
                                }
                            }
                        }
                    }
                    else -> {
                        toast("绑定失败:${it?.body?.resultDesc}")
                    }
                }
            }
        }


    }


}