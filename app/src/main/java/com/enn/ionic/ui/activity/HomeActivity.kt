package com.enn.ionic.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.enn.base.base.BaseActivity
import com.enn.base.util.statusbar.StatusBarCompat
import com.enn.ionic.R
import com.kongzue.dialogx.dialogs.CustomDialog
import com.kongzue.dialogx.interfaces.OnBindView

class HomeActivity : BaseActivity(R.layout.activity_home) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarCompat.themeAndColorStatusBar(this)
    }
}