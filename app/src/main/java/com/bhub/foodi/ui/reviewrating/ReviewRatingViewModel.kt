package com.bhub.foodi.ui.reviewrating

import android.content.Context
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.bhub.foodi.R
import com.bhub.foodi.core.BaseViewModel
import com.bhub.foodi.data.*
import com.bhub.foodi.utilities.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.bhub.foodi.networkservice.ApiResponse
import com.bhub.foodi.networkservice.ApiService
import com.bhub.foodi.response.Reviews
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class ReviewRatingViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val reviewRepository: ReviewRepository,
    private val userManager: UserManager,
    private val db: FirebaseFirestore,
    private val apiService: ApiService
) : BaseViewModel() {
    val addReview = MutableLiveData<ApiResponse<JsonObject?>?>()
    val productReviews = MutableLiveData<ApiResponse<Reviews>?>()
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference

    val listReview = MutableLiveData<List<Review>>(emptyList())
    val listRating = MutableLiveData<List<Int>>(emptyList())
    val alertStar = MutableLiveData(false)
    val alertDescription = MutableLiveData(false)
    val statusFilterImage = MutableLiveData(false)
    private val idUser = if (userManager.isLogged()) {
        userManager.getAccessToken()
    } else {
        null
    }

    private val statusIdProduct = MutableStateFlow("")
    val product = statusIdProduct.flatMapLatest {
        getProduct(it)
    }.asLiveData()


    fun isLogged(): Boolean {
        return userManager.isLogged()
    }

    private fun fetchRatingProduct(idProduct: String) {

//        db.collection(REVIEW_FIREBASE).whereEqualTo(ID_PRODUCT, idProduct).get()
//            .addOnSuccessListener { documents ->
//                val list: MutableList<Int> = mutableListOf(0, 0, 0, 0, 0)
//                for (document in documents) {
//                    val review = document.toObject<Review>()
//                    if (review.rating in 1..5) {
//                        list[review.rating.toInt() - 1]++
//                    }
//                }
//
//                listRating.postValue(list)
//                val totalRating = Product().getTotalRating(list)
//                val average = Product().getAverageRating(list)
//                db.collection(PRODUCT_FIREBASE).document(idProduct)
//                    .update(NUMBER_REVIEWS, totalRating, REVIEW_STARS, average)
//                isLoading.postValue(false)
//            }
    }

    fun getRatingProduct(idProduct: String) {
        db.collection(REVIEW_FIREBASE).whereEqualTo(ID_PRODUCT, idProduct).get()
            .addOnSuccessListener { documents ->
                val list: MutableList<Int> = mutableListOf(0, 0, 0, 0, 0)
                for (document in documents) {
                    val review = document.toObject<Review>()
                    if (review.rating in 1..5) {
                        list[review.rating.toInt() - 1]++
                    }
                }
                listRating.postValue(list)
            }
    }

    fun setIdProduct(idProduct: String) {
        isLoading.postValue(true)
        statusIdProduct.value = idProduct
    }

    private fun getProduct(idProduct: String): Flow<Product> {
        return productRepository.getProduct(idProduct)
    }

    private val compositeDisposable = CompositeDisposable() // To manage disposables

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose() // Dispose subscriptions when ViewModel is cleared
    }

    fun getReview(idProduct: String) {

        isLoading.postValue(true)
        Log.d("RatingViewModel", "getReview:idProduct: $idProduct ")
        compositeDisposable.add(
            apiService.getProductReviews(idProduct).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({ response ->
                    Log.d("RatingViewModel", "getReview:apiService.getProductReviews: $idProduct ")
                    productReviews.postValue(response)

                    isLoading.postValue(false)
                }, { error ->
                    // Handle errors
                    Log.d("RatingViewModel", "getReview:error ${error} ")
                    Log.d("RatingViewModel", "getReview:error::: ${error.message} ")
                    productReviews.postValue(null)
                    isLoading.postValue(false)
                })
        )
    }


