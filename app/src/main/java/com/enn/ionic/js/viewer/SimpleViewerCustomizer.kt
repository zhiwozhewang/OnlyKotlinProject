package com.enn.ionic.js.viewer

import android.Manifest
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.enn.base.base.IUiView
import com.enn.base.ktx.inflate
import com.enn.base.ktx.setOnClickCallback
import com.enn.ionic.download.toDownLoad
import com.enn.network.toast
import com.enn.network.utils.LogUtils
import com.github.iielse.imageviewer.ImageViewerActionViewModel
import com.github.iielse.imageviewer.ImageViewerBuilder
import com.github.iielse.imageviewer.R
import com.github.iielse.imageviewer.adapter.ItemType
import com.github.iielse.imageviewer.core.OverlayCustomizer
import com.github.iielse.imageviewer.core.Photo
import com.github.iielse.imageviewer.core.VHCustomizer
import com.github.iielse.imageviewer.core.ViewerCallback
import com.kongzue.dialogx.DialogX
import com.kongzue.dialogx.dialogs.*
import com.kongzue.dialogx.interfaces.BaseDialog
import com.kongzue.dialogx.interfaces.DialogLifecycleCallback
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.request.ExplainScope
import java.io.File


/**
 * viewer 自定义业务&UI
 */
open class SimpleViewerCustomizer : LifecycleEventObserver, VHCustomizer, OverlayCustomizer,
    ViewerCallback {
    private var activity: FragmentActivity? = null
    private var viewerViewModel: ImageViewerActionViewModel? = null
    private var indicatorDecor: View? = null
    private var indicator: TextView? = null
    private var current: TextView? = null
    private var total: TextView? = null
    private var currentPosition = -1
    private var totalCount: String? = null

    /**
     * 对viewer进行自定义封装. 添加自定义指示器.video绑定.图片说明等自定义配置
     */
    fun process(activity: FragmentActivity, builder: ImageViewerBuilder, totalCount: String) {
        this.activity = activity
        viewerViewModel = ViewModelProvider(activity)[ImageViewerActionViewModel::class.java]
        activity.lifecycle.addObserver(this)
        builder.setVHCustomizer(this)
        builder.setOverlayCustomizer(this)
        builder.setViewerCallback(this)
        this.totalCount = totalCount
    }

    override fun initialize(type: Int, viewHolder: RecyclerView.ViewHolder) {
//        (viewHolder.itemView as? ViewGroup?)?.let {
//            it.addView(it.inflate(R.layout.item_photo_custom_layout))
//        }
        when (type) {
            ItemType.SUBSAMPLING -> {
                viewHolder.itemView.findViewById<View>(R.id.subsamplingView)
                    ?.setOnClickCallback { viewerViewModel?.dismiss() }
            }
            ItemType.PHOTO -> {
                viewHolder.itemView.findViewById<View>(R.id.photoView)?.setOnClickCallback {
                    viewerViewModel?.dismiss()
                }
            }
        }
    }

    override fun provideView(parent: ViewGroup): View {
        return parent.inflate(com.enn.ionic.R.layout.layout_indicator).also {
            current = it.findViewById(com.enn.ionic.R.id.tv_current)
            total = it.findViewById(com.enn.ionic.R.id.tv_total)
            total?.text = totalCount
        }
    }

    override fun bind(type: Int, data: Photo, viewHolder: RecyclerView.ViewHolder) {
        val myData = data as MyData
        viewHolder.itemView.findViewById<View>(R.id.photoView)?.setOnLongClickListener {
            BaseDialog.cleanAll()
            DialogX.implIMPLMode = DialogX.IMPL_MODE.DIALOG_FRAGMENT

            BottomMenu.show(arrayOf("保存"))
                .setDialogLifecycleCallback(object : DialogLifecycleCallback<BottomDialog>() {
                    override fun onDismiss(dialog: BottomDialog?) {
                        super.onDismiss(dialog)
                        BaseDialog.cleanAll()
                        DialogX.implIMPLMode = DialogX.IMPL_MODE.VIEW
                    }
                })
                .setCancelButton("取消")
                .setOnMenuItemClickListener { dialog, text, index ->
                    saveImageToSdcard(myData.url)
                    false
                }
            true
        }
    }

    override fun onRelease(viewHolder: RecyclerView.ViewHolder, view: View) {
        indicatorDecor?.animate()?.setDuration(200)?.alpha(0f)?.start()
        release()
    }

    override fun onPageSelected(position: Int, viewHolder: RecyclerView.ViewHolder) {
        currentPosition = position
        indicator?.text = position.toString()
        current?.text = "${position + 1}"
    }


    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
    }

    private fun release() {
        activity?.lifecycle?.removeObserver(this)
        activity = null
        indicatorDecor = null
        indicator = null
        current = null
        total = null
    }


    private fun saveImageToSdcard(url: String) {
        PermissionX.init(activity)
            .permissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .onExplainRequestReason { scope: ExplainScope, deniedList: List<String> ->
                val message: String = "慧用能需要您同意一下权限才能使用"
                scope.showRequestReasonDialog(
                    deniedList,
                    message, "确定",
                    "取消"
                )
            }
            .request { allGranted: Boolean, grantedList: List<String?>?, deniedList: List<String?>? ->
                if (allGranted) {
                    val cacheDir = Environment.getExternalStorageDirectory().absolutePath + "/Download"
                    createDir(cacheDir)
                    val fileName: String = if (url.contains("downPdf")) {
                        "${System.currentTimeMillis()}.pdf"
                    } else {
                        "${System.currentTimeMillis()}.png"
                    }

                    (activity as IUiView).toDownLoad<Any>(url, File(cacheDir), fileName) {
                        onError = {
                            toast("下载失败")
                        }
                        onSuccess = {
                            if (!url.contains("downPdf")){
                                SaveUtils.saveImgToAlbum(activity!!, it.absolutePath)
                            }
                            toast("保存成功")
                        }
                        onInProgress = { it ->
                            LogUtils.d("下载进度:$it")
                        }
                    }
                } else {
                    toast("保存失败")
                }
            }
    }

    private fun createDir(rootPath:String):Boolean{
        val dirRoot = File(rootPath)
        if (!dirRoot.exists() || !dirRoot.isDirectory) {
            val isCreateRoot = dirRoot.mkdirs();
            return isCreateRoot;
        }
        return true;
    }
}
