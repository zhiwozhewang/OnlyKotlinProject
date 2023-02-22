package com.enn.ionic.js.viewer

import com.github.iielse.imageviewer.adapter.ItemType
import com.github.iielse.imageviewer.core.Photo

data class MyData(val id: Long,
                  val url: String,
                  val subsampling: Boolean = false,
                  val desc: String = "[$id] Caption or other information for this picture [$id]") : Photo {
    override fun id(): Long = id
    override fun itemType(): Int {
        return when {
            subsampling -> ItemType.SUBSAMPLING
            else -> ItemType.PHOTO
        }
    }
}
