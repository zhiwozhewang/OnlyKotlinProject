package com.enn.network.entity

import com.google.gson.annotations.SerializedName
import java.io.Serializable

open class ApiResponse<T>(
    @SerializedName("data", alternate = ["result"])
    open var result: T? = null,
    @SerializedName("code", alternate = ["errorCode"])
    open val errorCode: Int? = null,
    open val message: String? = null,
    open val error: Throwable? = null,
//    val success: Boolean? = null
) : Serializable {
    val isSuccess: Boolean
        get() = errorCode == 0

    override fun toString(): String {
        return "ApiResponse(result=$result, code=$errorCode, message=$message, error=$error)"
    }


}

data class ApiSuccessResponse<T>(val response: T) : ApiResponse<T>(result = response)

class ApiEmptyResponse<T> : ApiResponse<T>()

data class ApiFailedResponse<T>(override val errorCode: Int?, override val message: String?) :
    ApiResponse<T>(errorCode = errorCode, message = message)

data class ApiErrorResponse<T>(val throwable: Throwable) : ApiResponse<T>(error = throwable)
