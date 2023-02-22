package com.enn.base.util

import com.enn.base.data.WebBaseBean
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 *
 * 参考文章：https://juejin.cn/post/7049983952496361503
 */
open class JsonUtils() {

    /**
     * fromJson2List
     */
    inline fun <reified T> fromJson2List(json: String) = fromJson<List<T>>(json)

    /**
     * fromJson
     */
    inline fun <reified T> fromJson(json: String): T? {
        return try {
            val type = object : TypeToken<T>() {}.type
            return Gson().fromJson(json, type)
        } catch (e: Exception) {
            println("try exception,${e.message}")
            null
        }
    }
}