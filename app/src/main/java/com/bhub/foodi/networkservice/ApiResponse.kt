package com.bhub.foodi.networkservice

data class ApiResponse<T>(
    val status: Int,
    val message: String?,
    val data: T
) {
    fun isSuccessful(): Boolean {
        return status == 200
    }

    fun errorBody(): String? {
        return message
    }

}