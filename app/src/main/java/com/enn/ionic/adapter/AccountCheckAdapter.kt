package com.enn.ionic.adapter

import android.widget.ImageView
import coil.load
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.enn.base.util.TimestampUtils
import com.enn.ionic.ConstantObject
import com.enn.ionic.R
import com.enn.ionic.bean.webbean.CheckListBean

class AccountCheckAdapter(layoutResId: Int, data: List<CheckListBean>?) :
    BaseQuickAdapter<CheckListBean, BaseViewHolder>(layoutResId, ArrayList(data)) {

    init {
        addChildClickViewIds(
            R.id.item_account_check_btn1,
            R.id.item_account_check_btn2,
            R.id.item_account_check_delete
        )
    }

    override fun convert(holder: BaseViewHolder, item: CheckListBean) {
        holder.setText(
            R.id.item_account_check_name,
            if (item.nickname.isNullOrEmpty()) ConstantObject.DEFAULT_NICK_NAME2 else item.nickname
        )
        holder.setText(R.id.item_account_check_phone, item.username)
        holder.setText(
            R.id.item_account_check_time,
            TimestampUtils.transToString(item.applyBindingTimestamp)
        )
        when (item.reviewStatus) {
            0 -> {
                //                审核未通过
                initGroup(holder, false)
                holder.setText(R.id.item_account_check_pass, "未通过")
                holder.setTextColorRes(R.id.item_account_check_pass, R.color.color_333333)
            }
            1 -> {
                //                审核通过
                initGroup(holder, false)
                holder.setText(R.id.item_account_check_pass, "已通过")
                holder.setTextColorRes(R.id.item_account_check_pass, R.color.color_333333)

            }
            2 -> {
                //                待审核
                initGroup(holder, true)
            }
            3 -> {
                //                超时
                initGroup(holder, false)
                holder.setText(R.id.item_account_check_pass, "超时未审核")
                holder.setTextColorRes(R.id.item_account_check_pass, R.color.FF9200)

            }
            else -> {}
        }
        //   coil使用     https://blog.csdn.net/vitaviva/article/details/113064062
        holder.getView<ImageView>(R.id.item_account_check_im).load(item.headImage) {
            transformations(CircleCropTransformation())
            error(R.mipmap.im_mine_head)
            placeholder(R.mipmap.im_mine_head)
        }


    }

    //istocheck  是否是待审核
    private fun initGroup(holder: BaseViewHolder, istocheck: Boolean) {
        holder.setVisible(R.id.item_account_check_pass, !istocheck)
        holder.setVisible(R.id.item_account_check_btngroup, istocheck)
    }


}