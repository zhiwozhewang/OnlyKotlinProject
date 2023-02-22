package com.enn.ionic.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import com.dylanc.viewbinding.binding
import com.enn.base.base.BaseFragment
import com.enn.base.util.launchAndCollectIn
import com.enn.base.util.launchWithLoading
import com.enn.ionic.R
import com.enn.ionic.bean.DataBean
import com.enn.ionic.databinding.FragmentMainBinding
import com.enn.ionic.vm.ApiViewModel
import com.enn.network.toast

class MainFragment : BaseFragment(R.layout.fragment_main) {

    private val mViewModel by viewModels<ApiViewModel>()
    private val mBinding: FragmentMainBinding by binding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        view.findViewById<Button>(R.id.bt0).setOnClickListener {
//            view.findNavController().navigate(R.id.loginFragment)
//        }

        initData()
        initObserver()
    }

    private fun initObserver() {
        mViewModel.uiState.launchAndCollectIn(this, Lifecycle.State.STARTED) {
            onSuccess = { result: List<DataBean>? ->
                mBinding.tvTest.text = result.toString()
            }

            onComplete = { Log.i("MainFragment", ": onComplete") }

            onFailed = { _, msg -> toast(msg ?: "") }

            onError = { }
        }
    }


    private fun initData() {
        mBinding.bt1.setOnClickListener { requestNet() }

        mBinding.bt2.setOnClickListener {
            requestNetError()
        }

        mBinding.bt3.setOnClickListener {
            login()
        }
    }


    private fun requestNet() = launchWithLoading(mViewModel::requestNet)

    private fun requestNetError() = launchWithLoading(mViewModel::requestNetError)


    /**
     * 链式调用，返回结果的处理都在一起，viewmodel中不需要创建一个livedata对象
     * 适用于不需要监听数据变化的场景
     * 屏幕旋转，Activity销毁重建，数据会消失
     */
    private fun login() {
//        launchWithLoadingAndCollect({
//            mViewModel.login(ToLoginBean("张三","123456"))
//        }) {
//            onSuccess = {
//                mBinding.tvTest.text = it.toString()
//            }
//            onFailed = { errorCode, errorMsg ->
//                toast(errorMsg?:"") }
//            }
    }
}