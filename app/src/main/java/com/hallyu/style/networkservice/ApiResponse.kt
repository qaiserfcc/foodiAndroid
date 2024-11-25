package com.hallyu.style.networkservice

import retrofit2.Response
data class ApiResponse<T>(
    val status: Int,
    val message: String?,
    val data: T
){
    fun isSuccessful(): Boolean {
        return status == 200
    }

    fun errorBody(): String? {
        return message
    }

}