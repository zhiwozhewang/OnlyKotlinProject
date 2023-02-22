package com.enn.ionic.adapter

import coil.load
import coil.transform.RoundedCornersTransformation
import com.enn.ionic.bean.FirstBannerBean
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder

class FirstBannerImAdapter<T>(mData: MutableList<T>?) : BannerImageAdapter<T>(mData) {

    override fun onBindView(holder: BannerImageHolder?, data: T, position: Int, size: Int) {


        //   coil使用     https://blog.csdn.net/vitaviva/article/details/113064062
        holder?.imageView?.load((data as FirstBannerBean).imageUrl) {
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