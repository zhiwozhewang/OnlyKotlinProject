package com.enn.base.util

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import java.text.ParsePosition
import java.text.SimpleDateFormat

object TimestampUtils {
    /**
     * Timestamp to String
     *  SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
     * @param Timestamp
     * @return String
     */
    @SuppressLint("SimpleDateFormat")
    fun transToString(time: Long): String {
        return SimpleDateFormat("yyyy-MM-dd").format(time)
    }

    /**
     * String to Timestamp
     * @param String
     * @return Timestamp
     */
    fun transToTimeStamp(date: String): Long {
        return SimpleDateFormat("YY-MM-DD-hh-mm-ss").parse(date, ParsePosition(0)).time
    }
}
