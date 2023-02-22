package com.enn.ionic.bean

data class WeatherBean(
    val province: String,
    val city: String,
    val day: String,
    val hour: String,
    val temperature: String,
    val winddirection: String,
    val windpower: String,
    val reporttime: String,
    val humidity: String,
    val weather: String
) {
    val weatherDes: String
        get() {
            return when (weather) {
                "duoyun" -> "云"
                "qing" -> "晴"
                "yin" -> "阴"
                "yujiaxue" -> "雨雪天气"
                "wu", "mai" -> "雾霾"
                "xiaoxue", "daxue", "zhongxue", "baoxue" -> "雪"
                "yangsha", "shachenbao" -> "沙尘"
                "leizhenyu", "zhenyu", "xiaoyu", "zhongyu", "dayu", "baoyu", "dabaoyu" -> "雨"
                "qingjianduoyun" -> "晴间多云"
                else -> ""
            }
        }
}