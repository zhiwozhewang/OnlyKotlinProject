package com.enn.ionic.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.InputFilter
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import com.youth.banner.util.LogUtils
import java.io.File
import java.util.regex.Matcher
import java.util.regex.Pattern


object CommonUtil {
    /**
     * 安装APk
     *
     * @param file     文件
     * @param activity 上下文
     */
    fun installApk(file: File, activity: Activity) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
            } else { //Android7.0之后获取uri要用contentProvider
                intent.setDataAndType(
                    getUriFromFile(file, activity),
                    "application/vnd.android.package-archive"
                )
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    val hasInstallPermission: Boolean =
//                        activity.packageManager.canRequestPackageInstalls()
//                    if (!hasInstallPermission) {
//                        startInstallPermissionSettingActivity(activity)
//                    }
//                }

            }
            activity.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startInstallPermissionSettingActivity(activity: Activity) {
        //注意这个是8.0新API
        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivity(intent)
    }

    private fun getUriFromFile(file: File, activity: Activity): Uri {
        val imageUri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val authority = activity.packageName + ".fileProvider"
            FileProvider.getUriForFile(activity, authority, file)
        } else {
            Uri.fromFile(file)
        }
        return imageUri
    }

    fun hidePhone(phonenum: String): String {
        val sb = StringBuilder()
        if (phonenum.length > 6) {
            for (i in phonenum.indices) {
                val c = phonenum[i]
                if (i in 3..6) {
                    sb.append('*')
                } else {
                    sb.append(c)
                }
            }
        }
        return sb.toString()
    }

    fun subingJob(job: String): String {
        val sb = StringBuilder()
        if (job.length > 5) {
            sb.append(job.substring(0, 5))
            sb.append("...")
        } else
            sb.append(job)
        return sb.toString()
    }

    fun hideGasNum(num: String?): String {
        val sb = StringBuilder()
        if (num != null) {
            if (num.length > 8) {
                val numtop = num.substring(0, 4)
                sb.append(numtop)
                sb.append("*")
                val numbottom = num.substring(num.length - 4)
                sb.append(numbottom)
            } else {
                sb.append(num)
            }
        }
        return sb.toString()
    }

    /**
     * 禁止EditText输入空格和换行符
     *
     * @param editText EditText输入框
     */
    fun setEditTextInputSpace(editText: EditText) {
        val filter =
            InputFilter { source, _, _, _, _, _ ->
                if (source == " " || source.toString().contentEquals("\n")) {
                    ""
                } else {
                    source.toString()
                }
            }
        editText.filters = arrayOf(filter)
    }

    /**
     * 禁止EditText输入特殊字符
     *
     * @param editText EditText输入框
     */
    fun setEditTextInputSpeChat(editText: EditText) {
        val filter =
            InputFilter { source, _, _, _, _, _ ->
                val speChat = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]"
                val pattern: Pattern = Pattern.compile(speChat)
                val matcher: Matcher = pattern.matcher(source.toString())
                if (matcher.find()) {
                    ""
                } else {
                    null
                }
            }
        editText.filters = arrayOf(filter)
    }

    fun getGasIm(meterType: String): String {
        var imurl: String
        when (meterType) {
            null -> {
                imurl = "https://lfrz1.stor.enncloud.cn/enn-oss/account2688/空表38152.png"
            }
            "iot" -> {
                imurl = "https://lfrz1.stor.enncloud.cn/enn-oss/account2688/iot表84747.png"
            }
            "磁卡表" -> {
                imurl = "https://lfrz1.stor.enncloud.cn/enn-oss/account2688/磁卡表63202.png"
            }
            "二代" -> {
                imurl = "https://lfrz1.stor.enncloud.cn/enn-oss/account2688/二代表88035.png"
            }
            "金卡" -> {
                imurl = "https://lfrz1.stor.enncloud.cn/enn-oss/account2688/金卡表62855.png"
            }
            "普表非物联" -> {
                imurl = "https://lfrz1.stor.enncloud.cn/enn-oss/account2688/普表13041.png"
            }
            "物联表" -> {
                imurl = "https://lfrz1.stor.enncloud.cn/enn-oss/account2688/物联表73816.png"
            }
            else -> {
                imurl = "https://lfrz1.stor.enncloud.cn/enn-oss/account2688/空表38152.png"

            }
        }
        return imurl
    }

    fun checkTimeIsLimit(time: String): Boolean {
        return when (time.length >= 12) {
            true -> {
                val systime = time.substring(11).replace(":", "").toInt()
                systime in 230000..240000
            }
            else -> {
                false
            }
        }
    }
}