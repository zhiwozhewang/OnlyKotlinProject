package com.enn.ionic

import android.util.Log
import com.enn.base.base.BaseApp
import com.enn.ionic.ui.activity.QRCodeScanningActivity
import com.enn.ionic.utils.initWx
import com.luck.picture.lib.basic.PictureSelectorSupporterActivity
import com.networkbench.agent.impl.NBSAppAgent
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.QbSdk.PreInitCallback
import com.tencent.smtt.sdk.TbsDownloader
import com.tencent.smtt.sdk.TbsListener
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.unit.Subunits


class App : BaseApp() {
    override fun onCreate() {
        super.onCreate()

        AutoSizeConfig.getInstance().externalAdaptManager
            .addCancelAdaptOfActivity(PictureSelectorSupporterActivity::class.java)
            .addCancelAdaptOfActivity(QRCodeScanningActivity::class.java)
        AutoSizeConfig.getInstance().unitsManager.apply {
            isSupportDP = false
            setSupportSP(false).supportSubunits = Subunits.MM;
        }
        initWx()
        initTBS()
        initTingYun()
    }

    private fun initTingYun(){
        NBSAppAgent.setLicenseKey(BuildConfig.KEY_TINGYUN)
            .setRedirectHost("appapm.enn.cn")
            .withLocationServiceEnabled(true)
            .setHttpEnabled(true)
            .start(this.applicationContext)
    }

    private fun initTBS(){

        QbSdk.setDownloadWithoutWifi(true)

        /* 此过程包括X5内核的下载、预初始化，接入方不需要接管处理x5的初始化流程，希望无感接入 */
        QbSdk.initX5Environment(
            this,
            object : PreInitCallback {
                override fun onCoreInitFinished() {
                    // 内核初始化完成，可能为系统内核，也可能为系统内核
                }

                /**
                 * 预初始化结束
                 * 由于X5内核体积较大，需要依赖wifi网络下发，所以当内核不存在的时候，默认会回调false，此时将会使用系统内核代替
                 * 内核下发请求发起有24小时间隔，卸载重装、调整系统时间24小时后都可重置
                 * @param isX5 是否使用X5内核
                 */
                override fun onViewInitFinished(isX5: Boolean) {
                }
            })
        QbSdk.setTbsListener(object : TbsListener {
            /**
             * @param stateCode 110: 表示当前服务器认为该环境下不需要下载
             */
            override fun onDownloadFinish(stateCode: Int) {
            }

            /**
             * @param stateCode 200、232安装成功
             */
            override fun onInstallFinish(stateCode: Int) {
            }

            /**
             * 首次安装应用，会触发内核下载，此时会有内核下载的进度回调。
             * @param progress 0 - 100
             */
            override fun onDownloadProgress(progress: Int) {
            }
        })

        val needDownload = TbsDownloader.needDownload(this, TbsDownloader.DOWNLOAD_OVERSEA_TBS);
        if (needDownload) {
            TbsDownloader.startDownload(this);
        }

    }
}