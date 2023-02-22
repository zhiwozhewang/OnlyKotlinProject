package com.enn.ionic.ui.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.dylanc.viewbinding.binding
import com.enn.base.base.BaseActivity
import com.enn.base.data.getValue
import com.enn.base.util.*
import com.enn.base.util.statusbar.StatusBarCompat
import com.enn.ionic.ConstantObject
import com.enn.ionic.R
import com.enn.ionic.adapter.TransferAdapter
import com.enn.ionic.bean.*
import com.enn.ionic.databinding.ActivityTransferBinding
import com.enn.ionic.utils.MyDialogUtils
import com.enn.ionic.utils.launchWithLoadingNoBase
import com.enn.ionic.utils.seConfiguration
import com.enn.ionic.vm.AccountViewModel
import com.enn.network.toast
import kotlinx.coroutines.launch


class AccountTransferActivity : BaseActivity(R.layout.activity_transfer) {

    val mBinding by binding<ActivityTransferBinding>()
    private val mViewModel by viewModels<AccountViewModel>()
    private lateinit var accountBean: AccountBean
    private lateinit var transferAdapter: TransferAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarCompat.themeAndColorStatusBar(
            this,
            "#F7FAFF",
        )
        initView()
    }

    private fun initView() {
        accountBean = intent.getSerializableExtra("bean") as AccountBean
        mBinding.transferBack.setOnClickListener { finish() }
        mBinding.transferEdit.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                queryAccount(textView.text.toString())
            }
            true
        }
        mBinding.transferEdit.seConfiguration( mBinding.transferEditDelete)
      /*  //        监听输入
        mBinding.transferEdit.addTextChangedListener { text: Editable? ->
            mBinding.transferEditDelete.visibility =
                if (text?.isEmpty() == true) View.GONE else View.VISIBLE
        }
        //        焦点状态
        mBinding.transferEdit.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                // 此处为失去焦点时的处理内容
                mBinding.transferEditDelete.visibility = View.GONE
            }
        }
        mBinding.transferEditDelete.setOnClickListener { mBinding.transferEdit.text = null }*/
        queryAccount("")
    }


    private fun queryAccount(toString: String) {
        launchWithLoadingNoBase({
            mViewModel.queryAccount(
                getValue("token", ""),
                icLoginDto = ToQueryAccountBean(toString)
            )
        }) {
            onSuccess = {
                if (TextUtils.equals(ConstantObject.Result_Code_Sucess, it?.body?.resultCode)) {
                    it?.body?.data?.let { it1 ->
                        initList(it1)
                    }
                } else {
                    toast("查询子账号:${it?.body?.resultDesc}")

                }
            }
        }
    }

    private fun initList(bean: TransferBean) {
        if (bean.list.isNullOrEmpty()) {
            //展示空页面
            mBinding.transferNoGroup.visibility = View.VISIBLE
            mBinding.transferRecy.visibility = View.GONE
            return
        }
        mBinding.transferNoGroup.visibility = View.GONE
        mBinding.transferRecy.visibility = View.VISIBLE
        transferAdapter = TransferAdapter(R.layout.item_transfer_account, bean.list)
        mBinding.transferRecy.adapter = transferAdapter.apply {
            setOnItemClickListener { adapter, view, position ->
//               去转让
                MyDialogUtils.showTransferDialog((adapter.data[position] as TransferListBean).nickName) {
                    transferAccount((adapter.data[position] as TransferListBean).id)
                }
            }


        }


    }

    private fun transferAccount(toid: Int) {
        launchWithLoadingNoBase({
            mViewModel.transferAccount(
                getValue("token", ""),
                getValue("transferid", 0),
                toid
            )
        }) {
            onSuccess = {
                if (TextUtils.equals(ConstantObject.Result_Code_Sucess, it?.body?.resultCode)) {
//                        FlowEventBus.post(Event.AfterTransfer(true))
                    lifecycleScope.launch {
                        mViewModel.toRefreshInfo()
                        finish()
                    }
                } else {
                    toast("转移失败:${it?.body?.resultDesc}")

                }
            }
        }
    }


}