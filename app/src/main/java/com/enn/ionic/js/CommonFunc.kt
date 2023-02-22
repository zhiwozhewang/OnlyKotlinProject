package com.enn.ionic.js

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.JavascriptInterface
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.enn.base.base.BaseActivity
import com.enn.base.base.IUiView
import com.enn.base.data.WebBaseBean
import com.enn.base.eventbus.Event
import com.enn.base.eventbus.FlowEventBus
import com.enn.base.ktx.getSelfVersionCode
import com.enn.base.ktx.toCall
import com.enn.base.util.parseWebJsonString
import com.enn.base.util.parseWebJsonStringAll
import com.enn.base.util.statusbar.StatusBarCompat
import com.enn.ionic.ConstantObject
import com.enn.ionic.bean.webbean.*
import com.enn.ionic.databinding.ActivityWebNewBinding
import com.enn.ionic.download.toDownLoad
import com.enn.ionic.js.viewer.ViewerHelper
import com.enn.ionic.net.RetrofitClient
import com.enn.ionic.ui.activity.AccountCheckActivity
import com.enn.ionic.ui.activity.WebActivityNew
import com.enn.ionic.utils.getCurrentLocation
import com.enn.ionic.utils.toAliPay2
import com.enn.ionic.utils.toLogin
import com.enn.ionic.utils.toWechatPay
import com.enn.network.toast
import com.enn.network.utils.LogUtils
import com.google.gson.Gson
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.permissionx.guolindev.PermissionX
import com.tencent.smtt.sdk.QbSdk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import me.jessyan.autosize.utils.ScreenUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.File

