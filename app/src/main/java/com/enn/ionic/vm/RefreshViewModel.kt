package com.enn.ionic.vm

import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.enn.base.base.BaseViewModel
import com.enn.ionic.bean.*
import com.enn.ionic.net.LoginRepository
import com.enn.ionic.net.RefreshInfoRepository
import com.enn.ionic.net.RetrofitClient
import com.tencent.mm.opensdk.modelmsg.SendAuth
import kotlinx.coroutines.launch
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Query


open class RefreshViewModel<T : RefreshInfoRepository> : BaseViewModel() {


    val repository: T
        get() = new<RefreshInfoRepository>("as") as T

    /**
     *  解绑后刷新状态
     */
    fun toRefreshInfo() {

        viewModelScope.launch {
            repository.refreshInfo()
//            FlowEventBus.post(Event.AfterLoginOrOut(0))
        }
    }

    /**
     *  实例化泛型  含参
     */
    inline fun <reified T : Any> new(vararg params: Any): T {
        val clz = T::class.java
        val paramTypes = params.map { it::class.java }.toTypedArray()
        val mCreate = clz.getDeclaredConstructor(*paramTypes)
        mCreate.isAccessible = true
        return mCreate.newInstance(* params)
    }
}