package com.enn.base.eventbus

sealed class Event {
    data class ShowPayCode(val code: Int) : Event()
    data class ShowWxLoginCode(val code: String) : Event()

    //    修改个人信息后刷新
    data class SaveInfoBean<T>(val bean: T) : Event()

    //    转移主账号后刷新
    data class AfterTransfer(val isdone: Boolean) : Event()

    //    登录登出后刷新
    data class AfterLoginOrOut(val loginstate: Int) : Event()

    //    登录登出后刷新
    data class ToOtherPage(val index: Int) : Event()

    //    登录登出后刷新
    data class AfterChangeUtils(val index: Int) : Event()
    //   处理待办后刷新
    data class AfterDoneToDo(val index: Int) : Event()
}
