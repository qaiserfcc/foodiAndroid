package com.hallyu.style.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.hallyu.style.ui.promotion.FilterPromotion
import com.hallyu.style.utilities.PROMOTION_FIREBASE
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.gson.JsonObject
import com.hallyu.style.networkservice.ApiResponse
import com.hallyu.style.networkservice.ApiService
import retrofit2.Response
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PromotionRepository @Inject constructor(
    private val db: FirebaseFirestore, private val apiService: ApiService
) {
    val promotions = MutableLiveData<List<Promotion>>()
    val promotion = MutableLiveData<Promotion>()
    val statusPromo = MutableLiveData<String>()

//    fun fetchData() {
//        db.collection(PROMOTION_FIREBASE)
//            .get()
//            .addOnSuccessListener { documents ->
//                val list = mutableListOf<Promotion>()
//                for (document in documents) {
//                    val promotion = document.toObject<Promotion>()
//                    promotion.endDate?.let {
//                        if (it.time - Date().time > 0) {
//                            list.add(document.toObject())
//                        }
//                    }
//                }
//                promotions.postValue(list)
//            }
//    }

    suspend fun getPromotion(coupon: String): Response<ApiResponse<Promotion>>? {
        return try {
//            if (coupon.isBlank()) {
//                promotion.postValue(Promotion())
//            }
            val request = JsonObject()
            request.addProperty("coupon", coupon)
            Log.d("PromotionRepository", "getPromotion: Request: $request")
            apiService.applyCoupon(request)
        } catch (e: Exception) {
            Log.e("BagRepository", "removeCartItem: Error: ${e.localizedMessage}")
            null
        }
    }

//    fun getPromotion(coupon: String) {
//        if (coupon.isBlank()) {
//            promotion.postValue(Promotion())
//        }
//
//        db.collection(PROMOTION_FIREBASE).document(coupon).get()
//            .addOnSuccessListener { document ->
//                if (document.exists() && document != null) {
//                    promotion.postValue(document.toObject())
//                }
//            }
//    }
}