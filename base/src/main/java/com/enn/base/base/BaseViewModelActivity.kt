package com.enn.base.base

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.annotation.LayoutRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.enn.base.R
import java.lang.reflect.ParameterizedType

abstract class BaseViewModelActivity<VM : BaseViewModel, VB : ViewBinding>(@LayoutRes val layoutResId: Int) :
    BaseActivity(layoutResId) {

    protected lateinit var viewModel: VM
    protected lateinit var viewBinding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = createViewModel()
        viewBinding = createViewBinding()
        initStateBar();
        initData()
    }


    private fun initStateBar() {
        TODO("Not yet implemented")
    }

    protected abstract fun initData()

    private inline fun  ComponentActivity.createViewModel(): VM {
        val vmClass =
            (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments.filterIsInstance<Class<*>>()
        val viewModel = vmClass[1] as Class<VM>
        return ViewModelProvider(this).get(viewModel)
    }

    private inline fun createViewBinding(): VB {
        val clazz =
            (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<VB>
        val method = clazz.getMethod("inflate", LayoutInflater::class.java)
        return method.invoke(null, layoutInflater) as VB
    }

}