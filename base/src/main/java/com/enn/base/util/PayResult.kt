package com.enn.base.util

import android.text.TextUtils

class PayResult(rawResult: Map<String, String>) {
    /**
     * @return the resultStatus
     */
    var resultStatus: String? = null

    /**
     * @return the result
     */
    var result: String? = null

    /**
     * @return the memo
     */
    var memo: String? = null
    override fun toString(): String {
        return ("resultStatus={" + resultStatus + "};memo={" + memo
                + "};result={" + result + "}")
    }

    init {

        rawResult?.keys?.forEach { key ->
            if (TextUtils.equals(key, "resultStatus")) {
                resultStatus = rawResult[key]
            } else if (TextUtils.equals(key, "result")) {
                result = rawResult[key]
            } else if (TextUtils.equals(key, "memo")) {
                memo = rawResult[key]
            }
        }
    }
}