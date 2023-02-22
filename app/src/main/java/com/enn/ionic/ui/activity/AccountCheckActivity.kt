package com.enn.ionic.ui.activity

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.activity.viewModels
import com.dylanc.viewbinding.binding
import com.enn.base.base.BaseActivity
import com.enn.base.data.getValue
import com.enn.base.util.*
import com.enn.base.util.statusbar.StatusBarCompat
import com.enn.ionic.ConstantObject
import com.enn.ionic.R
import com.enn.ionic.adapter.AccountCheckAdapter
import com.enn.ionic.bean.webbean.CheckListBean
import com.enn.ionic.databinding.ActivityAccountCheckBinding
import com.enn.ionic.utils.MyDialogUtils
import com.enn.ionic.utils.launchWithLoadingNoBase
import com.enn.ionic.vm.AccoutnCheckViewModel
import com.enn.network.toast


class AccountCheckActivity : BaseActivity(R.layout.activity_account_check) {

    val mBinding by binding<ActivityAccountCheckBinding>()
    private val mViewModel by viewModels<AccoutnCheckViewModel>()
    private lateinit var accountCheckAdapter: AccountCheckAdapter
    private var pagenum = 0
    private var pagesize = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarCompat.themeAndColorStatusBar(this)
        StatusBarCompat.themeAndColorStatusBar(
            this,
            "#F7FAFF",
        )
        initView()
    }

    private fun initView() {
        mBinding.accountCheckBack.setOnClickListener { finish() }
        queryCheckList()
    }


    private fun queryCheckList() {
        launchWithLoadingNoBase({
            mViewModel.getCheckList(
                getValue("token", ""),
                pagenum, pagesize
            )
        }) {
            onSuccess = {
                if (TextUtils.equals(ConstantObject.Result_Code_Sucess, it?.body?.resultCode)) {

                    if (it?.body?.data?.list.isNullOrEmpty()) {
                        mBinding.accountCheckNoGroup.visibility = View.VISIBLE
                        mBinding.accountCheckRecy.visibility = View.GONE
                    } else {
                        mBinding.accountCheckNoGroup.visibility = View.GONE
                        mBinding.accountCheckRecy.visibility = View.VISIBLE
                        it?.body?.data?.list?.let { it1 -> initAdapter(it1) }
                    }

                } else {
                    toast("查询审核列表:${it?.body?.resultDesc}")

                }
            }
        }
    }

    private fun initAdapter(checkListBeans: List<CheckListBean>) {

        if (mBinding.accountCheckRecy.adapter != null) {
            accountCheckAdapter.setList(checkListBeans)
            return
        }
        accountCheckAdapter = AccountCheckAdapter(R.layout.item_account_check, checkListBeans)
        mBinding.accountCheckRecy.adapter = accountCheckAdapter.apply {
            setOnItemChildClickListener { adapter, view, position ->
                val checkListBean = adapter.getItem(position) as CheckListBean
                when (view.id) {
                    R.id.item_account_check_btn1 -> {
//                        拒绝
                        toCheck(checkListBean, 0)
                    }
                    R.id.item_account_check_btn2 -> {
//                        通过
                        toCheck(checkListBean, 1)

                    }
                    R.id.item_account_check_delete -> {
//                        删除
                        mBinding.accountCheckRecy.closeMenu()
//                        mDeviceList.remove(position)
                        MyDialogUtils.showDeleteDialog {
                            deleteCheck(checkListBean) {
                                adapter.removeAt(position)
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun toCheck(checkListBean: CheckListBean, reviewStatus: Int) {
        launchWithLoadingNoBase({
            mViewModel.toCheck(
                getValue("token", ""),
                getValue("transferid", 0),
                checkListBean.id,
                reviewStatus
            )
        }) {
            onSuccess = {
                if (TextUtils.equals(ConstantObject.Result_Code_Sucess, it?.body?.resultCode)) {
                    //刷新列表
                    queryCheckList()
                } else {
                    toast("操作失败:${it?.body?.resultDesc}")

                }
            }
        }
    }

    private fun deleteCheck(checkListBean: CheckListBean, afterDelete: () -> Unit) {
        launchWithLoadingNoBase({
            mViewModel.deleteCheck(
                getValue("token", ""),
                checkListBean.bp  ,
//                getValue("bp", ""),
                checkListBean.username
            )
        }) {
            onSuccess = {
                if (TextUtils.equals(ConstantObject.Result_Code_Sucess, it?.body?.resultCode)) {
                    afterDelete()
                } else {
                    toast("删除失败:${it?.body?.resultDesc}")

                }
            }
        }
    }


}