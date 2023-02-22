package com.enn.network.utils

import android.util.Log
import okhttp3.logging.HttpLoggingInterceptor
import java.util.logging.Logger

class HttpLogger : HttpLoggingInterceptor.Logger {

    private val mMessage: StringBuilder by lazy { StringBuilder() }


    override fun log(message0: String) {
        // 请求或者响应开始
        var message = message0
        if (message.startsWith("--> ")) {
            mMessage.setLength(0)
        }
        // 以{}或者[]形式的说明是响应结果的json数据，需要进行格式化
        if (message.startsWith("{") && message.endsWith("}")
            || message.startsWith("[") && message.endsWith("]")
        ) {
            message = JsonUtil.formatJson(JsonUtil.decodeUnicode(message)).toString()
        }
        mMessage.append(message.trimIndent())
        // 响应结束，打印整条日志
        if (message.startsWith("<-- END HTTP")) {
            LogUtils.e(mMessage.toString())
        }
    }


}
