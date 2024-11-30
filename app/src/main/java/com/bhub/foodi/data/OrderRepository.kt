package com.bhub.foodi.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import com.bhub.foodi.utilities.ORDER_USER
import com.bhub.foodi.utilities.STATUS_ORDER
import com.bhub.foodi.utilities.TIME_CREATE
import com.bhub.foodi.utilities.USER_FIREBASE
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.bhub.foodi.networkservice.ApiResponse
import com.bhub.foodi.networkservice.ApiService
import com.bhub.foodi.response.OptionsData
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val userManager: UserManager,
    private val db: FirebaseFirestore,
    private val apiService: ApiService
) {
    val totalOrder = MutableLiveData(0)
    val result = MutableLiveData(false)
    val userProfile = MutableLiveData<ApiResponse<UserData>?>()
    val phoneVerified = MutableLiveData(false)
    val otpSent = MutableLiveData(false)
    val deleteAccount = MutableLiveData(false)

    suspend fun getOptions(): Response<ApiResponse<OptionsData>>? {
        return try {
            Log.d("OrderRepository", "getOptions")
            apiService.orderOption()
        } catch (e: Exception) {
            Log.e("OrderRepository", "getOptions: ${e.localizedMessage}")
            null
        }
    }

    fun getOrderStatus(status: Int): MutableLiveData<List<Order>> {
        val result = MutableLiveData<List<Order>>()
        db.collection(USER_FIREBASE).document(userManager.getAccessToken()).collection(ORDER_USER)
            .whereEqualTo(STATUS_ORDER, 1).orderBy(TIME_CREATE, TypeSort.DESCENDING.value)
            .get().addOnSuccessListener { documents ->
                val list = mutableSetOf<Order>()
                for (doc in documents) {
                    list.add(doc.toObject())
                }
                result.postValue(list.toList())
            }
        return result
    }

    fun getSize() {
        if (userManager.isLogged()) {
            db.collection(USER_FIREBASE).document(userManager.getAccessToken())
                .collection(ORDER_USER).get().addOnSuccessListener { documents ->
                    totalOrder.postValue(documents.size())
                }
        }
    }

    fun getOrder(idOrder: String): Flow<Order> {
        val result = MutableLiveData<Order>()
        if (userManager.isLogged() && idOrder.isNotBlank()) {
            db.collection(USER_FIREBASE).document(userManager.getAccessToken())
                .collection(ORDER_USER).document(idOrder).get().addOnSuccessListener { document ->
                    result.postValue(document.toObject())
                }
        }
        return result.asFlow()
    }

    fun setOrderFirebase(order: Order): MutableLiveData<Boolean> {
        val result = MutableLiveData(false)
        db.collection(USER_FIREBASE)
            .document(userManager.getAccessToken())
            .collection(ORDER_USER)
            .document(order.id)
            .set(order)
            .addOnSuccessListener {
                result.postValue(true)
            }
        return result
    }

    suspend fun getUserInfo(): MutableLiveData<ApiResponse<UserData>?> {

        val response = apiService.getInfo()
        Log.d("TAG", "setOrderCheckout: response:: $response")
        userProfile.postValue(response)
        return userProfile
    }

    suspend fun setOrderCheckout(order: OrderRequest): MutableLiveData<Boolean> {

        val gson = Gson()
        val jsonString = gson.toJson(order)
        val requestBody = jsonString.toRequestBody("application/json".toMediaTypeOrNull())

        val response = apiService.placeOrder(requestBody)
        Log.d("TAG", "setOrderCheckout: response:: $response")
        result.postValue(response.isSuccessful())
        return result
    }

    suspend fun verifyPhone(phone: String): MutableLiveData<Boolean> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("phone", "971$phone")
        val response = apiService.verifyPhone(jsonObject)
        Log.d("TAG", "verifyPhone: response:: $response")
        otpSent.postValue(response.isSuccessful())
        return otpSent
    }

    suspend fun verifyOtp(phone: String, otp: String): MutableLiveData<Boolean> {
        val jsonObject = JsonObject()
        jsonObject.addProperty("phone", "971$phone")
        jsonObject.addProperty("verify_otp", otp)
        val response = apiService.verifyPhone(jsonObject)
        Log.d("TAG", "verifyOtp: response:: $response")
        phoneVerified.postValue(response.isSuccessful())
        return phoneVerified
    }

    suspend fun deleteAccount(): MutableLiveData<Boolean> {
        val response = apiService.deleteAccount()
        Log.d("TAG", "deleteAccount: response:: $response")
        deleteAccount.postValue(true)
        return deleteAccount
    }
}