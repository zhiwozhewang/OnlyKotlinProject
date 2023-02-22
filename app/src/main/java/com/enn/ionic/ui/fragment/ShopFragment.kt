package com.enn.ionic.ui.fragment

import android.os.Bundle
import android.view.View
import com.dylanc.viewbinding.binding
import com.enn.base.base.BaseFragment
import com.enn.base.data.getValue
import com.enn.base.util.statusbar.StatusBarCompat
import com.enn.ionic.ConstantObject
import com.enn.ionic.R
import com.enn.ionic.databinding.ActivityWebNewBinding
import com.enn.ionic.databinding.FragmentMarketBinding

class ShopFragment : BaseFragment(R.layout.fragment_market) {


    val binding by binding<FragmentMarketBinding>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        js调用Android 测试
//        binding.webWrapper.loadUrl("file:///android_asset/javascript.html");
        binding.webMarket.loadUrl(ConstantObject.MARKET_URL, activity?.getValue("token", ""))
    }

    override fun onResume() {
        super.onResume()
        StatusBarCompat.nosteepStatusBarText(activity, statusColor = "#FFFFFFFF")

    }
}