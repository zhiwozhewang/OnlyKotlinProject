package com.enn.ionic.download

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.ResponseBody
import retrofit2.Retrofit
import java.io.File
import java.io.IOException

object DownloadManager {

    suspend fun download(url: String, file: File): Flow<DownloadState> {
        return flow {
            val retrofit = Retrofit.Builder()
                .baseUrl(UrlUtils.getBaseUrl(url))
                .build()
            val response = retrofit.create(DownloadService::class.java).download(url).execute()
            if (response.isSuccessful) {
                saveToFile(response.body()!!, file) {
                    emit(DownloadState.InProgress(it))
                }
                emit(DownloadState.Success(file))
            } else {
                emit(DownloadState.Error(IOException(response.toString())))
            }
        }.catch {
            emit(DownloadState.Error(it))
        }.flowOn(Dispatchers.IO)
    }

    private inline fun saveToFile(
        responseBody: ResponseBody,
        file: File,
        progressListener: (Int) -> Unit
    ) {
        val total = responseBody.contentLength()
        var bytesCopied = 0L
        var emittedProgress = 0
        file.outputStream().use { output ->
            val input = responseBody.byteStream()
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var bytes = input.read(buffer)
            while (bytes >= 0) {
                output.write(buffer, 0, bytes)
                bytesCopied += bytes
                bytes = input.read(buffer)
                val progress = (bytesCopied * 100 / total).toInt()
                if (progress - emittedProgress > 0) {
                    progressListener(progress)
                    emittedProgress = progress
                }
            }
        }
    }
}
