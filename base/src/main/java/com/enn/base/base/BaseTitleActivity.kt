package com.enn.base.base

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import com.enn.base.R

abstract class BaseTitleActivity(@LayoutRes val layoutResId: Int) : BaseActivity(R.layout.activity_base) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LayoutInflater.from(this).inflate(layoutResId, findViewById<FrameLayout>(R.id.container))
        init()
    }

    protected abstract fun init()
}