package com.enn.ionic.adapter

import android.widget.ImageView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.DraggableModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.enn.ionic.R
import com.enn.ionic.bean.HqwModelVO

class FirstGridAdapter(layoutResId: Int, data: MutableList<HqwModelVO>?) :
    BaseQuickAdapter<HqwModelVO, BaseViewHolder>(layoutResId, data), DraggableModule {


    override fun convert(holder: BaseViewHolder, item: HqwModelVO) {
        holder.setText(R.id.item_first_grid_text, item.modelName)
//   coil使用     https://blog.csdn.net/vitaviva/article/details/113064062
        holder.getView<ImageView>(R.id.item_first_grid_im).load(item.iconUrl2) {
            transformations(
                RoundedCornersTransformation(
                    topRight = 10f,
                    topLeft = 10f,
                    bottomLeft = 10f,
                    bottomRight = 10f
                )
            )
        }


    }


}