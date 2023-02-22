package com.enn.network.utils

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import java.nio.charset.Charset

class CustomLogInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestLog = generateRequestLog(request)
        val response = chain.proceed(request)
        val responseLog = generateResponseLog(response)
        LogUtils.w(requestLog.plus(responseLog))
        return response
    }

    private fun generateResponseLog(response: Response?): String {
        if (response == null) {
            return ""
        }
        return "Response Time-->：${
            System.currentTimeMillis()
//            TimeUtils.millis2String(System.currentTimeMillis())
        } \r\n Response Result ${
            if (response.code != 200)
                response.code
            else
                ""
        } -->：${
            getResponseText(response)
        }"
    }

    private fun generateRequestLog(request: Request?): String {
        if (request == null) {
            return ""
        }
        val requestParams = getRequestParams(request)
        val needPrintRequestParams = requestParams.contains("IsFile").not()
        return "自定义日志打印 \r\n Request Time-->：${
            System.currentTimeMillis()
//            TimeUtils.millis2String(System.currentTimeMillis())
        } \r\n Request Url-->：${request.method} ${request.url} \r\n Request Header-->：${
            getRequestHeaders(
                request
            )
        } \r\n Request Parameters-->：${
            if (needPrintRequestParams)
                requestParams
            else
                "文件上传，不打印请求参数"
        } \r\n "
    }

    @Deprecated("unused")
    private fun printInfo(request: Request?, response: Response?) {
        if (request != null && response != null) {
            val requestParams = getRequestParams(request)
            val needPrintRequestParams = requestParams.contains("IsFile").not()
            val logInfo =
                "自定义日志打印 \r\n Request Url-->：${request.method} ${request.url} \r\n Request Header-->：${
                    getRequestHeaders(
                        request
                    )
                } \r\n Request Parameters-->：${
                    if (needPrintRequestParams)
                        requestParams
                    else
                        "文件上传，不打印请求参数"
                } \r\n Response Result ${
                    if (response.code != 200)
                        response.code
                    else
                        ""
                } -->：${
                    getResponseText(response)
                }"
            LogUtils.w(logInfo)
        }
    }

    /**
     * 获取请求参数
     */
    private fun getRequestParams(request: Request): String {
        var str: String? = null
        try {
            request.body?.let {
                val buffer = Buffer()
                it.writeTo(buffer)
                val charset = it.contentType()?.charset(Charset.forName("UTF-8"))
                    ?: Charset.forName("UTF-8")
                str = buffer.readString(charset)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return if (str.isNullOrEmpty()) "Empty!" else str!!
    }

    private fun getRequestHeaders(request: Request): String {
        val headers = request.headers
        return if (headers.size > 0) {
            headers.toString()
        } else {
            "Empty!"
        }
    }

    /**
     * 获取返回数据字符串
     */
    private fun getResponseText(response: Response): String {
        try {
            response.body?.let {
                val source = it.source()
                source.request(Long.MAX_VALUE)
                val buffer = source.buffer
                val charset = it.contentType()?.charset(Charset.forName("UTF-8"))
                    ?: Charset.forName("UTF-8")
                if (it.contentLength().toInt() != 0) {
                    buffer.clone().readString(charset).let { result ->
                        return result
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return "Empty!"
    }
}
