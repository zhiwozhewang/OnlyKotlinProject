package com.enn.ionic.js.viewer

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.imageLoader
import coil.request.ImageRequest
import com.enn.base.base.IUiView
import com.enn.ionic.download.toDownLoad
import com.github.iielse.imageviewer.core.ImageLoader
import com.github.iielse.imageviewer.core.Photo
import com.luck.picture.lib.utils.ActivityCompatHelper
import java.io.File

class SimpleImageLoader(val context: Activity) : ImageLoader {
    override fun load(view: ImageView, data: Photo, viewHolder: RecyclerView.ViewHolder) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        val it = (data as? MyData?)?.url ?: return

        var target: ImageRequest? = null

        if (it.contains("downPdf")) {
            (context as IUiView).toDownLoad<Any>(it, context.cacheDir, "${File(it).name}.pdf") {
                onSuccess = { file ->
                    target = ImageRequest.Builder(context)
                        .data(getBitmap(file))
                        .target(view)
                        .build()

                    target?.let {
                        context.imageLoader.enqueue(it)
                    }
                }
            }
        } else {
            target = ImageRequest.Builder(context)
                .data(it)
                .target(view)
                .build()

            target?.let {
                context.imageLoader.enqueue(it)
            }
        }
    }

    private fun getBitmap(file: File): Bitmap {
        val parcelFileDescriptor =
            ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        val pdfRenderer = PdfRenderer(parcelFileDescriptor)
        val page = pdfRenderer.openPage(0)

        val width = context.resources.displayMetrics.densityDpi * page.width / 72
        val height = context.resources.displayMetrics.densityDpi * page.height / 72

        val bitmap = Bitmap.createBitmap(
            width,
            height,
            Bitmap.Config.ARGB_8888
        );
//        val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        val r = Rect(0, 0, width, height)

        page.render(bitmap, r, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        page.close()
        return bitmap
    }
}
