package com.enn.ionic.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.activity.viewModels
import com.dylanc.viewbinding.binding
import com.enn.base.base.IUiView
import com.enn.base.data.getValue
import com.enn.base.data.putValue
import com.enn.base.ktx.startActivity
import com.enn.ionic.ConstantObject
import com.enn.ionic.R
import com.enn.ionic.databinding.ActivityFindpwBinding
import com.enn.ionic.databinding.QrcodeScanActivityBinding
import com.enn.ionic.utils.launchNoLoadingNoBase
import com.enn.ionic.vm.LoginViewModel
import com.enn.ionic.vm.ScanViewModel
import com.enn.network.toast
import com.google.mlkit.vision.barcode.Barcode
import com.king.mlkit.vision.barcode.QRCodeCameraScanActivity
import com.king.mlkit.vision.camera.AnalyzeResult

//二维码扫描页面，扫描成功会关闭页面，结果会放在Result中，取RESULT_KEY_SCAN
class QRCodeScanningActivity : QRCodeCameraScanActivity(), IUiView {

    val mViewModel by viewModels<ScanViewModel>()
    val mBinding by binding<QrcodeScanActivityBinding>()
    var scan_result = 0

    override fun initCameraScan() {
        super.initCameraScan()
        cameraScan.setPlayBeep(true)
            .setVibrate(true)
//        getScanToken(getValue("token", ""), "e3736bf1-46b3-48bd-b5c8-1de519ae990f")

    }

    override fun getLayoutId(): Int {
        return R.layout.qrcode_scan_activity
    }

    override fun onScanResultCallback(result: AnalyzeResult<MutableList<Barcode>>) {
        cameraScan.setAnalyzeImage(false)
        result.result[0].rawValue?.let {
//            setResult(RESULT_CODE_SCAN, Intent().putExtra(RESULT_KEY_SCAN, it))
//            finish()
            getScanToken(getValue("token", ""), it)
        }
    }

    private fun getScanToken(token: String, id: String) {
        launchNoLoadingNoBase({
            mViewModel.getScanToken(token, id)
        }) {
            onSuccess = {
                if (TextUtils.equals(ConstantObject.Result_Code_Sucess, it?.body?.resultCode)) {
//                    toast("获取临时token成功")
                    it?.body?.data?.let { it1 ->
                        toScanBind(
                            token,
                            it1,
                            id,
                            getValue("bp", ""),
                            getValue("username", "")
                        )
                    }
                } else {
//                    toast("获取临时token失败:${it?.body?.resultDesc}")
                    scan_result = 1
                    toScanResult(scan_result)
                }
            }
        }

    }


    private fun toScanBind(
        token: String, tempToken: String,
        id: String,
        companyId: String, username: String
    ) {
        launchNoLoadingNoBase({
            mViewModel.toScanBind(
                token,
                tempToken,
                id,
                companyId,
                username
            )
        }) {
            onSuccess = {
                if (TextUtils.equals(ConstantObject.Result_Code_Sucess, it?.body?.resultCode)) {
//                    toast("pc登录成功")
                } else {
                    scan_result = 1
//                    toast("pc${it?.body?.resultDesc}")
                }
                toScanResult(scan_result)

            }
        }

    }

    private fun toScanResult(scanResult: Int) {
        startActivity<QRCodeResultActivity>(Bundle().also { it2 ->
            it2.putInt(
                "scan_result",
                scanResult
            )
        })
        finish()
    }

    companion object {
        const val RESULT_CODE_SCAN = 0X11
        const val RESULT_KEY_SCAN = "RESULT_KEY_SCAN"
    }

    override fun showLoading() {
    }

    override fun dismissLoading() {
    }
}