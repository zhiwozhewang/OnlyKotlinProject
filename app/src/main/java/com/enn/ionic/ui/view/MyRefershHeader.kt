package com.enn.ionic.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import com.enn.ionic.R
import com.enn.ionic.databinding.LayoutRefershHeaderBinding
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshKernel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.constant.SpinnerStyle


class MyRefershHeader @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), RefreshHeader {

    var layoutMineItemBinding: LayoutRefershHeaderBinding
    var myanimation0: Animation
    var myanimation1: Animation
    var myanimation2: Animation


    init {
        //通过布局解释器获取布局
        LayoutInflater.from(context).inflate(R.layout.layout_refersh_header, this)
        layoutMineItemBinding = LayoutRefershHeaderBinding.bind(this)
        myanimation0 = AnimationUtils.loadAnimation(context, R.anim.anim_refersh_header0)
        myanimation1 = AnimationUtils.loadAnimation(context, R.anim.anim_refersh_header0)
        myanimation2 = AnimationUtils.loadAnimation(context, R.anim.anim_refersh_header0)

    }

    override fun onStateChanged(
        refreshLayout: RefreshLayout,
        oldState: RefreshState,
        newState: RefreshState
    ) {
    }

    override fun getView(): View {
        return this
    }

    override fun getSpinnerStyle(): SpinnerStyle {
        return SpinnerStyle.Translate//指定为平移，不能null
    }

    override fun setPrimaryColors(vararg colors: Int) {
    }

    override fun onInitialized(kernel: RefreshKernel, height: Int, maxDragHeight: Int) {
    }

    override fun onMoving(
        isDragging: Boolean,
        percent: Float,
        offset: Int,
        height: Int,
        maxDragHeight: Int
    ) {
    }

    override fun onReleased(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {
    }

    override fun onStartAnimator(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {
        startAnim();//开始动画
    }

    private fun startAnim() {

        layoutMineItemBinding.refreshHeadPoint0.startAnimation(myanimation0)
        layoutMineItemBinding.refreshHeadPoint1.startAnimation(myanimation1.also {
            it.startOffset = 300
        })
        layoutMineItemBinding.refreshHeadPoint2.startAnimation(myanimation2.also {
            it.startOffset = 600
        })
    }

    override fun onFinish(refreshLayout: RefreshLayout, success: Boolean): Int {
        myanimation0.cancel()//停止动画
        myanimation1.cancel()
        myanimation2.cancel()
        if (success) {
//            mHeaderText.setText("刷新完成");
        } else {
//            mHeaderText.setText("刷新失败");
        }
        return 500;//延迟500毫秒之后再弹回
    }

    override fun onHorizontalDrag(percentX: Float, offsetX: Int, offsetMax: Int) {
    }

    override fun isSupportHorizontalDrag(): Boolean {
        return false
    }
}