//        db.collection(REVIEW_FIREBASE)
//            .whereEqualTo(ID_PRODUCT, idProduct)
//            .orderBy(FilterReview.DATE.value,TypeSort.DESCENDING.value)
//            .get()
//            .addOnSuccessListener { documents ->
//                val list = mutableListOf<Review>()
//                for (doc in documents) {
//                    list.add(doc.toObject())
//                }
//                listReview.postValue(list)
//            }
//    }

    private fun setReviewForProduct(review: Review) {
        Log.d("RatingViewModel", "setReviewForProduct:review: $review ")

        if (!userManager.isLogged()) {
            toastMessage.postValue("Please Login to add reviews")
            return
        }
        isLoading.postValue(true)
        val gson = Gson()
        val requestJson = gson.toJsonTree(review).asJsonObject

        compositeDisposable.add(
            apiService.addReview(requestJson).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({ response ->
                    Log.d(
                        "RatingViewModel",
                        "setReviewForProduct:apiService.setReviewForProduct: ${review.product_id} "
                    )
                    addReview.postValue(response)
                    dismiss.postValue(true)
                    isLoading.postValue(false)

                }, { error ->
                    // Handle errors
                    Log.d("RatingViewModel", "setReviewForProduct:error ${error} ")
                    Log.d("RatingViewModel", "setReviewForProduct:error::: ${error.message} ")
                    addReview.postValue(null)
                    dismiss.postValue(true)
                    isLoading.postValue(false)
                })
        )
//        db.collection(REVIEW_FIREBASE).document(review.getReviewDate()?.seconds.toString())
//            .set(review).addOnSuccessListener {
//                toastMessage.postValue(BottomAddReview.SUCCESS)
//                dismiss.postValue(true)
//                isLoading.postValue(false)
//                getReview(review.product_id)
//            }.addOnFailureListener {
//                toastMessage.postValue(BottomAddReview.SUCCESS)
//                dismiss.postValue(true)
//                isLoading.postValue(false)
//            }
    }

    fun filterImage(isCheck: Boolean): List<Review>? {
        return if (isCheck) {
            listReview.value?.filter {
                it.listImage.isNotEmpty()
            }
        } else {
            listReview.value
        }
    }

    fun uploadImage(
        idReview: String, listImage: List<String>
    ): Pair<LiveData<List<String>>, LiveData<Boolean>> {
        val result = MutableLiveData<List<String>>()
        val isSuccess = MutableLiveData(true)
        val list: MutableList<String> = mutableListOf()
        viewModelScope.launch {
            for ((index, review) in listImage.withIndex()) {
                val ref = storageReference.child("reviews/${idReview}/$index")
                val uploadTask = ref.putFile(File(review).toUri())
                uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        isSuccess.postValue(false)
                        task.exception?.let {
                            throw it
                        }
                    }
                    ref.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        list.add(downloadUri.toString())
                        result.postValue(list)
                    }
                }.addOnFailureListener {
                    isSuccess.postValue(false)
                }
            }
        }
        return Pair(result, isSuccess)
    }

    fun getNameAndAvatarUser(idUser: String): LiveData<Pair<String, String>> {
        val result = MutableLiveData<Pair<String, String>>()
        db.collection(USER_FIREBASE).document(idUser).get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject<User>()
                user?.let {
                    result.postValue(Pair(user.name, user.avatar))
                }
            }
        return result
    }

    fun removeHelpful(review: Review) {
        if (idUser != null) {
            db.collection(REVIEW_FIREBASE).document(review.getReviewDate()?.seconds.toString())
                .collection(HELPFUL).document(idUser).delete()
        }
    }

    fun addHelpful(review: Review) {
        if (idUser != null) {
            val data = hashMapOf(ID_USER to idUser)
            db.collection(REVIEW_FIREBASE).document(review.getReviewDate()?.seconds.toString())
                .collection(HELPFUL).document(idUser).set(data)
        }

    }


    fun checkHelpfulForUser(review: Review): LiveData<Boolean> {
        val result = MutableLiveData(false)
        if (idUser != null) {
            db.collection(REVIEW_FIREBASE).document(review.getReviewDate()?.seconds.toString())
                .collection(HELPFUL).document(idUser).get().addOnSuccessListener {
                    if (it.data != null) {
                        result.postValue(true)
                    }
                }
        }
        return result
    }

    fun createReview(
        idProduct: String, description: String, star: Long, listImage: List<String>
    ): Review? {
        if (star < 1) {
            alertStar.postValue(true)
        } else if (description.isBlank()) {
            alertDescription.postValue(true)
        } else {
            return idUser?.let {

                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

                Review(
                    user_id = it,
                    product_id = idProduct,
                    review = description,
                    rating = star,
                    review_date = dateFormat.format(Date()),
                    listImage = listImage
                )
            }
        }
        return null
    }

    fun insertReview(review: Review) {
        setReviewForProduct(review)
    }

    fun setColorHelpful(
        context: Context, isHelpful: Boolean, txtHelpful: TextView, icLike: ImageView
    ) {
        if (isHelpful) {
            txtHelpful.setTextColor(
                ContextCompat.getColor(
                    context, R.color.colorPrimary
                )
            )
            icLike.setImageDrawable(
                ContextCompat.getDrawable(context, R.drawable.ic_like2)
            )
        } else {
            txtHelpful.setTextColor(
                ContextCompat.getColor(
                    context, R.color.black
                )
            )
            icLike.setImageDrawable(
                ContextCompat.getDrawable(context, R.drawable.ic_like)
            )
        }
    }

    val reviews = reviewRepository.reviews

    fun getAllReviewOfUser() {
        reviewRepository.getAllReviewOfUser()
    }

    fun filterReview(filterReview: FilterReview, typeSort: TypeSort) {
        reviewRepository.filterReview(filterReview, typeSort)
    }
}

enum class FilterReview(val value: String) {
    DATE("createdTimer"), STAR("star")
}