package com.enn.ionic.utils

import android.content.Context
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClient.updatePrivacyAgree
import com.amap.api.location.AMapLocationClient.updatePrivacyShow
import com.amap.api.location.AMapLocationClientOption
import kotlin.coroutines.resume

import kotlin.coroutines.suspendCoroutine

suspend fun getCurrentLocation(context: Context) =
    suspendCoroutine<AMapLocation?> { continuation ->
        updatePrivacyShow(context, true, true)
        updatePrivacyAgree(context,true);

        val mLocationClient = AMapLocationClient(context)
        //设置定位回调监听
        mLocationClient.setLocationListener {
            continuation.resume(it)
        }
        //初始化AMapLocationClientOption对象
        val mLocationOption = AMapLocationClientOption()
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.isOnceLocation = false
        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.isOnceLocationLatest = true
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.isNeedAddress = true
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.isMockEnable = false
        //关闭缓存机制
        mLocationOption.isLocationCacheEnable = false
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption)
        mLocationClient.stopLocation()
        //启动定位
        mLocationClient.startLocation()
    }
