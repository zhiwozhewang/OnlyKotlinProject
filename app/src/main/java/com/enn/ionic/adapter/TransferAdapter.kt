package com.enn.ionic.adapter

import android.widget.ImageView
import coil.load
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.enn.ionic.ConstantObject
import com.enn.ionic.R
import com.enn.ionic.bean.TransferListBean

class TransferAdapter(layoutResId: Int, data: List<TransferListBean>?) :
    BaseQuickAdapter<TransferListBean, BaseViewHolder>(layoutResId, ArrayList(data)) {


    override fun convert(holder: BaseViewHolder, item: TransferListBean) {
        holder.setText(
            R.id.item_transfer_name,
                    if (item.nickName.isNullOrEmpty()) ConstantObject.DEFAULT_NICK_NAME2 else item.nickName
        )
        holder.setText(R.id.item_transfer_phone, item.username)
//   coil使用     https://blog.csdn.net/vitaviva/article/details/113064062
        holder.getView<ImageView>(R.id.item_transfer_im).load(item.headImageUrl) {
            transformations(
                CircleCropTransformation()
            )
            error(R.mipmap.im_mine_head)
            placeholder(R.mipmap.im_mine_head)
        }


    }


}