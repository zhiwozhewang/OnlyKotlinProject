package com.enn.ionic.utils

import android.text.Editable
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.addTextChangedListener

/**
 * AppCompatEditText 配置
 */
fun AppCompatEditText.seConfiguration(deleteview: ImageView, ishide: CheckBox? = null) {
    //      过滤空格        监听输入
    addTextChangedListener(FilterSpaceTextWatcher(this) { text: Editable? ->
        deleteview.visibility =
            if (text?.isEmpty() == true) View.GONE else View.VISIBLE
        ishide?.visibility =
            if (text?.isEmpty() == true) View.GONE else View.VISIBLE
    })
    //        焦点状态
    onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
        if (!hasFocus) {
            // 此处为失去焦点时的处理内容
            deleteview.visibility = View.GONE
        }
    }
    //        清空内容
    deleteview.setOnClickListener {
        text = null
        it.visibility = View.GONE
    }
//    可见不可见
    ishide?.setOnCheckedChangeListener { compoundButton, b ->
        transformationMethod = if (b) {
            //                密码可见
            HideReturnsTransformationMethod.getInstance()
        } else
        //                密码不可见
            PasswordTransformationMethod.getInstance()
    }
}