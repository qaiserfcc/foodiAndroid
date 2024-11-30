package com.bhub.foodi.data

import android.content.Context
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.bhub.foodi.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.JsonObject
import com.bhub.foodi.networkservice.ApiService
import com.bhub.foodi.response.CartItem
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class BagRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val userManager: UserManager,
    private val apiService: ApiService
) : BaseRepository() {
    //    val bagAndProduct = MutableLiveData<CartOrders>()
    val bags = MutableLiveData<MutableList<CartItem>>()
    val onAddToCart = MutableLiveData<JsonObject?>()
    suspend fun clearCart(): Response<JsonObject>? {
        return try {
            Log.d("BagRepository", "removeCartItem: Request:")
            apiService.clearCart()
        } catch (e: Exception) {
            Log.e("BagRepository", "removeCartItem: Error: ${e.localizedMessage}")
            null
        }
    }

    //
//    fun fetchBagAndProduct() {
//        if (userManager.isLogged()) {
//            val responseObservable: Observable<Response<CartOrders?>> = apiService.getCart()
//            responseObservable.observeOn(AndroidSchedulers.mainThread())?.subscribeOn(Schedulers.io())
//                ?.observeOn(AndroidSchedulers.mainThread())
//                ?.subscribe(object : DisposableObserver<Response<CartOrders?>?>() {
//                    override fun onNext(response: Response<CartOrders?>) {
//                        if (response.isSuccessful()) {
//                            bagAndProduct.postValue(response.body())
//                            bags.postValue(response.body()?.data)
//                        } else {
//                            Log.e("TAG", "onFailure: at " + response.errorBody())
//
//                        }
//                    }
//
//                    override fun onError(e: Throwable) {
//                        Log.e("TAG", "onFailure: at " + e.localizedMessage)
//                    }
//
//                    override fun onComplete() {}
//                })
//        }
//    }
//
//    fun getBags() {
//        if (userManager.isLogged()) {
//
//        }
//    }
//
//    suspend fun insertBag(
//        product: Product
//    ): MutableLiveData<Boolean> {
//        val isFinish = MutableLiveData(false)
//        if (userManager.isLogged()) {
//            try {
//                val request = JsonObject().apply {
//                    addProperty("product_id", product.id)
//                    addProperty("quantity", 1)
//                }
//                Log.d("TAG", "getAllProducts: Query request: $request")
//
//                val response = apiService.addToCart(request) // Make API call
//                if (response.isSuccessful) {
//                    onAddToCart.postValue(response.body())
//                } else {
//                    Log.e("TAG", "onFailure: ${response.errorBody()}")
//                    onAddToCart.postValue(null) // Handle error
//                }
//            } catch (e: Exception) {
//                Log.e("TAG", "onFailure: ${e.localizedMessage}")
//                onAddToCart.postValue(null) // Handle exception
//            } finally {
////                isLoading.postValue(false) // Hide loading state
//            }
//
//        }
//        return isFinish
//    }
//
//
//    fun removeBagItem(bag: Product) {
//        if (userManager.isLogged()) {
//            bagAndProduct.value?.let {
//                it.remove(bag)
//                bagAndProduct.postValue(it)
//            }
//
//        }
//    }
//
    fun removeAllFirebase() {
        if (userManager.isLogged()) {


        }
    }

    //
//    fun updateBagFirebase(bag: Bag, isFetch: Boolean = true) {
//        if (userManager.isLogged()) {
//
//        }
//    }
//
//
    fun calculatorTotal(lists: List<CartItem>): Double {
        var total = 0.0
        for (bagAndProduct in lists) {
            total += bagAndProduct.getTotalQtyPrice()
        }
        return total
    }
//
//
//    private fun plusQuantity(bag: Bag) {
//        bag.quantity += 1
//        updateBagFirebase(bag)
//    }

    fun setButtonBag(context: Context, buttonView: View, favorite: Product) {
        bags.value?.let {
            for (bag in it) {
                if (bag.product.id == favorite.id
                ) {
                    buttonView.background = ContextCompat.getDrawable(
                        context,
                        R.drawable.btn_bag_active
                    )
                    break
                } else {
                    buttonView.background = ContextCompat.getDrawable(
                        context,
                        R.drawable.btn_bag_no_active
                    )
                }
            }
        }
    }
}