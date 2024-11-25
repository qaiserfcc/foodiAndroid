package com.hallyu.style.ui.home

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.hallyu.style.core.BaseViewModel
import com.hallyu.style.data.BagRepository
import com.hallyu.style.data.FavoriteRepository
import com.hallyu.style.data.Product
import com.hallyu.style.data.ProductRepository
import com.hallyu.style.data.UserManager
import com.hallyu.style.networkservice.ApiResponse
import com.hallyu.style.networkservice.ApiService
import com.hallyu.style.response.Category
import com.hallyu.style.response.HomeData
import com.hallyu.style.response.SubCategory
import com.hallyu.style.utilities.CATEGORY_NAME
import com.hallyu.style.utilities.CREATED_DATE
import com.hallyu.style.utilities.LIMIT
import com.hallyu.style.utilities.PRODUCT_FIREBASE
import com.hallyu.style.utilities.SALE_PERCENT
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val favoriteRepository: FavoriteRepository,
    private val bagRepository: BagRepository,
    private val db: FirebaseFirestore,
    val userManager: UserManager,
    val apiService: ApiService
) : BaseViewModel() {
    //    val category = productRepository.getAllCategory().asLiveData()
    val btnFavorite = MutableLiveData<View>()
    val loadMore = MutableLiveData(true)
    val checkSale = MutableLiveData(false)

    val homeLiveData = MutableLiveData<HomeData?>()
    val categories = mutableListOf<Category>()
    val subCategories = mutableListOf<SubCategory>()
    val homeError = MutableLiveData<String?>()

    val productSelected: MutableStateFlow<Product?> = MutableStateFlow(null)

    init {
        favoriteRepository.fetchFavoriteAndProduct()
    }

    fun getHomeIndex() {
        val responseObservable: Observable<ApiResponse<HomeData?>> = apiService.homeIndex()
        responseObservable.observeOn(AndroidSchedulers.mainThread())?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : DisposableObserver<ApiResponse<HomeData?>?>() {
                override fun onNext(response: ApiResponse<HomeData?>) {
                    if (response.isSuccessful()) {
                        response.data?.categories?.let {
                            categories.clear()
                            categories.addAll(it)
                            subCategories.clear()
                            it.map { t->
                                subCategories.addAll(t.subs)
                            }
                        }
                        homeLiveData.postValue(response.data)
                    } else {
                        Log.e("TAG", "onFailure: at " + response)
                        homeError.postValue(response.errorBody())

                    }
                }

                override fun onError(e: Throwable) {
                    Log.e("TAG", "onFailure: at " + e.localizedMessage)
                    homeError.postValue(e.localizedMessage)
                }

                override fun onComplete() {}
            })
    }

    fun setProduct(product: Product) {
        productSelected.value = product
    }

    fun getProductWithCategory(category: String): MutableLiveData<List<Product>> {
        val result: MutableLiveData<List<Product>> = MutableLiveData(emptyList())
        db.collection(PRODUCT_FIREBASE).whereEqualTo(CATEGORY_NAME, category).get()
            .addOnSuccessListener { documents ->
                val list = mutableListOf<Product>()
                for (document in documents) {
                    list.add(document.toObject())
                }
                result.postValue(list)
            }
        return result
    }

    fun getNewProduct(): MutableLiveData<List<Product>> {
        val result: MutableLiveData<List<Product>> = MutableLiveData(emptyList())
        db.collection(PRODUCT_FIREBASE).orderBy(CREATED_DATE, Query.Direction.DESCENDING)
            .limit(LIMIT.toLong()).get().addOnSuccessListener { documents ->
                val list = mutableListOf<Product>()
                for (document in documents) {
                    list.add(document.toObject())
                }
                result.postValue(list)
            }
        return result
    }

    fun getSaleProduct(): MutableLiveData<List<Product>> {
        val result: MutableLiveData<List<Product>> = MutableLiveData(emptyList())
        db.collection(PRODUCT_FIREBASE).whereNotEqualTo(SALE_PERCENT, null).get()
            .addOnSuccessListener { documents ->
                if (documents.size() == 0) {
                    checkSale.postValue(true)
                } else {
                    val list = mutableListOf<Product>()
                    for (document in documents) {
                        list.add(document.toObject())
                    }
                    result.postValue(list)
                }
            }
        return result
    }

    fun setButtonFavorite(context: Context, buttonView: View, idProduct: String) {
        favoriteRepository.setButtonFavorite(context, buttonView, idProduct)
    }
}