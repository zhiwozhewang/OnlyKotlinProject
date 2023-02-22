package com.enn.ionic.bean

import java.io.Serializable

data class AccountBean(
    val isMaster: Int,
    val passNumOfOrdinary: Int,
    val waitingPassNumOfOrdinary: Int
) : Serializable