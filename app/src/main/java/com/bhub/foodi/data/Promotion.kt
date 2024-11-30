package com.bhub.foodi.data

data class Promotion(
    val oldTotal: String = "",
    val newTotal: Long = 0,
    val code: String? = null,
    val value: String = "None",
    val discount: Int = 0,
)
