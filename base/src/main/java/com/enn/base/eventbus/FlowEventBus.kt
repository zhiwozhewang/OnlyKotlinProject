package com.enn.base.eventbus

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStateAtLeast
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import java.util.concurrent.ConcurrentHashMap

object FlowEventBus {

    //用HashMap存储SharedFlow
    private val flowEvents = ConcurrentHashMap<String, MutableSharedFlow<Event>>()


    val sharedFlow = MutableSharedFlow<Int>(
        5 // 参数一：当新的订阅者Collect时，发送几个已经发送过的数据给它
        , 3 // 参数二：减去replay，MutableSharedFlow还缓存多少数据
        , BufferOverflow.DROP_OLDEST // 参数三：缓存策略，三种 丢掉最新值、丢掉最旧值和挂起
    )


    //获取Flow，当相应Flow不存在时创建
    fun getFlow(key: String): MutableSharedFlow<Event> {
        return flowEvents[key] ?: MutableSharedFlow<Event>().also { flowEvents[key] = it }
    }

    fun post(event: Event, delay: Long = 0) {
//        MainScope 默认运行在主线程
        MainScope().launch {
            delay(delay)
            getFlow(event.javaClass.simpleName).emit(event)
        }
    }


    //做了一点改造，加了Lifecycle.State参数可以更精细地将控制接受到事件时的执行时机
    inline fun <reified T : Event> observe(
        lifecycleOwner: LifecycleOwner,
        minState: Lifecycle.State = Lifecycle.State.RESUMED,
        dispatcher: CoroutineDispatcher = Dispatchers.Main,
        crossinline onReceived: (T) -> Unit
    ) = lifecycleOwner.lifecycleScope.launch(dispatcher) {
        getFlow(T::class.java.simpleName).collect {
//            lifecycleOwner.lifecycle.whenStateAtLeast(minState) {
            if (it is T) onReceived(it)
//            }
        }
    }

}
