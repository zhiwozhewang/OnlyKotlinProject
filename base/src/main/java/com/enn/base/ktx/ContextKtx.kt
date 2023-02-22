package com.enn.base.ktx

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.content.pm.PackageInfoCompat
import java.io.File

fun Context.toastNative(id: Int, length: Int = Toast.LENGTH_SHORT) {
    toastNative(getString(id), length)
}

fun Context.toastNative(msg: String, length: Int = Toast.LENGTH_SHORT) {
    try {
        if (isOnMainThread()) {
            doToast(this, msg, length)
        } else {
            Handler(Looper.getMainLooper()).post {
                doToast(this, msg, length)
            }
        }
    } catch (e: Exception) {
    }
}

private fun doToast(context: Context, message: String, length: Int) {
    if (context is Activity) {
        if (!context.isFinishing && !context.isDestroyed) {
            Toast.makeText(context, message, length).show()
        }
    } else {
        Toast.makeText(context, message, length).show()
    }
}

//获取版本号
fun Context.getSelfVersionCode(): String {
    val packageManager = packageManager
    val info = packageManager.getPackageInfo(packageName, 0)
    return PackageInfoCompat.getLongVersionCode(info).toString()
}

//获取versionName
fun Context.getSelfVersionName(): String {
    val packageManager = packageManager
    val info = packageManager.getPackageInfo(packageName, 0)
    return info.versionName
}

//打电话
fun Context.toCall(phone: String) {
    var intent = Intent()
    intent.action = Intent.ACTION_DIAL//dial是拨号的意思
    intent.data = Uri.parse("tel:$phone")//这个tel不能改，后面的数字可以随便改
    startActivity(intent)
}
