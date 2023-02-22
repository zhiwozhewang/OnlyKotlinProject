package com.enn.ionic.js.viewer

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.nio.file.Files
import kotlin.Throws

object SaveUtils {
    private const val TAG = "SaveUtils"

    /**
     * 将图片保存到系统相册
     */
    fun saveImgToAlbum(context: Context, imageFile: String): Boolean {
        Log.d(TAG, "saveImgToAlbum() imageFile = [$imageFile]")
        return try {
            val localContentResolver = context.contentResolver
            val tempFile = File(imageFile)
            val localContentValues = getImageContentValues(tempFile, System.currentTimeMillis())
            val uri = localContentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                localContentValues
            )
            copyFileAfterQ(context, localContentResolver, tempFile, uri)
            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * 获取图片的ContentValue
     */
    fun getImageContentValues(paramFile: File, timestamp: Long): ContentValues {
        val localContentValues = ContentValues()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            localContentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/Camera")
        }
        localContentValues.put(MediaStore.Images.Media.TITLE, paramFile.name)
        localContentValues.put(MediaStore.Images.Media.DISPLAY_NAME, paramFile.name)
        localContentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        localContentValues.put(MediaStore.Images.Media.DATE_TAKEN, timestamp)
        localContentValues.put(MediaStore.Images.Media.DATE_MODIFIED, timestamp)
        localContentValues.put(MediaStore.Images.Media.DATE_ADDED, timestamp)
        localContentValues.put(MediaStore.Images.Media.ORIENTATION, 0)
        localContentValues.put(MediaStore.Images.Media.DATA, paramFile.absolutePath)
        localContentValues.put(MediaStore.Images.Media.SIZE, paramFile.length())
        return localContentValues
    }

    /**
     * 将视频保存到系统相册
     */
    fun saveVideoToAlbum(context: Context, videoFile: String): Boolean {
        Log.d(TAG, "saveVideoToAlbum() videoFile = [$videoFile]")
        return try {
            val localContentResolver = context.contentResolver
            val tempFile = File(videoFile)
            val localContentValues = getVideoContentValues(tempFile, System.currentTimeMillis())
            val localUri = localContentResolver.insert(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                localContentValues
            )
            copyFileAfterQ(context, localContentResolver, tempFile, localUri)
            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    @Throws(IOException::class)
    private fun copyFileAfterQ(
        context: Context,
        localContentResolver: ContentResolver,
        tempFile: File,
        localUri: Uri?
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            context.applicationInfo.targetSdkVersion >= Build.VERSION_CODES.Q
        ) {
            //拷贝文件到相册的uri,android11及以上得这么干，否则不会显示。可以参考ScreenMediaRecorder的save方法
            val os = localContentResolver.openOutputStream(localUri!!, "w")
            Files.copy(tempFile.toPath(), os)
            os!!.close()
            tempFile.deleteOnExit()
        }
    }

    /**
     * 获取视频的contentValue
     */
    fun getVideoContentValues(paramFile: File, timestamp: Long): ContentValues {
        val localContentValues = ContentValues()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            localContentValues.put(MediaStore.Video.Media.RELATIVE_PATH, "DCIM/Camera")
        }
        localContentValues.put(MediaStore.Video.Media.TITLE, paramFile.name)
        localContentValues.put(MediaStore.Video.Media.DISPLAY_NAME, paramFile.name)
        localContentValues.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        localContentValues.put(MediaStore.Video.Media.DATE_TAKEN, timestamp)
        localContentValues.put(MediaStore.Video.Media.DATE_MODIFIED, timestamp)
        localContentValues.put(MediaStore.Video.Media.DATE_ADDED, timestamp)
        localContentValues.put(MediaStore.Video.Media.DATA, paramFile.absolutePath)
        localContentValues.put(MediaStore.Video.Media.SIZE, paramFile.length())
        return localContentValues
    }
}