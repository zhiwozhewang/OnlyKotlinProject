package com.enn.ionic.adapter

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.enn.base.data.getValue
import com.enn.ionic.ConstantObject
import com.enn.ionic.R
import com.enn.ionic.bean.HqwModelVO
import com.enn.ionic.bean.UtilsBean
import com.enn.ionic.ui.activity.WebActivityNew

class UtilsAdapter(layoutResId: Int, data: MutableList<UtilsBean>?) :
    BaseQuickAdapter<UtilsBean, BaseViewHolder>(layoutResId, data) {
    private var mOnChildItemChildClickListener: OnChildItemChildClickListener<HqwModelVO>? = null


    override fun convert(holder: BaseViewHolder, item: UtilsBean) {

        holder.setText(R.id.item_utils_text, item.modelName)

        holder.getView<RecyclerView>(R.id.item_utils_recy).adapter =
            UtilsItemAdapter(R.layout.item_utils_recy, item.hqwModelVOList).apply {
                setOnItemChildClickListener { adapter, view, position ->
                    view.isClickable = false
                    //添加至头布局
                    mOnChildItemChildClickListener?.onItemChildClick(
                        this, view, 0, position,
                        adapter.getItem(position) as HqwModelVO
                    )
                    view.postDelayed({
                        view.isClickable = true
                    }, 1000)
                }

            }


    }


    fun setOnChildItemChildClickListener(listener: OnChildItemChildClickListener<HqwModelVO>) {
        this.mOnChildItemChildClickListener = listener
    }

    //    单个数据遍历
    fun toEditOne(modelId: Int, edit: Boolean) {

        loop2@ for (index in 0 until data.size) {
            loop3@ for (hqwModelVO in data[index].hqwModelVOList) {

                if (hqwModelVO.modelId == modelId) {
                    hqwModelVO.isEdit = true
//                    notifyItemChanged(index)
                    continue@loop2
                }
            }
        }
        notifyDataSetChanged()

    }

    //    全数据遍历
    fun toEditAll(innerBeans: MutableList<HqwModelVO>, edit: Boolean) {

        loop1@ for (index in 0 until data.size) {
            loop2@ for (hqwModelVO in data[index].hqwModelVOList) {

                if (!edit) {
                    hqwModelVO.isEdit = false
                    continue@loop2
                } else {
                    hqwModelVO.isEdit = true
                }
                loop3@ for (innerBean in innerBeans) {
                    if (hqwModelVO.modelId == innerBean.modelId) {
                        hqwModelVO.isEdit = false
                        continue@loop2
                    }

                }


            }
        }
        notifyDataSetChanged()


    }
}