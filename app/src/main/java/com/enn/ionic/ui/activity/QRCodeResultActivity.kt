package com.enn.ionic.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.activity.viewModels
import com.dylanc.viewbinding.binding
import com.enn.base.base.BaseActivity
import com.enn.base.base.IUiView
import com.enn.base.data.getValue
import com.enn.base.data.putValue
import com.enn.base.util.statusbar.StatusBarCompat
import com.enn.ionic.ConstantObject
import com.enn.ionic.R
import com.enn.ionic.databinding.ActivityFindpwBinding
import com.enn.ionic.databinding.ActivityQrcodeResultBinding
import com.enn.ionic.databinding.QrcodeScanActivityBinding
import com.enn.ionic.vm.LoginViewModel
import com.enn.ionic.vm.ScanViewModel
import com.enn.network.toast
import com.google.mlkit.vision.barcode.Barcode
import com.king.mlkit.vision.barcode.QRCodeCameraScanActivity
import com.king.mlkit.vision.camera.AnalyzeResult


class QRCodeResultActivity : BaseActivity(R.layout.activity_qrcode_result) {

    val mBinding by binding<ActivityQrcodeResultBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarCompat.themeAndColorStatusBar(this)
        initView()
    }

    private fun initView() {

        var result = intent.getIntExtra("scan_result", 0)
        when (result) {
            0 -> {}
            else -> {
                mBinding.scanResultIm.setImageResource(R.mipmap.im_scan_result_faild)
                mBinding.scanResultText.text = "二维码已失效"
            }
        }
        mBinding.scanResultClose.setOnClickListener { finish() }
        mBinding.scanResultBtn.setOnClickListener { finish() }

    }


}