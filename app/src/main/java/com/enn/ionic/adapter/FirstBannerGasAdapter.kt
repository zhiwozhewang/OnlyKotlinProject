package com.enn.ionic.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Scale
import coil.transform.RoundedCornersTransformation
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.enn.ionic.ConstantObject
import com.enn.ionic.R
import com.enn.ionic.bean.FirstBannerBean
import com.enn.ionic.bean.GasItem
import com.enn.ionic.databinding.ItemGasBannerBinding
import com.enn.ionic.utils.CommonUtil
import com.enn.ionic.utils.CommonUtil.hideGasNum
import com.youth.banner.adapter.BannerAdapter


class FirstBannerGasAdapter(mData: List<GasItem>?) :
    BannerAdapter<GasItem, FirstBannerGasAdapter.BannerViewHolder>(mData) {

    private lateinit var mContext: Context

    //创建ViewHolder，可以用viewType这个字段来区分不同的ViewHolder
    override fun onCreateHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        mContext = parent.context
        return BannerViewHolder(
            ItemGasBannerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }


    override fun onBindView(holder: BannerViewHolder?, data: GasItem?, position: Int, size: Int) {

        holder?.text_balance?.text = data?.balance
        holder?.text_day?.text =
            "预计可用${if (data?.predictDays.isNullOrEmpty()) "-" else data?.predictDays}天"
//        holder.text_im.text =data.balance
        holder?.text_place?.text = data?.bjTplnrTxt + " " + hideGasNum(data?.serge)
        holder?.text_im?.load(data?.meterType?.let { CommonUtil.getGasIm(it) }) {
            placeholder(R.mipmap.im_gas_tab) //加载中占位图
            error(R.mipmap.im_gas_tab) //加载错误占位图
        }

        data?.predictDays?.let {
            when {
                it.toInt() <= 1 -> {
                    initBg(holder, ConstantObject.GAS_RED_BG)
                }
                it.toInt() == 2 -> {
                    initBg(holder, ConstantObject.GAS_ORANGE_BG)
                }
                else -> {
                    initBg(holder, ConstantObject.GAS_BLUE_BG)
                }
            }
        }


    }

    private fun initBg(holder: FirstBannerGasAdapter.BannerViewHolder?, gasRedBg: String) {
//        holder?.itemGasBalanceImBg?.load(gasRedBg) {
//            placeholder(R.mipmap.im_banner_gas_blue) //加载中占位图
//            error(R.mipmap.im_banner_gas_blue) //加载错误占位图
//            scale(Scale.FILL)
//        }
        holder?.itemGasBalanceImBg?.let {
            Glide.with(mContext).load(gasRedBg)
                .into(object : SimpleTarget<Drawable?>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable?>?
                    ) {
                        it.background = resource
                    }
                })
        }


    }

    inner class BannerViewHolder(@NonNull itemGasBannerBinding: ItemGasBannerBinding) :
        RecyclerView.ViewHolder(itemGasBannerBinding.root) {

        val text_balance = itemGasBannerBinding.itemGasBalance
        val text_day = itemGasBannerBinding.itemGasDay
        val text_im = itemGasBannerBinding.itemGasIm
        val text_place = itemGasBannerBinding.itemGasPlace
        val itemGasBalanceImBg = itemGasBannerBinding.itemGasBalanceCl
    }
}