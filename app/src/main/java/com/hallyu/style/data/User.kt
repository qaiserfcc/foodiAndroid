package com.hallyu.style.data

import com.hallyu.style.utilities.DateFormat

class User(
    var name: String = "",
    val email: String = "",
    var password: String = "",
    val token: String = "",
    var dob: String = "",
    var avatar: String = "",
    var defaultAddress: String = "",
    var defaultPayment: String = "",
    val id: String = "0",
    var phone: String = "",
)
data class UserData(
    val id: Int,
    val name: String?,
    val photo: String?,
    val email: String?,
    val email_verified: String?,
    val ban: Int?,
    val current_balance: Int?,
    val balance: Int?,
    val reward: Int?,
    val address: String?,
    val phone: String?,
    val zip: String?,
    val city: String?,
    val country: String?,
    val state: String?,
    val fax: String?,
    val created_at: String?,
    val updated_at: String?,
    val is_provider: Int?,
    val status: Int?,
    val verification_link: String?,
    val affilate_code: String?,
    val affilate_income: Int?,
    val shop_name: String?,
    val owner_name: String?,
    val shop_number: String?,
    val shop_address: String?,
    val reg_number: String?,
    val shop_message: String?,
    val shop_details: String?,
    val shop_image: String?,
    val f_url: String?,
    val g_url: String?,
    val t_url: String?,
    val l_url: String?,
    val is_vendor: Int?,
    val f_check: Int?,
    val g_check: Int?,
    val t_check: Int?,
    val l_check: Int?,
    val mail_sent: Int?,
    val shipping_cost: Int?,
    val date: String?,
    val email_token: String?,
//    val orders: List<OldOrder>?
)

data class AuthResponse(
    val token: String,
    val user: UserData
)

