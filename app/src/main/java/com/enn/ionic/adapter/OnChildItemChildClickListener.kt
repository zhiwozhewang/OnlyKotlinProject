package com.enn.ionic.adapter

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter

open interface OnChildItemChildClickListener<T> {
    fun onItemChildClick(
        adapter: BaseQuickAdapter<*, *>,
        view: View,
        parentPosition: Int,
        position: Int,
        item: T
    )
}
