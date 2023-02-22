package com.enn.ionic.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.enn.ionic.bean.FirstRecommendBean

class FirstRecommendAdapter(layoutResId: Int, data: MutableList<FirstRecommendBean>?) :
    BaseQuickAdapter<FirstRecommendBean, BaseViewHolder>(layoutResId, data) {


    override fun convert(holder: BaseViewHolder, item: FirstRecommendBean) {
        TODO("Not yet implemented")
    }


}