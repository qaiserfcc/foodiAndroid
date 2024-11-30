package com.bhub.foodi.core


//class ProductPagingSource(private val apiService: ApiService) : PagingSource<Int, Product>() {
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
//        try {
//            val currentLoadingPageKey = params.key ?: 1
//            Log.d("TAG", "load: calling api $currentLoadingPageKey")
//            val response: Response<ApiResponse<PaginationData?>> =
//                apiService.getAllProduct(currentLoadingPageKey)
//
//            if (response.isSuccessful) {
//                Log.d("TAG", "load: calling api:Success")
//                val data = response.body()?.data
//                val products = data?.products ?: emptyList()
//                val currentPage = data?.pagination?.current_page ?: 1
//                val prevKey = if (currentPage == 1) null else currentPage - 1
//                val nextKey = if (data?.pagination?.next_page_url == null) null else currentPage + 1
//                return LoadResult.Page(
//                    data = products, prevKey = prevKey, nextKey = nextKey
//                )
//            } else {
//                return LoadResult.Error(Throwable("API Error: ${response.code()}"))
//            }
//        } catch (e: Exception) {
//            return LoadResult.Error(e)
//        }
//    }
//
//    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
//        // Not required for basic pagination, can return null
//        return null
//    }
//}