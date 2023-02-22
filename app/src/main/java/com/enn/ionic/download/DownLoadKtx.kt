package com.enn.ionic.download

import androidx.lifecycle.lifecycleScope
import com.enn.base.base.IUiView
import kotlinx.coroutines.launch
import java.io.File


fun <T> IUiView.toDownLoad(
    url: String, filepath: File, filename: String,
    downLoadListener: DownLoadListener<T>.() -> Unit
) {
    lifecycleScope.launch {
        DownloadManager.download(url, File(filepath, filename)).collect {
            it.parseData<T>(downLoadListener)
        }
    }

}


