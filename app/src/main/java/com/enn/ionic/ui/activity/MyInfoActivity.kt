package com.enn.ionic.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.CircleCropTransformation
import com.dylanc.viewbinding.binding
import com.enn.base.base.BaseActivity
import com.enn.base.data.getValue
import com.enn.base.data.putValue
import com.enn.base.eventbus.Event
import com.enn.base.eventbus.FlowEventBus
import com.enn.base.ktx.startActivity
import com.enn.base.util.*
import com.enn.base.util.statusbar.StatusBarCompat
import com.enn.ionic.R
import com.enn.ionic.bean.ToAddInfoBean
import com.enn.ionic.bean.ToUpdateInfoBean
import com.enn.ionic.databinding.ActivityMyinfoBinding
import com.enn.ionic.js.CoilEngine
import com.enn.ionic.js.FileResponse
import com.enn.ionic.js.ImageFileCompressEngine
import com.enn.ionic.js.TokenBody
import com.enn.ionic.net.RetrofitClient
import com.enn.ionic.utils.ImageFileCropEngine
import com.enn.ionic.utils.MyDialogUtils
import com.enn.ionic.vm.InfoViewModel
import com.enn.network.entity.ApiSuccessResponse
import com.enn.network.toast
import com.enn.network.utils.LogUtils
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class MyInfoActivity : BaseActivity(R.layout.activity_myinfo) {

    val mViewModel by viewModels<InfoViewModel>()
    val mBinding by binding<ActivityMyinfoBinding>()
    var toUpdateInfoBean: ToUpdateInfoBean? = null
    lateinit var token: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarCompat.steepStatusBarText(this, false)
        initView()
    }

    private fun initView() {
        mBinding.myinfoBack.setOnClickListener { finish() }

        mBinding.myinfoPhone.setRightText(getValue("phone", ""))
        mBinding.myinfoHead.setOnClickListener {
            MyDialogUtils.showPicDialog { fromCameraOrLocal: Int -> changeHeadIm(fromCameraOrLocal) }
        }
        mBinding.myinfoUsername.setOnClickListener { toChange("修改用户名") }
        mBinding.myinfoJob.setOnClickListener { toChange("修改职务") }
//
//        FlowEventBus.observe<Event.SaveInfoBean<ToUpdateInfoBean>>(this) { it ->
//            mViewModel.saveInfoData(it.bean)
//            refreshInfo(it.bean)
//        }
        refreshToken()
    }

    override fun onResume() {
        super.onResume()
        getInfo()
    }

    override fun onDestroy() {
        mViewModel.saveInfoData(toUpdateInfoBean)
        toUpdateInfoBean?.let {
            FlowEventBus.post(Event.SaveInfoBean(it))
        }
        super.onDestroy()
    }

    /**
     *获取个人信息
     */
    private fun getInfo() {
        launchWithLoadingAndCollect({
            mViewModel.getInfo(getValue("username", ""))
        }) {
            onSuccess = {

                when {
                    it.isNullOrEmpty() -> {
                        addInfo()
                    }
                    it[0].id.isEmpty() -> {
                        addInfo(it[0])
                    }
                    else -> {
                        toUpdateInfoBean = it[0]
                        refreshInfo(toUpdateInfoBean)
                    }
                }
            }
            onFailed = { _, errorMsg ->
                toast(errorMsg ?: "")
            }
            onDataEmpty = {
                addInfo()
            }
        }
    }

    /**
     *添加个人信息
     */
    private fun addInfo(toUpdateInfoBean: ToUpdateInfoBean? = null) {
        launchAndCollect({
            mViewModel.addInfo(
                ToAddInfoBean(
                    toUpdateInfoBean?.headImageUrl,
                    getValue("username", ""),
                    toUpdateInfoBean?.nickname,
                    toUpdateInfoBean?.position
                )
            )
        }) {
            onSuccess = {
                if (it?.get(0) == true) {
                    getInfo()
                } else {
                    toast("添加个人信息失败")
                }
            }
            onFailed = { _, errorMsg ->
                toast(errorMsg ?: "")
            }
        }
    }

    /**
     *刷新个人信息
     */
    private fun refreshInfo(bean: ToUpdateInfoBean?) {
        mBinding.myinfoUsername.setRightText(bean?.nickname ?: "")
        mBinding.myinfoJob.setRightText(bean?.position ?: "")
        mBinding.myinfoHead.load(bean?.headImageUrl) {
            transformations(CircleCropTransformation())
            error(R.mipmap.im_mine_head)
            placeholder(R.mipmap.im_mine_head)
        }

    }

    private fun toChange(title: String) {
        toUpdateInfoBean?.let { it1 ->
            startActivity<MyInfoChangeActivity>(Bundle().also {
                it.putSerializable("bean", it1)
                it.putString("title", title)
            })
        }
    }

    private fun changeHeadIm(fromCameraOrLocal: Int) {
        when (fromCameraOrLocal) {
            0 -> {
                PictureSelector.create(this).openCamera(SelectMimeType.ofImage())
                    .setCompressEngine(ImageFileCompressEngine())
                    .setCropEngine(ImageFileCropEngine(this))
                    .forResult(object : OnResultCallbackListener<LocalMedia> {
                        override fun onResult(result: ArrayList<LocalMedia>?) {
                            result?.let {
                                uploadFiles(it)
                            }
                        }

                        override fun onCancel() {

                        }
                    })
            }
            1 -> {
                PictureSelector.create(this)
                    .openGallery(SelectMimeType.ofImage())
                    .setImageEngine(CoilEngine())
                    .setCropEngine(ImageFileCropEngine(this))
                    .setCompressEngine(ImageFileCompressEngine())
                    .setMaxSelectNum(1)
                    .setMinSelectNum(1)
                    .forResult(object : OnResultCallbackListener<LocalMedia> {
                        override fun onResult(result: ArrayList<LocalMedia>?) {
                            result?.let {
                                uploadFiles(it)
                            }
                        }

                        override fun onCancel() {

                        }
                    })
            }
            else -> {}
        }
    }

    /**
     *上传到文件服务 获取url
     */
    private fun uploadFiles(
        result: ArrayList<LocalMedia>
    ) {
        lifecycleScope.launch {
            runCatching {
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
                        LogUtils.e("上传图片后的Url:${it.toString()}")
                        val apiResponse = toUpdateInfoBean?.apply {
                            headImageUrl = it.urlList[0]
                        }?.let { it1 -> mViewModel.updateInfo(it1) }
                        if (apiResponse is ApiSuccessResponse) {
                            if (apiResponse.result?.get(0) == true) {
                                putValue("headim", toUpdateInfoBean?.headImageUrl)
                                refreshInfo(toUpdateInfoBean)
                            } else
                                toast("修改头像失败${apiResponse.message}")

                        } else {
                            toast("修改头像失败${apiResponse?.message}")
                        }
                    }
                }
            }.onFailure {
                toast("获取图片失败${it.message}")
            }
        }
    }

    /**
     *获取文件服务token
     */
    private fun refreshToken() {
        lifecycleScope.launch(Dispatchers.IO) {
            runCatching {
                val response =
                    RetrofitClient.service.getToken(TokenBody("gradmin", "admin123", "serlink_h5"))
                if (response.code == 200) {
                    token = response.token
                } else {
                    LogUtils.e("获取文件服务器token失败")
                }
            }.onFailure {
                LogUtils.e("获取文件服务器token异常")
            }
        }
    }


}