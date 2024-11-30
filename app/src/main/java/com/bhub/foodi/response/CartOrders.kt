package com.bhub.foodi.response

import com.bhub.foodi.data.Product


data class CartOrders(
    val status: Int, val data: ArrayList<CartItem>
)

data class CartItem(
    val id: Int = 0,
    val user_id: Int = 0,
    val product_id: Int = 0,
    var quantity: Int = 0,
    var total_price: String = "",
    val created_at: String = "",
    val updated_at: String = "",
    val product: Product = Product()
) {
    fun getTotalQtyPrice(): Double {
        //this total is already multiplied with quantity
        return total_price.toDouble() ?: 0.0
    }
}