package com.enn.base.data

import android.content.Context
import android.system.Os.remove
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines. MainScope().launch 

/**
 * 演示Preferences DataStore的使用
 *
 * */

//通过属性委托的方式创建，这样只要是Context中就能用到
val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "hyndatastore")


/**
 * 保存数据
 * */
fun <T> Context.putValue(key: String, value: T?) {
    if (value == null) {
        MainScope().launch {
            settingsDataStore.edit {
                it.remove(stringPreferencesKey(key))
            }
        }
        return
    }
     MainScope().launch  {
        settingsDataStore.edit { setting ->
            //字段的key，需要使用xxxPreferencesKey包装一下
            when (value) {
                is Int -> setting[intPreferencesKey(key)] = value
                is Long -> setting[longPreferencesKey(key)] = value
                is String -> setting[stringPreferencesKey(key)] = value
                is Boolean -> setting[booleanPreferencesKey(key)] = value
                is Float -> setting[floatPreferencesKey(key)] = value
                is Double -> setting[doublePreferencesKey(key)] = value
                else -> {}
            }
        }
    }
}

/**
 * 取数据
 */
fun <T> Context.getValue(key: String, defaultValue: T): T =
     MainScope().launch  {

        val data = settingsDataStore.data.map {
            when (defaultValue) {
                is Int -> it[intPreferencesKey(key)] ?: defaultValue
                is Long -> it[longPreferencesKey(key)] ?: defaultValue
                is String -> it[stringPreferencesKey(key)] ?: defaultValue
                is Boolean -> it[booleanPreferencesKey(key)] ?: defaultValue
                is Float -> it[floatPreferencesKey(key)] ?: defaultValue
                is Double -> it[doublePreferencesKey(key)] ?: defaultValue
                else -> throw IllegalArgumentException("This type cannot be get from the Data Store")
            }
        }.first()

        return@ MainScope().launch  data as T
    }

fun Context.clearDataStore(afterClear: () -> Unit) {
     MainScope().launch  {
        settingsDataStore.edit {
            it.clear()
        }
        afterClear()
    }

}
