package com.enn.ionic.ui.activity

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import com.dylanc.viewbinding.binding
import com.enn.base.base.BaseActivity
import com.enn.base.util.launchWithLoadingAndCollect
import com.enn.base.util.statusbar.StatusBarCompat
import com.enn.ionic.R
import com.enn.ionic.bean.ToUpdateInfoBean
import com.enn.ionic.databinding.ActivityMyinfoChangeBinding
import com.enn.ionic.vm.InfoViewModel
import com.enn.network.toast


class MyInfoChangeActivity : BaseActivity(R.layout.activity_myinfo_change) {

    val mViewModel by viewModels<InfoViewModel>()
    val mBinding by binding<ActivityMyinfoChangeBinding>()
    lateinit var title: String
    lateinit var bean: ToUpdateInfoBean
    var maxlength: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarCompat.themeAndColorStatusBar(this)
        initView()
    }

    private fun initView() {
        StatusBarCompat.themeAndColorStatusBar(
            this,
            "#F7FAFF",
        )
        title = intent.getStringExtra("title").toString()
        intent.getSerializableExtra("bean")?.let {
            bean = it as ToUpdateInfoBean
        }
//
        mBinding.infoChangeBack.setOnClickListener { finish() }
        mBinding.infoChangeTitle.text = title
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mBinding.infoChangeEdit.focusable = View.FOCUSABLE
        }
        mBinding.infoChangeEdit.isFocusableInTouchMode = true
        mBinding.infoChangeEdit.requestFocus()

        when (title) {
            "修改用户名" -> {
                maxlength = 4
                mBinding.infoChangeEdit.filters = arrayOf<InputFilter>(object : LengthFilter(4) {})
            }
            "修改职务" -> {
                maxlength = 5
                mBinding.infoChangeEdit.filters = arrayOf<InputFilter>(object : LengthFilter(5) {})
            }
            else -> {}
        }
//        监听输入
        mBinding.infoChangeEdit.addTextChangedListener { text: Editable? ->


            mBinding.infoChangeDelete.visibility =
                if (text.isNullOrEmpty()) View.GONE else View.VISIBLE
            mBinding.infoChangeNum.visibility =
                if (text.isNullOrEmpty()) View.GONE else View.VISIBLE
            if (!text.isNullOrEmpty()) {
                mBinding.infoChangeNum.text = "${text.length}/$maxlength"
//                mBinding.infoChangeBtn2.setTextColor(R.color.ABD0FF)
            }
//            else {
//                mBinding.infoChangeBtn2.setText(R.color.color_0473FF)
//            }
        }
        mBinding.infoChangeDelete.setOnClickListener { mBinding.infoChangeEdit.text = null }
//        mBinding.infoChangeBtn1.setOnClickListener { finish() }
        mBinding.infoChangeBtn2.setOnClickListener {
            if (mBinding.infoChangeEdit.text.isNullOrEmpty()) {
                toast("请输入内容")
                return@setOnClickListener
            }
            when (title) {
                "修改用户名" -> {
                    updateInfo(bean.also { it.nickname = mBinding.infoChangeEdit.text.toString() })
                }
                "修改职务" -> {
                    updateInfo(bean.also { it.position = mBinding.infoChangeEdit.text.toString() })
                }
                else -> {}
            }
        }

    }

    /**
     *修改成功回传数据
     */
    private fun updateInfo(bean: ToUpdateInfoBean) {
        launchWithLoadingAndCollect({
            mViewModel.updateInfo(bean)
        }) {
            onSuccess = {
                if (it?.get(0) == true) {
//                    FlowEventBus.post(Event.SaveInfoBean(bean))
                    finish()
                }
            }
            onFailed = { _, errorMsg ->
                toast(errorMsg ?: "")
            }
        }
    }


}