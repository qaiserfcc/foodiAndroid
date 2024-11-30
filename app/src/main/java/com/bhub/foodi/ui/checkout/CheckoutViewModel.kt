package com.bhub.foodi.ui.checkout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.bhub.foodi.core.BaseViewModel
import com.bhub.foodi.data.*
import com.bhub.foodi.response.CartItem
import com.bhub.foodi.response.OptionsData
import com.bhub.foodi.utilities.MAX
import com.bhub.foodi.utilities.MIN
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val shippingAddressRepository: ShippingAddressRepository,
    private val paymentRepository: PaymentRepository,
    private val orderRepository: OrderRepository,
    private val bagRepository: BagRepository,
    private val promotionRepository: PromotionRepository,
    private val userManager: UserManager,
    private val db: FirebaseFirestore,
) : BaseViewModel() {
    private val statusIdPayment = MutableStateFlow("")
    val bag = bagRepository.bags
    val shippingAddress = shippingAddressRepository.address
    val promotion = promotionRepository.promotion
    val orderOptions = MutableLiveData<OptionsData>()
    val result = orderRepository.result
    val phoneVerified = orderRepository.phoneVerified
    val otpSent = orderRepository.otpSent

    init {

    }

    val payment = paymentRepository.card


    fun getPhone(): String {
        return userManager.getPhone()
    }

    fun setPhone(phone: String) {
        val user = userManager.getUser()
        user.phone = phone
        userManager.setPhone(phone)
        userManager.writeProfile(db, user)
    }

    fun getOrderOptions() {
        viewModelScope.launch {
            isLoading.value = true
            val response = orderRepository.getOptions()
            if (response == null) {
                toastMessage.postValue("Failed, Something went wrong")
            }
            response?.body()?.data?.let {
                orderOptions.postValue(it)
            }
            isLoading.value = false
        }
    }

    private fun getPayment(idPayment: String): LiveData<Card> {
        val result = paymentRepository.card
        if (idPayment.isNotBlank()) {
            paymentRepository.getCard(idPayment)
        } else {
            paymentRepository.card.postValue(Card())
        }
        return result
    }

    fun getPromotion(idPromotion: String): LiveData<Promotion> {
        val result = promotionRepository.promotion
        if (idPromotion.isNotBlank()) {
//            promotionRepository.getPromotion(idPromotion)
        }
        return result
    }

    fun setPromotionDefault() {
        promotionRepository.promotion.postValue(Promotion())
    }

    fun setOrderFirebase(order: OrderRequest): MutableLiveData<Boolean> {

        viewModelScope.launch {
            orderRepository.setOrderCheckout(order)
            isLoading.postValue(false) // Update loading state after the operation
        }
        isLoading.postValue(true) // Set loading state before starting the operation
        return result
    }

    fun sentOtpVerify(phone: String, otp: String? = null): MutableLiveData<Boolean> {
        val regex = Regex("^(?:50|51|52|55|56|2|3|4|6|7|9)\\d{7}\$")
        if (!phone.matches(regex)) {
            toastMessage.postValue("Invalid phone number, 5******** is allowed")
            return MutableLiveData()
        }
        viewModelScope.launch {
            orderRepository.verifyPhone(phone)
            isLoading.postValue(false) // Update loading state after the operation
        }
        isLoading.postValue(true) // Set loading state before starting the operation
        return otpSent
    }

    fun verifyOTP(phone: String, otp: String): MutableLiveData<Boolean> {

        viewModelScope.launch {
            orderRepository.verifyOtp(phone, otp)
            isLoading.postValue(false)
        }
        isLoading.postValue(true)
        return phoneVerified
    }

    fun checkOrder(
        bags: List<CartItem>,
        address: ShippingAddress?,
        card: Card?,
        total: Double,
        delivery: Delivery?,
        promotion: Promotion?
    ): MutableLiveData<OrderRequest> {
        if (getPhone().isBlank()) {
            toastMessage.postValue("Please enter your phone number")
            return MutableLiveData()
        } else if (phoneVerified.value == false) {
            toastMessage.postValue("Please enter correct OTP")
            return MutableLiveData()
        }
        if (address == null || address.fullName.isBlank()) {
            toastMessage.postValue(ALERT_ADDRESS)
            return MutableLiveData()
        }
//        if (card == null || card.id.isBlank()) {
//            toastMessage.postValue(ALERT_PAYMENT)
//            return MutableLiveData()
//        }
        if (delivery == null) {
            toastMessage.postValue(ALERT_DELIVERY)
            return MutableLiveData()
        }
        isLoading.postValue(true)
//        val listBag = getBag(bags)
//        val numberCard = getNumberCard(card)
        val order = createOrder(
            total,
            address,
            "Cash On Delivery",
            0,
            delivery,
            promotion,
            bags
        )
        orderRepository.setOrderFirebase(order)

        return MutableLiveData(orderCreate(order))
    }

    private fun createOrder(
        total: Double,
        shippingAddress: ShippingAddress,
        payment: String,
        typePayment: Int,
        delivery: Delivery,
        promotion: Promotion?,
        bags: List<CartItem>?,
    ) = Order(
        id = (MIN..MAX).random().toString(),
        trackingNumber = (MIN..MAX).random().toString(),
        total = total,
        status = 1,
        timeCreated = Date(),
        shippingAddress = shippingAddress,
        payment = payment,
        isTypePayment = typePayment,
        delivery = delivery,
        promotion = promotion,
        bags = bags
    )

    fun orderCreate(order: Order): OrderRequest {
        var totalqty = 0
        order.bags?.forEach {
            totalqty += it.quantity
        }
        return OrderRequest(
            personal_name = userManager.getName(),
            personal_email = userManager.getEmail(),
            shipping = "shipto",
            pickup_location = "",
            customer_name = userManager.getName(),
            customer_email = userManager.getEmail(),
            customer_phone = userManager.getPhone(),
            customer_address = order.shippingAddress?.address ?: "",
            customer_city = order?.shippingAddress?.city ?: "",
            customer_zip = "000000",
            customer_country = order.shippingAddress?.country ?: "",
            customer_state = order.shippingAddress?.state ?: "",
            shipping_name = userManager.getName(),
            shipping_email = userManager.getEmail(),
            shipping_phone = userManager.getPhone(),
            shipping_address = order.shippingAddress?.address ?: "",
            shipping_zip = "00000",
            shipping_city = order.shippingAddress?.city ?: "",
            shipping_state = order.shippingAddress?.state ?: "",
            order_notes = "",
            method = order.payment,
            shipping_cost = order.delivery?.price ?: 0,
            shipping_title = order.delivery?.title ?: "",
            packing_cost = 0,
            packing_title = "",
            totalQty = totalqty,
            pay_amount = order.total,
            vendor_shipping_id = 0,
            vendor_packing_id = 0,
            currency_sign = "AED",
            currency_name = "AED",
            currency_value = 1,
            coupon_code = order.promotion?.code ?: "",
            coupon_discount = order.promotion?.discount.toString() ?: "",
            coupon_id = "",
            user_id = userManager.getRole(),
            dp = 0,
            tax = 0,
            wallet_price = 0,
            tax_type = "state_tax"
        )
    }

    private fun getBag(bagAndProducts: List<BagAndProduct>): MutableList<ProductOrder> {
        val list: MutableList<ProductOrder> = mutableListOf()
        for (bagAndProduct in bagAndProducts) {
            bagAndProduct.apply {
                val size = product.getColorAndSize(
                    bag.color,
                    bag.size
                )
                var price: Long = 0
                size?.let {
                    var salePercent = 0
                    if (product.salePercent != null) {
                        salePercent = product.salePercent
                    }
                    price = size.price * (100 - salePercent) / 100
                }

                list.add(
                    ProductOrder(
                        idProduct = product.id,
                        image = product.getThumbnails(),
                        title = product.title,
                        brandName = product.brandName,
                        size = bag.size,
                        color = bag.color,
                        units = bag.quantity.toInt(),
                        price = price.toFloat(),
                    )
                )
            }
        }
        return list
    }

    private fun getAddress(shippingAddress: ShippingAddress): String {
        shippingAddress.apply {
            return "$address\n$city, $state $zipCode, $country"
        }
    }

    private fun getNumberCard(card: Card): Pair<String, Int> {
        val type = if (card.number[0] == '4') {
            0
        } else {
            1
        }
        val number = card.number.substring(card.number.length - 4)
        return Pair(number, type)
    }


    fun setIdAddressDefault() {
        if (userManager.getAddress().isNotBlank()) {
            shippingAddressRepository.getAddress(userManager.getAddress())
        } else {
            shippingAddressRepository.address.postValue(ShippingAddress())
        }
    }

    fun setIdPaymentDefault() {
        statusIdPayment.value = userManager.getPayment()
        paymentRepository.insertCard("COD", "COD", "expertDate", "cvv", true)
    }

    fun calculatorTotalOrder(list: List<CartItem>, discount: Double = 0.0): Double {
        val total = (calculatorTotal(list) - discount)
        return total
    }

    fun calculatorTotal(lists: List<CartItem>): Double {
        var total = 0.0
        for (bagAndProduct in lists) {
            total += bagAndProduct.getTotalQtyPrice()
        }
        return total
    }

    companion object {
        const val ALERT_DELIVERY = "Please choose one delivery"
        const val ALERT_ADDRESS = "Please choose one address"
        const val ALERT_PAYMENT = "Please choose one payment"
    }
}