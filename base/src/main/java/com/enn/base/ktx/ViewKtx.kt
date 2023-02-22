package com.enn.base.ktx

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.enn.base.R

fun View.clickWithLimit(intervalMill: Int = 500, block: ((v: View?) -> Unit)) {
    setOnClickListener(object : View.OnClickListener {
        var last = 0L
        override fun onClick(v: View?) {
            if (System.currentTimeMillis() - last > intervalMill) {
                block(v)
                last = System.currentTimeMillis()
            }
        }
    })
}

/**
 * 自定义圆角矩形
 */
fun View.setRoundRectBg(color: Int, cornerRadius: Int) {
    background = GradientDrawable().apply {
        setColor(color)
        setCornerRadius(cornerRadius.toFloat())
    }
}

fun View.setOnClickCallback(interval: Long = 500L, callback: (View) -> Unit) {
    if (!isClickable) isClickable = true
    if (!isFocusable) isFocusable = true
    setOnClickListener(object : View.OnClickListener {
        override fun onClick(v: View?) {
            v ?: return
            val lastClickedTimestamp = v.getTag(R.id.view_last_click_timestamp)?.toString()?.toLongOrNull() ?: 0L
            val currTimestamp = System.currentTimeMillis()
            if (currTimestamp - lastClickedTimestamp < interval) return
            v.setTag(R.id.view_last_click_timestamp, currTimestamp)
            callback(v)
        }
    })
}

fun ViewGroup.inflate(resId: Int): View {
    return LayoutInflater.from(context).inflate(resId, this, false)
}
