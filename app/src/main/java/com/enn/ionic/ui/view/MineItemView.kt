package com.enn.ionic.ui.view

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.enn.ionic.R
import com.enn.ionic.databinding.LayoutMineItemBinding

class MineItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    var layoutMineItemBinding: LayoutMineItemBinding

    init {
        //通过布局解释器获取布局
        LayoutInflater.from(context).inflate(R.layout.layout_mine_item, this)
        layoutMineItemBinding =
            LayoutMineItemBinding.bind(this)
        //通过attrs设置相关属性
        setStyle(context, attrs)

    }


    private fun setStyle(
        context: Context,
        attrs: AttributeSet?
    ) {
        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.ItemMine)
            //            获取text
            val text = array.getString(R.styleable.ItemMine_itemize_text)
            layoutMineItemBinding.itemMineText.text = text
            //            右侧图片是否显示
            val lDrawable_isshow =
                array.getBoolean(R.styleable.ItemMine_itemize_leftImage_isshow, true)
            layoutMineItemBinding.itemMineIm.visibility = if (lDrawable_isshow) VISIBLE else GONE
            //            获取左侧图片
            val lDrawable = array.getDrawable(R.styleable.ItemMine_itemize_leftImage)
            if (lDrawable_isshow && lDrawable != null) {
                layoutMineItemBinding.itemMineIm.setImageDrawable(lDrawable)
            }
            //            右侧数字文本是否显示
            val rnum_isshow = array.getBoolean(R.styleable.ItemMine_itemize_rightNum_isshow, false)
            layoutMineItemBinding.itemMineTextNum.visibility = if (rnum_isshow) VISIBLE else GONE
            //  右侧数字文本
            val right_num = array.getString(R.styleable.ItemMine_itemize_rightNum)
            if (rnum_isshow && right_num != null) {
                layoutMineItemBinding.itemMineTextNum.text = right_num
            }
            //            右侧图片是否显示
            val rDrawable_isshow =
                array.getBoolean(R.styleable.ItemMine_itemize_rightImage_isshow, true)
            layoutMineItemBinding.itemMineImTo.visibility = if (rDrawable_isshow) VISIBLE else GONE
            //            获取右侧图片
            val rDrawable = array.getDrawable(R.styleable.ItemMine_itemize_rightImage)
            if (rDrawable_isshow && rDrawable != null) {
                layoutMineItemBinding.itemMineImTo.setImageDrawable(rDrawable)
            }
            //            获取右侧文字
            val right_text = array.getString(R.styleable.ItemMine_itemize_rightText)
            if (right_text != null) {
                layoutMineItemBinding.itemMineTextRight.text = right_text
            }
            //            右侧文字颜色
            val right_text_color =
                array.getColor(R.styleable.ItemMine_itemize_rightText_color, Color.BLUE)
            if (right_text != null) {
                layoutMineItemBinding.itemMineTextRight.setTextColor(right_text_color)
            }
            //            右侧文字大小
            val right_text_size =
                array.getDimension(
                    R.styleable.ItemMine_itemize_rightText_size,
                    0f
                )
            if (right_text != null) {
                layoutMineItemBinding.itemMineTextRight.setTextSize(right_text_size)
            }
            //            获取背景图片
            val bgDrawable = array.getDrawable(R.styleable.ItemMine_itemize_background)
            if (bgDrawable != null) {
                layoutMineItemBinding.root.background = bgDrawable
            }
            //            下划线是否显示
            val line_isshow =
                array.getBoolean(R.styleable.ItemMine_itemize_line_isshow, false)
            layoutMineItemBinding.itemMineLine.visibility = if (line_isshow) VISIBLE else GONE

            array.recycle()
        }

    }

    //   通过资源id设置左侧图片样式
    fun setLeftImageResource(resId: Int) {
        layoutMineItemBinding.itemMineIm.setImageResource(resId)
    }

    //   右侧布局监听
    fun setRightLayoutClickListener(listener: OnClickListener) {
        layoutMineItemBinding.itemMineImTo.setOnClickListener(listener)
    }

    //   通过资源id设置右侧图片样式
    fun setRightImageResource(resId: Int) {
        layoutMineItemBinding.itemMineImTo.setImageResource(resId)
    }

    //  设置右侧图片是否展示
    fun setRightImageIsShow(isshow: Boolean) {
        layoutMineItemBinding.itemMineImTo.visibility = if (isshow) VISIBLE else GONE
    }

    //  设置右侧文字文本  与颜色
    fun setRightText(text: String, color: Int = R.color.color_0473FF) {
        layoutMineItemBinding.itemMineTextRight.text = text
        layoutMineItemBinding.itemMineTextRight.setTextColor(resources.getColor(color))
    }

    //  设置右侧文字文本  与颜色
    fun setRightText(text: String) {
        layoutMineItemBinding.itemMineTextRight.text = text
    }

    //    设置text
    fun setText(text: String) {
        layoutMineItemBinding.itemMineText.text = text
    }

    //    设置右侧数字显示
    fun setNumText(num: Int) {
        when (num) {
            0 -> {
                layoutMineItemBinding.itemMineTextNum.visibility = GONE
                layoutMineItemBinding.itemMineTextNum.text = num.toString()
            }
            else -> {
                layoutMineItemBinding.itemMineTextNum.visibility = VISIBLE
                layoutMineItemBinding.itemMineTextNum.text = num.toString()
            }
        }

    }

//    //    设置跟布局背景颜色
//    override fun setBackgroundColor(color: Int) {
//        titlebar_root!!.setBackgroundColor(color)
//    }
}