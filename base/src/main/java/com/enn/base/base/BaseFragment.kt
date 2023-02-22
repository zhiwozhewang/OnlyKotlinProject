package com.enn.base.base

import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.dylanc.viewbinding.binding
import com.enn.base.anno.FragmentConfiguration
import com.kongzue.dialogx.dialogs.WaitDialog

abstract class BaseFragment<VB:ViewBinding>(@LayoutRes contentLayoutId: Int,) : Fragment(contentLayoutId), IUiView {

    private var useEventBus = false

    init {
        this.javaClass.getAnnotation(FragmentConfiguration::class.java)?.let {
            useEventBus = it.useEventBus
        }
    }


    override fun showLoading() {
        WaitDialog.show("加载中...");
    }

    override fun dismissLoading() {
        WaitDialog.dismiss();
    }

}