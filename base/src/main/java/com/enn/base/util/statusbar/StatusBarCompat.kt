package com.enn.base.util.statusbar

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat


object StatusBarCompat {

    private val INVALID_VAL = -1
    private val COLOR_DEFAULT = Color.parseColor("#20000000")


    /**
     * 状态栏设置主题及染色
     * @param  statusColor 例子：#0473FF
     */
    fun themeAndColorStatusBar(activity: Activity?, statusColor: String, dark: Boolean = true) {
        themeAndColorStatusBar(activity, Color.parseColor(statusColor), dark)
    }

    /**
     * 设置状态栏白色背景黑色主题
     */
    fun themeAndColorStatusBar(activity: Activity?) {
        themeAndColorStatusBar(activity, INVALID_VAL, true)
    }

    /**
     * 状态栏设置主题及染色
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun themeAndColorStatusBar(activity: Activity?, statusColor: Int, dark: Boolean) {
        //      资源文件获取color           ContextCompat.getColor(activity,color )
        activity?.let {
            //设置主题
            setStatusBarTextDark(it, dark)
            //染色
            colorStatusBar(it, statusColor)
        }


    }

    /**
     *沉浸状态栏  并染色
     * @param dark 状态栏字体和图标是否为深色
     */
    fun steepStatusBarText(
        activity: Activity?, dark: Boolean = false, statusColor: String? = null
    ) {
        activity?.let {
            StepStatusBarUtils.setLightStatusBar(
                it,
                isMarginStatusBar = false,
                isMarginNavigationBar = false,
                isTransStatusBar = true,
                dark
            )
            //染色
            statusColor?.let { it1 -> colorStatusBar(it, Color.parseColor(it1)) }
        }

    }

    /**
     *取消沉浸状态栏  并染色
     * @param dark 状态栏字体和图标是否为深色
     */
    fun nosteepStatusBarText(
        activity: Activity?,
        dark: Boolean = true,
        statusColor: String? = null
    ) {
        activity?.let {
            StepStatusBarUtils.setLightStatusBar(
                it,
                isMarginStatusBar = false,
                isMarginNavigationBar = false,
                isTransStatusBar = false,
                dark
            )
            //染色
            statusColor?.let { it1 -> colorStatusBar(it, Color.parseColor(it1)) }
        }

    }

    private fun getStatusBarHeight(context: Context): Int {
        var result = 0
        val resourceId =
            context.getResources().getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId)
        }
        return result
    }

    /**
     *修改状态栏主题
     * @param dark 状态栏字体和图标是否为深色
     */
    private fun setStatusBarTextDark(activity: Activity, dark: Boolean) {

        // android6.0+系统
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            WindowInsetsControllerCompat(
                activity.window,
                activity.window.decorView
            ).let { controller ->
                controller.isAppearanceLightStatusBars = dark
            }
        }

    }

    /**
     * 染色
     */
    fun colorStatusBar(activity: Activity, statusColor: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            if (statusColor != INVALID_VAL) {
            activity.window.statusBarColor = statusColor
//            }
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            var color = COLOR_DEFAULT
            val contentView = activity.findViewById<View>(android.R.id.content) as ViewGroup
            if (statusColor != INVALID_VAL) {
                color = statusColor
            }
            val statusBarView = View(activity)
            val lp = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                getStatusBarHeight(activity)
            )
            statusBarView.setBackgroundColor(color)
            contentView.addView(statusBarView, lp)
        }
    }


    @Deprecated("修改状态栏主题", ReplaceWith("setStatusBarTextDark"))
    fun setStatusBarTextDarkOld(activity: Activity, dark: Boolean) {
        // android6.0+系统
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (dark) {
                activity.window.decorView.systemUiVisibility =
                    (View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
            } else {
                activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            }
        }
    }

    private fun hideSystemUI(activity: Activity) {
        WindowCompat.setDecorFitsSystemWindows(activity.window, false)
        WindowInsetsControllerCompat(activity.window, activity.window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun showSystemUI(activity: Activity) {
        WindowCompat.setDecorFitsSystemWindows(activity.window, true)
        WindowInsetsControllerCompat(activity.window, activity.window.decorView).show(
            WindowInsetsCompat.Type.systemBars()
        )
    }
}