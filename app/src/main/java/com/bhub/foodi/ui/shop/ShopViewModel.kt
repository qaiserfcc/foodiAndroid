package com.bhub.foodi.ui.shop

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.google.gson.JsonObject
import com.bhub.foodi.core.BaseViewModel
import com.bhub.foodi.data.FavoriteRepository
import com.bhub.foodi.data.Product
import com.bhub.foodi.data.ProductRepository
import com.bhub.foodi.data.TypeSort
import com.bhub.foodi.data.UserManager
import com.bhub.foodi.networkservice.ApiResponse
import com.bhub.foodi.networkservice.ApiService
import com.bhub.foodi.response.DetailData
import com.bhub.foodi.response.PaginationData
import com.bhub.foodi.utilities.CATEGORY_NAME
import com.bhub.foodi.utilities.CREATED_DATE
import com.bhub.foodi.utilities.LIMIT
import com.bhub.foodi.utilities.PRODUCT_FIREBASE
import com.bhub.foodi.utilities.SALE
import com.bhub.foodi.utilities.SALE_PERCENT
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ShopViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val favoriteRepository: FavoriteRepository,
    private val db: FirebaseFirestore,
    val userManager: UserManager,
    val apiService: ApiService
) : BaseViewModel() {
    val onErrorLiveData = MutableLiveData<String>()
    val productDetailLiveData = MutableLiveData<DetailData>()
    val onAddToCart = MutableLiveData<JsonObject?>()
    private val statusIdProduct = MutableStateFlow("")
    private var statusFilter = Pair("", "")
    var listProduct: MutableList<Product> = mutableListOf()
    val newListProductLiveData = MutableLiveData<PaginationData>()

    //    val product: LiveData<Product> = statusIdProduct.flatMapLatest {
//        productRepository.getProduct(it)
//    }.asLiveData()
    val statusSort = MutableLiveData(0)
    var page = 1
    val allCategory = productRepository.getAllCategory().asLiveData()
    val loadMore = MutableLiveData(true)
    val products = MutableLiveData<List<Product>>()
    val btnFavorite = MutableLiveData<View>()
    var query: Query? = null
//    fun getAllProduct(): Flow<PagingData<Product>> {
//        return Pager(PagingConfig(pageSize = 10, prefetchDistance = 1, enablePlaceholders = true)) {
//                ProductPagingSource(apiService)
//            }.flow.cachedIn(viewModelScope)
//    }


    fun getAllProducts(queryParams: ProductQueryParams) {
        Log.d("TAG", "getAllProducts: Query params: $queryParams")

        apiService.getAllProduct(
//            category = queryParams.category,
            subCategory = queryParams.subCategory,
            brand = queryParams.brand,
            minPrice = queryParams.minPrice,
            maxPrice = queryParams.maxPrice,
            sort = queryParams.sort,
            search = queryParams.search,
            limit = queryParams.limit,
            page = queryParams.page
        ).observeOn(AndroidSchedulers.mainThread())?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : DisposableObserver<ApiResponse<PaginationData?>?>() {
                override fun onNext(response: ApiResponse<PaginationData?>) {
                    Log.d("TAG", "onNext:check 1 ")
                    isLoading.postValue(false)
                    if (response.isSuccessful()) {
                        Log.d("TAG", "onNext:check 2 ")

                        response.data?.let {
                            if (listProduct.isNotEmpty()) {
                                //page 2
                                listProduct.addAll(it.products)
                            } else {
                                //page 1
                                listProduct = it.products.toMutableList()
                            }
                            newListProductLiveData.postValue(it)
                            Log.d("TAG", "onNext:check 3")
                            if (it.pagination.next_page_url == null) {
                                page = 0
                            } else {
                                page = it.pagination.current_page + 1
                            }

                        }
                    } else {
                        Log.e("TAG", "onFailure: at " + response.errorBody())
                        loadMore.postValue(false)

                    }
                }

                override fun onError(e: Throwable) {
                    isLoading.postValue(false)
                    Log.e("TAG", "onFailure: at " + e.localizedMessage)

                }

                override fun onComplete() {
                    loadMore.postValue(false)

                }
            })
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            Log.d(
                "TAG",
                "addToCart: AccessToken: ${userManager.getAccessToken()}"
            )// Use viewModelScope for coroutines
            try {
                val request = JsonObject().apply {
                    addProperty("product_id", product.id)
                    addProperty("quantity", 1)
                }
                Log.d("TAG", "getAllProducts: Query request: $request")
                isLoading.postValue(true) // Show loading state

                val response = apiService.addToCart(request) // Make API call
                if (response.isSuccessful) {
                    onAddToCart.postValue(response.body())
                } else {
                    Log.e("TAG", "onFailure:Add to cart ${response}")
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


    fun loadMoreProducts() {
        isLoading.postValue(true)
        statusFilter.apply {
            if (page == 0) {
                Log.d("TAG", "loadMoreProducts:No more pages back to page=0 ")
                isLoading.postValue(false)
                return
            }
            Log.d("TAG", "loadMoreProducts:this.first category = ${this.first} ")
            Log.d("TAG", "loadMoreProducts:search = ${this.second} ")
            var subCat: String? = null
            if (first != "all") {
                subCat = first
            }
            val queryParams = ProductQueryParams(
                subCategory = subCat, sort = "asc", search = this.second, page = page

            )
            getAllProducts(queryParams)
        }
    }


    fun getProductDetail(idProduct: String) {
        val responseObservable: Observable<ApiResponse<DetailData?>> =
            apiService.getProductDetails(idProduct)
        responseObservable.observeOn(AndroidSchedulers.mainThread())?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(object : DisposableObserver<ApiResponse<DetailData?>?>() {
                override fun onNext(response: ApiResponse<DetailData?>) {
                    if (response.isSuccessful()) {
                        response.data?.let { productDetailLiveData.postValue(it) }
                    } else {
                        Log.e("TAG", "onFailure: at " + response.errorBody())
                        onErrorLiveData.postValue(response.errorBody())

                    }
                }

                override fun onError(e: Throwable) {
                    Log.e("TAG", "onFailure: at " + e.localizedMessage)
                    onErrorLiveData.postValue(e.localizedMessage)
                }

                override fun onComplete() {}
            })
    }

    fun filterPrice(min: Float, max: Float, list: List<Product>): MutableList<Product> {
        val temp = list.toMutableList()
        for (product in list) {
            val price = product.getPrice()
            if (price < min || price > max) {
                temp.remove(product)
            }
        }
        return temp
    }


    fun loadMore(list: List<Product>) {
        Log.d("TAG", "loadMore:isLoading:: ${isLoading.value}")
        isLoading.postValue(true)
        statusFilter.apply {
            if (this.first.isNotBlank() && this.second.isNotBlank()) {
                loadMoreCategoryAndSearch(this.second, this.first, list)
            } else if (this.first.isNotBlank()) {
                loadMoreCategory(this.first, list)
            } else if (this.second.isNotBlank()) {
                loadMoreSearch(this.second, list)
            } else {
                loadMoreAll(list)
            }
        }
    }

    private fun loadMoreAll(list: List<Product>) {
        val queryBase =
            db.collection(PRODUCT_FIREBASE).orderBy(CREATED_DATE, TypeSort.DESCENDING.value)
                .limit(LIMIT.toLong())
        loadMoreBase(list, queryBase)

    }

    private fun loadMoreCategory(category: String, list: List<Product>) {
        if (category == SALE) {
            loadMoreSaleProduct(list)
        } else {
            val queryBase = db.collection(PRODUCT_FIREBASE).whereEqualTo(CATEGORY_NAME, category)
                .orderBy(CREATED_DATE, TypeSort.DESCENDING.value).limit(LIMIT.toLong())
            loadMoreBase(list, queryBase)
        }
    }

    private fun loadMoreSearch(search: String, list: List<Product>) {
        val queryBase =
            db.collection(PRODUCT_FIREBASE).orderBy(CREATED_DATE, TypeSort.DESCENDING.value)
                .limit(LIMIT.toLong())
        loadMoreBase(list, queryBase, search)
    }

    private fun loadMoreCategoryAndSearch(search: String, category: String, list: List<Product>) {
        val queryBase = db.collection(PRODUCT_FIREBASE).whereEqualTo(CATEGORY_NAME, category)
            .orderBy(CREATED_DATE, TypeSort.DESCENDING.value).limit(LIMIT.toLong())
        loadMoreBase(list, queryBase, search)
    }

    private fun loadMoreSaleProduct(list: List<Product>) {
        val queryBase = db.collection(PRODUCT_FIREBASE).whereNotEqualTo(SALE_PERCENT, null)
            .orderBy(SALE_PERCENT).limit(LIMIT.toLong())
        loadMoreBase(list, queryBase)
    }

    private fun loadMoreBase(list: List<Product>, queryBase: Query, search: String = "") {
        if (query == null) {
            query = queryBase
        }
        query?.let {
            it.get().addOnSuccessListener { documents ->
                val temp = mutableListOf<Product>()
                if (documents.size() != 0) {

                    temp.addAll(list)
                    for (document in documents) {
                        if (search.isNotBlank()) {
                            val product = document.toObject<Product>()
                            if (product.title.lowercase().contains(search.lowercase())) {
                                if (!temp.contains(product)) {
                                    temp.add(product)
                                }
                            }
                        } else {
                            temp.add(document.toObject())
                        }
                    }
                    val lastVisible = documents.documents[documents.size() - 1]
                    products.postValue(temp)
                    query = queryBase.startAfter(lastVisible)
                } else {
                    loadMore.postValue(false)
                }
                isLoading.postValue(false)
            }.addOnFailureListener {
                loadMore.postValue(false)
                isLoading.postValue(false)
            }
        }
    }


    fun setProduct(idProduct: String) {
        statusIdProduct.value = idProduct
    }

    fun filterSort(products: List<Product>): List<Product> {
        return when (statusSort.value) {
            0 -> products.sortedByDescending {
                it.numberReviews > 50
            }

            1 -> products.sortedByDescending {
                it.createdDate
            }

            2 -> products.sortedByDescending {
                it.numberReviews
            }

            3 -> {
                products.sortedBy {
                    it.getPrice()
                }
            }

            4 -> {
                products.sortedByDescending {
                    it.getPrice()
                }
            }

            else -> products
        }
    }

    fun relatedProduct(category: String): MutableLiveData<List<Product>> {
        val result = MutableLiveData<List<Product>>(emptyList())
        db.collection(PRODUCT_FIREBASE).whereEqualTo(CATEGORY_NAME, category).limit(LIMIT.toLong())
            .get().addOnSuccessListener { documents ->
                val list = mutableListOf<Product>()
                for (document in documents) {
                    list.add(document.toObject())
                }
                result.postValue(list)
            }
        return result
    }

    fun setCategory(category: String) {
        statusFilter = Pair(category, statusFilter.second)
    }

    fun getCategorySelectedId(): String {
        return statusFilter.first
    }

    fun setSearch(search: String) {
        statusFilter = Pair(statusFilter.first, search)
    }

    fun setSort(select: Int) {
        statusSort.postValue(select)
    }/*

    fun getAllSize(): MutableList<String> {
        val sizes: MutableSet<String> = mutableSetOf()
        product.value?.let {
            for (color in it.colors) {
                for (size in color.sizes) {
                    if (size.quantity > 0) {
                        sizes.add(size.size)
                    }
                }
            }
        }
        return sizes.toMutableList()
    }


    fun getAllColor(): MutableList<String> {
        val colors: MutableSet<String> = mutableSetOf()
        product.value?.let {
            for (color in it.colors) {
                color.color?.let { str ->
                    colors.add(str)
                }
            }
        }
        return colors.toMutableList()
    }
*/

    fun setButtonFavorite(context: Context, buttonView: View, idProduct: String) {
        favoriteRepository.setButtonFavorite(context, buttonView, idProduct)
    }

}