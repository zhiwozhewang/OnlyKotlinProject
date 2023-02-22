package com.enn.ionic.adapter

import android.widget.ImageView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.enn.ionic.R
import com.enn.ionic.bean.QueryCompanyBackBean

class CompanyAdapter(layoutResId: Int, data: List<QueryCompanyBackBean>?) :
    BaseQuickAdapter<QueryCompanyBackBean, BaseViewHolder>(layoutResId, ArrayList(data)) {


    override fun convert(holder: BaseViewHolder, item: QueryCompanyBackBean) {
        holder.setText(R.id.item_switch_company_name, item.name)
        when (item.isCurrent) {
            1 -> {
                holder.setTextColorRes(R.id.item_switch_company_name, R.color.color_0473FF)
            }
            else -> {
                holder.setTextColorRes(R.id.item_switch_company_name, R.color.black)
            }
        }
        holder.setVisible(R.id.item_switch_company_im, item.isCurrent == 1)
//   coil使用     https://blog.csdn.net/vitaviva/article/details/113064062
//        holder.getView<ImageView>(R.id.item_transfer_im).load(item.iconUrl2) {
//            transformations(
//                RoundedCornersTransformation(
//                    topRight = 10f,
//                    topLeft = 10f,
//                    bottomLeft = 10f,
//                    bottomRight = 10f
//                )
//            )
//        }


    }


}