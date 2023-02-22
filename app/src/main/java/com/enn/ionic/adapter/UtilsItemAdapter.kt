package com.enn.ionic.adapter

import android.widget.ImageView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.enn.ionic.R
import com.enn.ionic.bean.HqwModelVO

class UtilsItemAdapter(layoutResId: Int, data: List<HqwModelVO>?) :
    BaseQuickAdapter<HqwModelVO, BaseViewHolder>(layoutResId, ArrayList(data)) {

    init {
        addChildClickViewIds(
            R.id.item_utils_recy_im_right_layer,
            R.id.item_utils_recy_li
        )
    }

    override fun convert(holder: BaseViewHolder, item: HqwModelVO) {
        holder.setText(R.id.item_utils_recy_text, item.modelName)
        holder.setVisible(R.id.item_utils_recy_im_right_layer, item.isEdit)
//   coil使用     https://blog.csdn.net/vitaviva/article/details/113064062
        holder.getView<ImageView>(R.id.item_utils_recy_im).load(item.iconUrl2) {
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