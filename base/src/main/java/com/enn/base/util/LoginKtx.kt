package com.enn.base.util

import androidx.lifecycle.lifecycleScope
import com.enn.base.base.IUiView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import java.util.regex.Pattern

//数字
const val REG_NUMBER = ".*\\d+.*"

//小写字母
const val REG_UPPERCASE = ".*[A-Z]+.*"

//大写字母
const val REG_LOWERCASE = ".*[a-z]+.*"

//特殊符号
const val REG_SYMBOL = ".*[~!@#$%^&*()_+|<>,.?/:;'\\[\\]{}\"]+.*"

/**
 * 倒计时
 */
fun IUiView.countDownCoroutines(total: Int, onTick: (Int) -> Unit, onFinish: () -> Unit): Job {
    return flow {
        for (i in total downTo 0) {
            emit(i)
            delay(1000)
        }
    }.flowOn(Dispatchers.Default)
        .onCompletion { onFinish.invoke() }
        .onEach { onTick.invoke(it) }
        .flowOn(Dispatchers.Main)
        .launchIn(lifecycleScope)
}

/***
 * 手机号码检测
 */
fun checkPhoneNum(num: String): Boolean {
    val regExp = "^((13[0-9])|(15[^4])|(18[0-9])|(17[0-8])|(14[5-9])|(166)|(19[8,9])|)\\d{8}$"
    val p = Pattern.compile(regExp)
    val m = p.matcher(num)
    return m.matches()
}

/**
 * 长度至少minLength位,且最大长度不超过maxlength,须包含大小写字母,数字,特殊字符matchCount种以上组合
 * @param password 输入的密码
 * @param minLength 最小长度
 * @param maxLength 最大长度
 * @param matchCount 次数
 * @return 是否通过验证
 */
fun checkPwd(password: String?, minLength: Int, maxLength: Int, matchCount: Int): Boolean {

    if (password == null) {
        return false
    }
    //密码为空或者长度小于8位则返回false
    if (password.length < minLength || password.length > maxLength) return false
    var i = 0
    if (password.matches(Regex(REG_NUMBER))) i++
    if (password.matches(Regex(REG_LOWERCASE))) i++
    if (password.matches(Regex(REG_UPPERCASE))) i++
    if (password.matches(Regex(REG_SYMBOL))) i++
    return i >= matchCount
}
