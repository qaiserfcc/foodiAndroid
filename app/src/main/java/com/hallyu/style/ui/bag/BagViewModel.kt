package com.hallyu.style.ui.bag

import android.util.Log
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.hallyu.style.R
import com.hallyu.style.core.BaseViewModel
import com.hallyu.style.data.*
import com.hallyu.style.networkservice.ApiService
import com.hallyu.style.response.CartItem
import com.hallyu.style.response.CartOrders
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class BagViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val promotionRepository: PromotionRepository,
    private val userManager: UserManager,
    private val bagRepository: BagRepository,
    private val apiService: ApiService,
) : BaseViewModel() {
    val onCartOrders = bagRepository.bags
    val totalPrice = MutableLiveData(0.0)
    val promotion = promotionRepository.promotion
    val statusPromo = promotionRepository.statusPromo
    val isRemoveButton = MutableLiveData(false)
    val onUpdateCart = MutableLiveData<JsonObject?>()
    val onRemoveCart = MutableLiveData<JsonObject?>()
    val onAddToCart = MutableLiveData<Product?>()


    fun fetchBag() {
        val responseObservable: Observable<Response<CartOrders?>> = apiService.getCart()
        responseObservable.observeOn(AndroidSchedulers.mainThread())?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : DisposableObserver<Response<CartOrders?>?>() {
                override fun onNext(response: Response<CartOrders?>) {
                    if (response.isSuccessful()) {
                        onCartOrders.postValue(response.body()?.data)
                    } else {
                        Log.e("TAG", "onFailure: at " + response.errorBody())

                    }
                }

                override fun onError(e: Throwable) {
                    Log.e("TAG", "onFailure: at " + e.localizedMessage)
                }

                override fun onComplete() {}
            })
    }

    fun moveToCart(product: Product) {
        viewModelScope.launch { // Use viewModelScope for coroutines
            try {
                val request = JsonObject().apply {
                    addProperty("product_id", product.id)
                    addProperty("quantity", 1)
                }
                Log.d("TAG", "getAllProducts: Query request: $request")
                isLoading.postValue(true) // Show loading state

                val response = apiService.addToCart(request) // Make API call
                if (response.isSuccessful) {
                    onAddToCart.postValue(product)
                } else {
                    Log.e("TAG", "onFailure: ${response.errorBody()}")
                    onAddToCart.postValue(null) // Handle error
                }
            } catch (e: Exception) {
                Log.e("TAG", "onFailure: ${e.localizedMessage}")
                onAddToCart.postValue(null) // Handle exception
            } finally {
                isLoading.postValue(false) // Hide loading state
            }
        }
    }

    fun updateCart(product: CartItem) {
        viewModelScope.launch { // Use viewModelScope for coroutines
            try {
                val request = JsonObject().apply {
                    addProperty("product_id", product.product_id)
                    addProperty("quantity", product.quantity)
                }
                Log.d("TAG", "getAllProducts: Query request: $request")
                isLoading.postValue(true) // Show loading state

                val response = apiService.updateCartItem(request) // Make API call
                if (response.isSuccessful) {
                    onUpdateCart.postValue(response.body())
                } else {
                    Log.e("TAG", "onFailure: ${response.errorBody()}")
                    onUpdateCart.postValue(null) // Handle error
                }
            } catch (e: Exception) {
                Log.e("TAG", "onFailure: ${e.localizedMessage}")
                onUpdateCart.postValue(null) // Handle exception
            } finally {
                isLoading.postValue(false) // Hide loading state
            }
        }
    }
    fun clearCart() {
        viewModelScope.launch {
            isLoading.value = true
            val response = bagRepository.clearCart()
            if (response == null){
                toastMessage.postValue("Failed, Something went wrong")

            }
            isLoading.value = false
        }
    }
    fun removeCartItem(product: CartItem) {
        viewModelScope.launch { // Use viewModelScope for coroutines
            try {
                val request = JsonObject().apply {
                    addProperty("product_id", product.product_id)
                    addProperty("quantity", product.quantity)
                }
                Log.d("TAG", "getAllProducts: Query request: $request")
                isLoading.postValue(true) // Show loading state

                val response = apiService.deleteCartItem(request) // Make API call
                if (response.isSuccessful) {
                    onRemoveCart.postValue(response.body())
                } else {
                    Log.e("TAG", "onFailure: ${response.errorBody()}")
                    onRemoveCart.postValue(null) // Handle error
                }
            } catch (e: Exception) {
                Log.e("TAG", "onFailure: ${e.localizedMessage}")
                onRemoveCart.postValue(null) // Handle exception
            } finally {
                isLoading.postValue(false) // Hide loading state
            }
        }
    }


    fun calculatorTotal(lists: List<CartItem>) {
        var total = 0.0
        for (bagAndProduct in lists) {
            total += bagAndProduct.getTotalQtyPrice()
        }
        promotion.value?.let {
            total -= it.discount
        }
        totalPrice.postValue(total)
    }
    private fun changeQuantityAndCalculator(cartItem: CartItem, isPlus: Boolean = true) {
//        applyPromoCodeDiscount(cartItem.totalPrice)

//        if (isPlus){
//            val newPrice = (totalPrice.value ?: 0.0) + cartItem.product.salePrice
//            totalPrice.postValue(newPrice)
//        }else {
//            val newPrice = (totalPrice.value ?: 0.0) - cartItem.product.salePrice
//            totalPrice.postValue(newPrice)
//        }
//        getPromotion("")


    }

    fun applyPromoCodeDiscount(newTotal:Long, discount: Int) {


    }

    fun plusQuantity(bagAndProduct: CartItem, textView: TextView? = null, tvPrice: TextView? = null) {
        bagAndProduct.quantity += 1
        textView?.let {
            it.text = bagAndProduct.quantity.toString()
        }
        bagAndProduct.total_price = "${bagAndProduct.product.salePrice * bagAndProduct.quantity}"
        tvPrice?.let {
            it?.text = "${bagAndProduct.total_price} ${it.context.getString(R.string.currency)}"
        }
//        changeQuantityAndCalculator(bagAndProduct, true)
        updateCart(bagAndProduct)
    }

    fun minusQuantity(bagAndProduct: CartItem, textView: TextView, tvPrice: TextView? = null) {
        bagAndProduct.quantity -= 1
        if (bagAndProduct.quantity > 0) {
            textView?.let {
                it.text = bagAndProduct.quantity.toString()
            }
            bagAndProduct.total_price = "${bagAndProduct.product.salePrice * bagAndProduct.quantity}"
            tvPrice?.let {
                it?.text = "${bagAndProduct.total_price} ${it.context.getString(R.string.currency)}"
            }
//            changeQuantityAndCalculator(bagAndProduct, false)
            updateCart(bagAndProduct)
        }else{
            removeCartItem(bagAndProduct)
        }
    }


    fun isLogged(): Boolean {
        return userManager.isLogged()
    }

    fun getPromotion(id: String) {
        viewModelScope.launch {
            isLoading.value = true
            val response = promotionRepository.getPromotion(id)
            if (response == null) {
                toastMessage.postValue("Failed, Something went wrong")
            }

            response?.body()?.data?.let {
                promotion.postValue(it)
            }
            isLoading.value = false
            if (response?.body()?.status != 200){
                statusPromo.postValue("${response?.body()?.status}")
            }
        }
    }
}