class CommonFunc(
    activity: AppCompatActivity,
    val binding: ActivityWebNewBinding
) :
    BaseFunc(activity, binding.webWrapper.webView) {

    @JavascriptInterface
    fun takePhoto(json: String) {
        LogUtils.e("thread-takePhoto:${Thread.currentThread().name}");
        val result = parseWebJsonStringAll<WebBaseBean<TakePhotoBean>>(json)
        CoroutineScope(Dispatchers.Main).launch {
//            (activity as BaseActivity).showLoading()
            result?.let { webBaseBean ->
                if (webBaseBean.data.type == 0) {
                    PictureSelector.create(activity).openCamera(SelectMimeType.ofImage())
                        .setCompressEngine(ImageFileCompressEngine())
                        .forResult(object : OnResultCallbackListener<LocalMedia> {
                            override fun onResult(result: ArrayList<LocalMedia>?) {
                                result?.let {
                                    LogUtils.e("thread-onResult:${Thread.currentThread().name}");
                                    uploadFiles(webBaseBean, it)
                                }
                            }

                            override fun onCancel() {
//                                (activity as BaseActivity).dismissLoading()
                                callBackHtml(
                                    webBaseBean.sCallback,
                                    FileResponse(arrayOf(), 0, 0).toString()
                                )
                            }
                        })
                } else {
                    PictureSelector.create(activity)
                        .openGallery(SelectMimeType.ofImage())
                        .setImageEngine(CoilEngine())
                        .setCompressEngine(ImageFileCompressEngine())
                        .setMaxSelectNum(webBaseBean.data.maxSelectNum)
                        .setMinSelectNum(webBaseBean.data.minSelectNum)
                        .forResult(object : OnResultCallbackListener<LocalMedia> {
                            override fun onResult(result: ArrayList<LocalMedia>?) {
                                result?.let {
                                    LogUtils.e("thread-onResult:${Thread.currentThread().name}");
                                    uploadFiles(webBaseBean, it)
                                }
                            }

                            override fun onCancel() {
//                                (activity as BaseActivity).dismissLoading()
                                callBackHtml(
                                    webBaseBean.sCallback,
                                    FileResponse(arrayOf(), 0, 0).toString()
                                )
                            }
                        })
                }
            }
        }

    }

    private fun uploadFiles(
        webBaseBean: WebBaseBean<TakePhotoBean>,
        result: ArrayList<LocalMedia>
    ) {
        activity.lifecycleScope.launch {
            activity.runCatching {
                LogUtils.e("thread-uploadFiles:${Thread.currentThread().name}");
                if (result.isNotEmpty()) {
                    val flowList = result.map { localMedia ->
                        val file = File(localMedia.compressPath)
                        val requestBody =
                            file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                        val photoPart = MultipartBody.Part.createFormData(
                            "file",
                            file.name,
                            requestBody
                        )
                        flowOf(
                            RetrofitClient.service.uploadFile(
                                token,
                                photoPart
                            )
                        )
                    }
                    combine(flowList) { it ->
                        val urlList = mutableListOf<String>()
                        var sCount = 0
                        var fCount = 0

                        it.forEach { uploadPic ->
                            if (uploadPic.code == 200) {
                                urlList.add(uploadPic.data.fileUrl)
                                sCount++
                            } else {
                                fCount++
                            }
                        }

                        FileResponse(urlList.toTypedArray(), sCount, fCount)
                    }.collect {
                        callBackHtml(webBaseBean.sCallback, it.toString())
//                        (activity as BaseActivity).dismissLoading()

                    }
                }
            }.onFailure {
                callBackHtml(webBaseBean.fCallback, "获取图片失败${it.message}")
//                (activity as BaseActivity).dismissLoading()
            }
        }
    }

    @JavascriptInterface
    fun getAppVersion(version: String): String {
//        val webTitleBean = parseWebJsonString<WebAppVersionBean>(version)
        val webAppVersionReturnBean = WebAppVersionReturnBean(activity.getSelfVersionCode())
        return Gson().toJson(webAppVersionReturnBean)
    }

    @JavascriptInterface
    fun windowClose() {
        LogUtils.d("退出页面")
        activity.finish()
    }

    @JavascriptInterface
    fun openNewUrl(url: String) {
        LogUtils.d("跳转新页面$url")
        val webNewUrlBean = parseWebJsonString<WebNewUrlBean>(url)

        activity.startActivity(
            Intent(activity, WebActivityNew::class.java).apply {
                putExtras(Bundle().apply {
                    putCharSequence("url", webNewUrlBean?.url)

                })
            }
        )
//        binding.webWrapper.loadUrl(webNewUrlBean?.url)

    }

    @JavascriptInterface
    fun phone(phone: String) {
        LogUtils.d("phone:$phone")
        val webPhoneBean = parseWebJsonString<WebPhoneBean>(phone)
        webPhoneBean?.let { activity.toCall(it.phone) }

    }

    @JavascriptInterface
    fun downloadFile(file: String) {
        LogUtils.d("文件下载：$file")

        val webBaseBean = parseWebJsonStringAll<WebBaseBean<WebFileBean>>(file)
        //下载
        webBaseBean?.data?.fileUrl?.let {
            activity.getExternalFilesDir("")?.let { it1 ->
                (activity as IUiView).toDownLoad<WebFileBean>(it, it1, File(it).name) {
                    onError = {
                        LogUtils.d("下载失败")
                        binding.webWrapper.toCallJs(webBaseBean.fCallback)
                    }
                    onSuccess = {
                        LogUtils.d("下载成功")
                        binding.webWrapper.toCallJs(webBaseBean.sCallback)
                    }
                    onInProgress = { it ->
                        LogUtils.d("下载进度:$it")

                    }
                }
            }
        }
    }

    @JavascriptInterface
    fun statusbarColor(color: String) {
        LogUtils.d("染色状态栏$color")

        val webStatusColorBean = parseWebJsonString<WebStatusColorBean>(color)
        StatusBarCompat.themeAndColorStatusBar(
            activity,
            webStatusColorBean?.color ?: "#FFFFFF",
            webStatusColorBean?.isDarkFont ?: true
        )

    }

    @JavascriptInterface
    fun previewOffice(file: String) {
        LogUtils.d("文件预览：$file")
        val webFileBean = parseWebJsonStringAll<WebBaseBean<WebFileBean>>(file)
        //下载
        webFileBean?.data?.fileUrl?.let {
            (activity as IUiView).toDownLoad<WebFileBean>(
                it,
                activity.cacheDir,
                File(it).name
            ) {
                onError = {
                    LogUtils.d("下载失败")
                    binding.webWrapper.toCallJs(webFileBean.fCallback)
                }
                onSuccess = { file ->
                    openFileReader(activity, file.absolutePath)
                }
                onInProgress = { it ->
                    LogUtils.d("下载进度:$it")

                }
            }
        }
    }

    @JavascriptInterface
    fun setNativeTitle(title: String) {
        val webTitleBean = parseWebJsonString<WebTitleBean>(title)
        binding.utilsTitle.post {
            binding.utilsTitle.text = webTitleBean?.titleName ?: ""
        }
    }

    @JavascriptInterface
    fun showLoading(loading: String) {

        (activity as BaseActivity).showLoading()

    }

    @JavascriptInterface
    fun dismissLoading(loading: String) {
        (activity as BaseActivity).dismissLoading()

    }

    @JavascriptInterface
    fun pay(pay: String) {
        LogUtils.d("微信支付：$pay")

        val webBaseBean = parseWebJsonStringAll<WebBaseBean<PayBean>>(pay)
        webBaseBean?.data?.let {
            ConstantObject.SUCESS_CALLBACK_METHOD = webBaseBean.sCallback
            ConstantObject.FAILD_CALLBACK_METHOD = webBaseBean.fCallback
            (activity as BaseActivity).toWechatPay(it)
        }
    }

    @JavascriptInterface
    fun xaAlipay(pay: String) {
        LogUtils.d("支付宝支付：$pay")

        val webBaseBean = parseWebJsonStringAll<WebBaseBean<String>>(pay)
        webBaseBean?.data?.let {
            (activity as BaseActivity).toAliPay2(
                webBaseBean.data,
                {
                    binding.webWrapper.toCallJs(webBaseBean.sCallback)
                },
                {
                    binding.webWrapper.toCallJs(webBaseBean.fCallback)
                }
            )
        }
    }

    @JavascriptInterface
    fun hideNativeTitle() {
        LogUtils.d("隐藏title")
//        沉浸状态栏
        binding.utilsFr.post {
//            透明状态栏之后沉浸
            StatusBarCompat.steepStatusBarText(activity, true, "#00000000")
            binding.utilsFr.visibility = View.GONE
        }
    }

    @JavascriptInterface
    fun showNativeTitle() {
        LogUtils.d("显示title")
        binding.utilsFr.post {
            StatusBarCompat.nosteepStatusBarText(activity)
            binding.utilsFr.visibility = View.VISIBLE
        }
    }


    @JavascriptInterface
    fun previewImage(json: String) {
        LogUtils.d("图片预览：$json")

        val webImageUrlListBean = parseWebJsonString<WebImageUrlListBean>(json)
        webImageUrlListBean?.let {
            activity.lifecycleScope.launch(Dispatchers.Main) {
                ViewerHelper.provideImageViewerBuilder(activity, it.urlList, it.index.toLong())
                    .show()
            }
        }
    }


    private fun openFileReader(context: Context, localPath: String?) {
        if (localPath.isNullOrEmpty()) {
            toast("路径不能为空")
        } else {
            val params = HashMap<String, String>()
            val obj = JSONObject()
            try {
                obj.put("pkgName", context.applicationContext.packageName)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
//            params["style"] = "1";
//            params["local"] = "true";
            params["menuData"] = obj.toString()
            QbSdk.getMiniQBVersion(context)
            QbSdk.openFileReader(
                context, localPath, params
            ) { }
        }

    }

    @JavascriptInterface
    fun getLocation(json: String) {
        val bean = parseWebJsonStringAll<WebBaseBean<Any>>(json)
        bean?.let {
            PermissionX.init(activity)
                .permissions(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                .request { allGranted: Boolean, _: List<String?>?, _: List<String?>? ->
                    if (allGranted) {
                        activity.lifecycleScope.launch(Dispatchers.Main) {
                            val location = getCurrentLocation(activity)
                            val jsonStr = JSONObject()
                            jsonStr.put("longitude", location?.latitude)
                            jsonStr.put("latitude", location?.longitude)
                            jsonStr.put("province", location?.province)
                            jsonStr.put("city", location?.city)
                            jsonStr.put("district", location?.district)
                            callBackHtml(it.sCallback, jsonStr.toString())
                        }
                    } else {
                        callBackHtml(it.fCallback, "缺少定位权限")
                    }
                }
        }
    }

    @JavascriptInterface
    fun getStatusBarHeight(json: String) {
        val bean = parseWebJsonStringAll<WebBaseBean<Any>>(json)
        bean?.let {
//            var result = 0
//            val resourceId: Int = activity.resources.getIdentifier(
//                "status_bar_height", "dimen", "android"
//            )
//            if (resourceId > 0) {
//                result = activity.resources.getDimensionPixelOffset(resourceId)
//            }

            var result =
                ScreenUtils.getStatusBarHeight() / (activity.resources.displayMetrics.density + 0.5)
            val jsonStr = JSONObject()
            jsonStr.put("height", result)
            callBackHtml(it.sCallback, jsonStr.toString())
        }
    }

    /**
     *  h5token异常  重新登录
     */
    @JavascriptInterface
    fun h5LogOut() {
        LogUtils.d("js调用原生退出登录")
        toLogin()
    }

    /**
     *  调用审核列表
     */
    @JavascriptInterface
    fun examineList() {
        LogUtils.d("调用审核列表")

        activity.startActivity(Intent(activity, AccountCheckActivity::class.java))
    }
    /**
     *  待办刷新主页
     */
    @JavascriptInterface
    fun refreshMainPage() {
        LogUtils.d("待办刷新主页")
        FlowEventBus.post(Event.AfterDoneToDo(0))

    }

}