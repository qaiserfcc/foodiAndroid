package com.hallyu.style.data

import com.hallyu.style.response.CartItem
import com.hallyu.style.utilities.MAX
import com.hallyu.style.utilities.MIN
import com.hallyu.style.utilities.TRACKING_NUMBER
import java.util.*

data class Order(
    val id: String = (MIN..MAX).random().toString(),
        var products: List<ProductOrder> = emptyList(),
    var trackingNumber: String = TRACKING_NUMBER,
    var total: Double = 0.0,
    var status: Int = 0,
    var timeCreated: Date = Date(),
    var shippingAddress: ShippingAddress? = null,
    var payment: String = "",
    var isTypePayment: Int = 0,
    var delivery: Delivery? = null,
    var promotion: Promotion? = null,
    var bags: List<CartItem>? = null
) {
    fun getUnits(): Int {
        return bags?.let {
            var result = 0
            for (product in it) {
                result += product.quantity
            }
            result
        } ?: 0
    }

    fun convertNewOrderToOrder(newOrder: OldOrder): Order {
        return Order(
            id = newOrder.id.toString(),
            products = emptyList(), // Needs to be mapped from newOrder.cart or other sources
            trackingNumber = newOrder.order_number,
            total = newOrder.pay_amount.toDouble(),
            status = newOrder.status.toIntOrNull() ?: 0,
            timeCreated = Date(), // Needs proper conversion from newOrder.created_at
            shippingAddress = newOrder.customer_address.takeIf { it.isNotEmpty() }?.let {
                ShippingAddress(
                    address = newOrder.customer_address,
                    city = newOrder.customer_city,
                    zipCode = newOrder.customer_zip
                )
            },
            payment = newOrder.method,
            isTypePayment = 0, // Needs proper mapping
            delivery = newOrder.shipping_address.takeIf { it != null }?.let {
                Delivery(
                    title = "${newOrder.shipping_address}${newOrder.shipping_city}",
                    subtitle = "${newOrder.shipping_state }",
                    price = newOrder.shipping_cost
                )
            },
            promotion = newOrder.coupon_code?.let {
                Promotion(
                    code = it,
                    discount = newOrder.coupon_discount?.toIntOrNull()?:0
                )
            },
            bags = emptyList() // Needs to be mapped from newOrder.cart or other sources
        )
    }
}

data class OrderRequest(
    val personal_name: String,
    val personal_email: String,
    val shipping: String,
    val pickup_location: String,
    val customer_name: String,
    val customer_email: String,
    val customer_phone: String,
    val customer_address: String,
    val customer_city: String,
    val customer_zip: String,
    val customer_country: String,
    val customer_state: String,
    val shipping_name: String,
    val shipping_email: String,
    val shipping_phone: String,
    val shipping_address: String,
    val shipping_zip: String,
    val shipping_city: String,
    val shipping_state: String,
    val order_notes: String,
    val method: String,
    val shipping_cost: Int,
    val packing_cost: Int,
    val shipping_title: String,
    val packing_title: String,
    val totalQty: Int,
    val pay_amount: Double,
    val vendor_shipping_id: Int,
    val vendor_packing_id: Int,
    val currency_sign: String,
    val currency_name: String,
    val currency_value: Int,
    val coupon_code: String,
    val coupon_discount: String,
    val coupon_id: String,
    val user_id: String,
    val dp: Int,
    val tax: Int,
    val wallet_price: Int,
    val tax_type: String
)




