package com.enn.ionic.vm

import com.enn.network.toast
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SuspendTest {
    suspend fun fooSuspend(): Int {
        println("In Coroutine")
        delay(500) // suspend 调用1
        delay(1000) // suspend 调用2
        return 5
    }

    fun doFun() {
        MainScope().launch {
            fooSuspend()
            toast("作用域内")
        }
        toast("作用域外")

    }

}