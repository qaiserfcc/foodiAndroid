package com.bhub.foodi.data

data class ShippingAddress(
    val id: Long = 0,
    var fullName: String = "",
    var address: String = "",
    var city: String = "",
    var state: String = "",
    var zipCode: String = "",
    var country: String = "",
)
