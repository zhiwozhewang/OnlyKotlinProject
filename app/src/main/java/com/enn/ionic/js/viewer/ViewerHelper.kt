package com.enn.ionic.js.viewer

import androidx.fragment.app.FragmentActivity
import com.github.iielse.imageviewer.ImageViewerBuilder
import com.github.iielse.imageviewer.ImageViewerDialogFragment
import com.github.iielse.imageviewer.core.DataProvider
import com.github.iielse.imageviewer.core.SimpleDataProvider

/**
 * viewer的自定义初始化方案
 */
object ViewerHelper {
    fun provideImageViewerBuilder(
        context: FragmentActivity,
        list: List<String>,
        index: Long
    ): ImageViewerBuilder {
        fun myDataProvider(): DataProvider {
            val myDataList =
                list.map {
                    MyData(list.indexOf(it).toLong(), it)
                }
            return SimpleDataProvider(myDataList)
        }

        val builder = ImageViewerBuilder(
            context = context,
            initKey = index,  // 被点击的图片id
            dataProvider = myDataProvider(), // 数据提供者. 和调用者业务强绑定
            imageLoader = SimpleImageLoader(context),  // 自定义实现
            transformer = SimpleTransformer() // 固定写法. 实现 ViewerTransitionHelper 确定 进场退场动画
        )
        SimpleViewerCustomizer().process(context, builder,list.size.toString()) // 添加自定义业务逻辑和UI处理.
//        builder.setViewerFactory(object : ImageViewerDialogFragment.Factory() {
//            override fun build() = FullScreenImageViewerDialogFragment()
//        }) // 对弹窗增加自定义内容

        return builder
    }
}

