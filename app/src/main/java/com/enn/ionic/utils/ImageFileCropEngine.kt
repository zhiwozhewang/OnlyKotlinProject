package com.enn.ionic.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import coil.imageLoader
import coil.request.ImageRequest
import com.enn.ionic.R
import com.luck.picture.lib.config.PictureSelectionConfig
import com.luck.picture.lib.engine.CropFileEngine
import com.luck.picture.lib.utils.StyleUtils
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropImageEngine
import java.io.File

class ImageFileCropEngine(val context: Context) : CropFileEngine {
    override fun onStartCrop(
        fragment: Fragment,
        srcUri: Uri,
        destinationUri: Uri,
        dataSource: ArrayList<String>,
        requestCode: Int
    ) {
        val options = buildOptions()
        val uCrop = UCrop.of(srcUri, destinationUri, dataSource)
        uCrop.withOptions(options)
        uCrop.setImageEngine(object : UCropImageEngine {
            override fun loadImage(context: Context, url: String, imageView: ImageView) {
                if (!ImageLoaderUtils.assertValidRequest(context)) {
                    return
                }
                val target = ImageRequest.Builder(context)
                    .data(url)
                    .target(imageView)
                    .build()
                context.imageLoader.enqueue(target)
//                                Glide.with(context).load(url).override(180, 180).into(imageView);
            }

            override fun loadImage(
                context: Context,
                url: Uri,
                maxWidth: Int,
                maxHeight: Int,
                call: UCropImageEngine.OnCallbackListener<Bitmap>
            ) {
            }
        })
        uCrop.start(fragment.requireActivity(), fragment, requestCode)
    }

    /**
     * 配制UCrop，可根据需求自我扩展
     *
     * @return
     */
    private fun buildOptions(): UCrop.Options {
        val options = UCrop.Options()
        options.setHideBottomControls(true);
        options.setFreeStyleCropEnabled(false);
        options.setShowCropFrame(false);
        options.setShowCropGrid(false);
        options.setCircleDimmedLayer(true)
        options.withAspectRatio(1f, 1f);
        options.setCropOutputPathDir(getSandboxPath());
        options.isCropDragSmoothToCenter(false)
        options.setSkipCropMimeType(null);
        options.isForbidCropGifWebp(false);
        options.isForbidSkipMultipleCrop(false)
        options.setMaxScaleMultiplier(100f)
        if (PictureSelectionConfig.selectorStyle != null && PictureSelectionConfig.selectorStyle.selectMainStyle.statusBarColor != 0) {
            val mainStyle = PictureSelectionConfig.selectorStyle.selectMainStyle
            val isDarkStatusBarBlack = mainStyle.isDarkStatusBarBlack
            val statusBarColor = mainStyle.statusBarColor
            options.isDarkStatusBarBlack(isDarkStatusBarBlack)
            if (StyleUtils.checkStyleValidity(statusBarColor)) {
                options.setStatusBarColor(statusBarColor)
                options.setToolbarColor(statusBarColor)
            } else {
                options.setStatusBarColor(
                    ContextCompat.getColor(
                        context,
                        R.color.color_393A3E
                    )
                )
                options.setToolbarColor(ContextCompat.getColor(context, R.color.color_393A3E))
            }
            val titleBarStyle = PictureSelectionConfig.selectorStyle.titleBarStyle
            if (StyleUtils.checkStyleValidity(titleBarStyle.titleTextColor)) {
                options.setToolbarWidgetColor(titleBarStyle.titleTextColor)
            } else {
                options.setToolbarWidgetColor(ContextCompat.getColor(context, R.color.white))
            }
        } else {
            options.setStatusBarColor(ContextCompat.getColor(context, R.color.color_393A3E))
            options.setToolbarColor(ContextCompat.getColor(context, R.color.color_393A3E))
            options.setToolbarWidgetColor(ContextCompat.getColor(context, R.color.white))
        }
        return options
    }

    /**
     * 创建自定义输出目录
     *
     * @return
     */
    private fun getSandboxPath(): String {
        val externalFilesDir: File? = context.getExternalFilesDir("")
        val customFile = File(externalFilesDir?.absolutePath, "Sandbox")
        if (!customFile.exists()) {
            customFile.mkdirs()
        }
        return customFile.absolutePath + File.separator
    }
}