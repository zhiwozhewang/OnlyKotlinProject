package com.enn.ionic.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.dylanc.viewbinding.binding
import com.enn.base.base.BaseFragment
import com.enn.base.data.getValue
import com.enn.base.eventbus.Event
import com.enn.base.eventbus.FlowEventBus
import com.enn.base.ktx.startActivity
import com.enn.base.util.launchAndCollect
import com.enn.base.util.launchWithLoadingAndCollect
import com.enn.ionic.ConstantObject
import com.enn.ionic.R
import com.enn.ionic.adapter.*
import com.enn.ionic.bean.ToUpdateInfoBean
import com.enn.ionic.databinding.FragmentHomeBinding
import com.enn.ionic.ui.activity.LoginActivity
import com.enn.ionic.ui.activity.QRCodeScanningActivity
import com.enn.ionic.vm.HomeViewModel
import com.enn.ionic.vm.InfoViewModel
import com.enn.network.toast
import com.google.android.material.tabs.TabLayoutMediator
import com.youth.banner.util.LogUtils

class HomeFragment : BaseFragment(R.layout.fragment_home) {

    private val binding: FragmentHomeBinding by binding()
    val mViewModel by viewModels<HomeViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //        登录悬浮框
        binding.homeLoginBtn.setOnClickListener { btn -> navigateToPlant(btn) }
        initLoginView(true)
        FlowEventBus.observe<Event.AfterLoginOrOut>(this) { it ->
            LogUtils.e("eventbus 登录状态刷新")
            initLoginView(false)
        }
        FlowEventBus.observe<Event.ToOtherPage>(this) { it ->
            LogUtils.e("跳转其他页面刷新")
            binding.viewPager.currentItem = it.index
        }
        //

        binding.viewPager.adapter = HomePagerAdapter(this)
        // Set the icon and text for each tab
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
//            自定义
            tab.setCustomView(R.layout.layout_tab)
            setIcon(tab.customView?.findViewById(R.id.tab_im), position)
            setTabText(tab.customView?.findViewById(R.id.tab_text), position)
//            tab.setIcon(getTabIcon(position))
//            tab.text = getTabTitle(position)
        }.attach()

    }

    /**
     *获取个人信息
     */
//    private fun getInfo() {
//        launchAndCollect({
//            mViewModel.getInfo(activity?.getValue("username", ""))
//        }) {
//            onSuccess = {
//                val toUpdateInfoBean = (it?.get(0) ?: null) as ToUpdateInfoBean
//                mViewModel.saveInfoData(toUpdateInfoBean)
//            }
//            onFailed = { _, errorMsg ->
//                toast(errorMsg ?: "")
//            }
//        }
//    }

    private fun initLoginView(isFirstInApp: Boolean) {

        when (activity?.getValue("usertype", ConstantObject.USER_TYPE0).toString()) {
            ConstantObject.USER_TYPE0 -> {
//                未登录
                binding.homeLoginGroup.visibility = View.VISIBLE
            }
            else -> {
                binding.homeLoginGroup.visibility = View.GONE
                if (isFirstInApp) {
                    mViewModel.toRefreshInfo()
                }
            }
        }
    }

    private fun setTabText(textView: TextView?, position: Int) {
        textView?.text = getTabTitle(position)
    }

    private fun setIcon(viewById: ImageView?, position: Int) {
        viewById?.setImageResource(getTabIcon(position))
    }

    private fun getTabIcon(position: Int): Int {
        return when (position) {
            FIRST_PAGE_INDEX -> R.drawable.selector_tab_first
            SHOP_PAGE_INDEX -> R.drawable.selector_tab_shop
            NEWS_PAGE_INDEX -> R.drawable.selector_tab_news
            MINE_PAGE_INDEX -> R.drawable.selector_tab_mine
            else -> throw IndexOutOfBoundsException()
        }
    }

    private fun getTabTitle(position: Int): String? {
        return when (position) {
            FIRST_PAGE_INDEX -> "首页"
            SHOP_PAGE_INDEX -> "集市"
            NEWS_PAGE_INDEX -> "头条"
            MINE_PAGE_INDEX -> "我的"
            else -> null
        }
    }

    private fun navigateToPlant(view: View) {
//        Navigation.findNavController(view).navigate(R.id.action_mainFragment_to_loginFragment)
        startActivity<LoginActivity>()
    }
}