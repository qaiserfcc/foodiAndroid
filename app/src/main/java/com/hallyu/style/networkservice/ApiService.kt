package com.hallyu.style.networkservice

import com.google.gson.JsonObject
import com.hallyu.style.data.AuthResponse
import com.hallyu.style.data.Promotion
import com.hallyu.style.data.Review
import com.hallyu.style.data.UserData
import com.hallyu.style.response.CartOrders
import com.hallyu.style.response.DetailData
import com.hallyu.style.response.HomeData
import com.hallyu.style.response.OptionsData
import com.hallyu.style.response.PaginationData
import com.hallyu.style.response.Reviews
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("index")
    fun homeIndex(): Observable<ApiResponse<HomeData?>>

    @GET("show-product/{productId}")
    fun getProductDetails(@Path("productId") productId: String): Observable<ApiResponse<DetailData?>>

    @GET("product-reviews/{PRODUCT_ID}")
    fun getProductReviews(@Path("PRODUCT_ID") PRODUCT_ID: String): Observable<ApiResponse<Reviews>>


    @GET("get-products/")
    fun getAllProduct(
        @Query("category") category: String? = null,
        @Query("subCategory") subCategory: String? = null,
        @Query("brand") brand: Int? = null,
        @Query("minPrice") minPrice: Int? = null,
        @Query("maxPrice") maxPrice: Int? = null,
        @Query("sort") sort: String? = null,
        @Query("search") search: String? = null,
        @Query("limit") limit: Int = 10,
        @Query("page") page: Int = 1,
    ): Observable<ApiResponse<PaginationData?>>

@GET("cart/items/")
    fun getCart(
        ): Observable<Response<CartOrders?>>
    @POST("cart/add")
    suspend fun addToCart(@Body request: JsonObject): Response<JsonObject>

    @POST("cart/delete")
    suspend fun deleteCartItem(@Body request: JsonObject): Response<JsonObject>

    @POST("cart/clear")
    suspend fun clearCart(): Response<JsonObject>

    @POST("cart/update")
    suspend fun updateCartItem(@Body request: JsonObject): Response<JsonObject>

    @POST("auth/google/callback")
    suspend fun authCallBack(@Body request: JsonObject): Response<AuthResponse>

    @POST("auth/register")
    suspend fun registerUser(@Body request: JsonObject): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body request: JsonObject): Response<AuthResponse>

    @POST("cart/apply-coupon")
    suspend fun applyCoupon(@Body request: JsonObject): Response<ApiResponse<Promotion>>

    @GET("cart/order-options")
    suspend fun orderOption(): Response<ApiResponse<OptionsData>>

    @POST("cart/checkout")
    suspend fun placeOrder(@Body orderRequest: RequestBody): ApiResponse<JsonObject?>



    @GET("user/get-info")
    suspend fun getInfo(): ApiResponse<UserData>?



    @POST("auth/verify-otp")
    suspend fun verifyPhone(@Body orderRequest: JsonObject): ApiResponse<JsonObject>
    @POST("user/delete-my-account")
    suspend fun deleteAccount(): ApiResponse<JsonObject?>

    @POST("add-review")
    fun addReview(@Body request: JsonObject): Observable<ApiResponse<JsonObject?>>



}