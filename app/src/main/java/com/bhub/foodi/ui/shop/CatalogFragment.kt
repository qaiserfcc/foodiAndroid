//package com.bhub.foodi.ui.shop
//
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import android.widget.Toast
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.activityViewModels
//import androidx.fragment.app.setFragmentResultListener
//import androidx.fragment.app.viewModels
//import androidx.navigation.fragment.findNavController
//import androidx.recyclerview.widget.GridLayoutManager
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.bhub.foodi.R
//import com.bhub.foodi.adapters.ListCategoriesAdapter
//import com.bhub.foodi.adapters.ListProductAdapter
//import com.bhub.foodi.adapters.ListProductGridAdapter
//import com.bhub.foodi.adapters.ProductPagingDataAdapter
//import com.bhub.foodi.core.BaseFragment
//import com.bhub.foodi.data.Product
//import com.bhub.foodi.databinding.FragmentCatalogBinding
//import com.bhub.foodi.response.Category
//import com.bhub.foodi.ui.favorite.BottomSheetFavorite
//import com.bhub.foodi.ui.home.HomeViewModel
//import com.bhub.foodi.utilities.*
//import dagger.hilt.android.AndroidEntryPoint
//import kotlin.math.roundToInt
//
//
//@AndroidEntryPoint
//class CatalogFragment : BaseFragment<FragmentCatalogBinding>(
//    FragmentCatalogBinding::inflate
//) {
//    val homeViewModel: HomeViewModel by activityViewModels()
//    override val viewModel: ShopViewModel by viewModels()
//    override var isHideBottom = false
//
//    private var nameTitle: String? = null
//    private var searchName: String? = null
//    private lateinit var adapterProduct: ListProductAdapter
//    private lateinit var adapterProducts: ProductPagingDataAdapter
//    private lateinit var adapterProductGrid: ListProductGridAdapter
//    private lateinit var adapterCategory: ListCategoriesAdapter
//    private var isLinearLayoutManager = true
//    private var filterPrice = emptyList<Float>()
//    private var listProduct: List<Product> = emptyList()
//
//    override fun setUpArgument(bundle: Bundle) {
//        Log.d("TAG", "setUpObserve: nameTitle : ${arguments?.getString(NAME_CATEGORY)}")
//
//        arguments?.let {
//            nameTitle = it.getString(NAME_CATEGORY).toString()
//            searchName = it.getString(NAME_PRODUCT)
//            viewModel.setSearch(searchName ?: "")
//        }
//    }
//
//    override fun setUpAdapter() {
//        adapterProducts = ProductPagingDataAdapter()
//        adapterProduct = ListProductAdapter({
//            val action = CatalogNewFragmentDirections.actionCatalogFragmentToProductDetailFragment(
//                idProduct = it.id
//            )
//            findNavController().navigate(action)
//        }, { btnFavorite, product ->
//            val bottomSheetSize = BottomSheetFavorite(product)
//            bottomSheetSize.show(parentFragmentManager, BottomSheetFavorite.TAG)
//            viewModel.btnFavorite.postValue(btnFavorite)
//        }, { view, product ->
//            viewModel.setButtonFavorite(requireContext(), view, product.id)
//        })
//
//        adapterProductGrid = ListProductGridAdapter({
//            val action = CatalogNewFragmentDirections.actionCatalogFragmentToProductDetailFragment(
//                idProduct = it.id
//            )
//            findNavController().navigate(action)
//        }, { _, product ->
//            val bottomSheetSize = BottomSheetFavorite(product)
//            bottomSheetSize.show(parentFragmentManager, BottomSheetFavorite.TAG)
//        }, { view, product ->
//            viewModel.setButtonFavorite(requireContext(), view, product.id)
//        })
//
//        adapterCategory = ListCategoriesAdapter { str ->
//            if (binding.appBarLayout.topAppBar.title == str) {
//                val action = CatalogNewFragmentDirections.actionCatalogFragmentSelf(
//                    nameCategories = "", nameProduct = null
//                )
//                findNavController().navigate(action)
//            } else {
//                val action = CatalogNewFragmentDirections.actionCatalogFragmentSelf(
//                    nameCategories = str, nameProduct = null
//                )
//                findNavController().navigate(action)
//            }
//        }
//    }
//
//    override fun setUpObserve() {
//        Log.d("TAG", "setUpObserve: nameTitle : $nameTitle")
//        if (nameTitle.isNullOrBlank()) {
//            viewModel.setCategory("")
//        } else {
//            if (nameTitle.toString() == NEW) {
//                viewModel.setCategory("")
//                viewModel.setSort(1)
//            } else {
//                viewModel.setCategory(nameTitle.toString())
//            }
//        }
//        viewModel.isLoading.postValue(true)
//        viewModel.setSort(DEFAULT_SORT)
//        viewModel.query = null
//        checkInternet()
//
//
//        viewModel.apply {
//            homeViewModel.apply {
//                homeLiveData.observe(viewLifecycleOwner) {
//                    it?.categories?.let { it1: List<Category> ->
//                        val list = arrayListOf<String>()
//                        for (category in it1) {
//                            list.addAll(category.getSubcategoryNames())
//                        }
//                        adapterCategory.submitList(list)
//                        adapterCategory.positionCurrent = list.indexOf(getCategory())
//                    }
//                }
//            }
////            allCategory.observe(viewLifecycleOwner) {
////                adapterCategory.submitList(it)
////                adapterCategory.positionCurrent = it.indexOf(getCategory())
////            }
//
//            products.observe(viewLifecycleOwner) {
//                if (it.isNotEmpty()) {
//                    listProduct = it.toMutableList()
//                    isLoading.postValue(false)
//                    submitList(it)
//                } else {
//                    loadMoreProduct()
//                }
//            }
//            isLoading.observe(viewLifecycleOwner) {
//                if (it) {
//                    binding.progressBar.visibility = View.VISIBLE
//                } else {
//                    binding.progressBar.visibility = View.GONE
//                }
//            }
//            statusSort.observe(viewLifecycleOwner) {
//                if (it == DEFAULT_SORT) {
//                    binding.appBarLayout.btnSort.text = getString(R.string.newest)
//                } else if (listProduct.isNotEmpty() && it >= 0) {
//                    val list = filterSort(listProduct)
//                    listProduct = list.toMutableList()
//                    submitList(listProduct)
//                    binding.nestedScrollView.scrollTo(0, 0)
//                }
//            }
//        }
//    }
//
//    override fun setUpViews() {
//        Log.d("TAG", "setUpViews: nameTitle 1 : $nameTitle")
//        binding.apply {
//            Log.d("TAG", "setUpViews: nameTitle 1 2: $nameTitle")
//            nestedScrollView.viewTreeObserver.addOnScrollChangedListener {
//                val view = nestedScrollView.getChildAt(nestedScrollView.childCount - 1) as View
//                Log.d("Catalog List", " ${nestedScrollView.childCount}:child")
//                val diff: Int =
//                    view.bottom - (nestedScrollView.height + nestedScrollView.scrollY)
//                if (diff == 0) {
//                    loadMoreProduct()
//                }
//            }
//            if (nameTitle == "all") {
//                Log.d("TAG", "setUpViews: nameTitle 2: $nameTitle")
//                appBarLayout.topAppBar.title = getString(R.string.all_product)
//            } else {
//                appBarLayout.topAppBar.title = nameTitle
//            }
//
//            if (viewModel.statusSort.value == 1) {
//                appBarLayout.btnSort.text = getString(R.string.newest)
//            }
//
////            nestedScrollView.viewTreeObserver?.addOnScrollChangedListener {
////                nestedScrollView.apply {
////                    val view = getChildAt(0)
////                    val diff = view.bottom - (height + scrollY)
////                    if (diff <= 0) {
////                        loadMoreProduct()
////                    }
////                }
////            }
//
//            appBarLayout.MaterialToolbar.setNavigationOnClickListener {
//                findNavController().navigateUp()
//            }
//
//            appBarLayout.recyclerViewCategories.layoutManager = LinearLayoutManager(
//                context, LinearLayoutManager.HORIZONTAL, false
//            )
//
//            appBarLayout.recyclerViewCategories.adapter = adapterCategory
//            appBarLayout.btnFilter.setOnClickListener {
//                if (filterPrice.isNotEmpty()) {
//                    val bottomFilter = BottomFilter(filterPrice[0], filterPrice[1])
//                    bottomFilter.show(parentFragmentManager, BottomFilter.TAG)
//                } else {
//                    val bottomFilter = BottomFilter()
//                    bottomFilter.show(parentFragmentManager, BottomFilter.TAG)
//                }
//
//            }
//            //Handle Button Change View
//            appBarLayout.btnChangeView.background =
//                if (isLinearLayoutManager) ContextCompat.getDrawable(
//                    requireContext(),
//                    R.drawable.ic_view_module
//                )
//                else ContextCompat.getDrawable(requireContext(), R.drawable.ic_view_list)
//            recyclerViewProduct.isNestedScrollingEnabled = false
//            recyclerViewProduct.layoutManager =
//                GridLayoutManager(context, GRIDVIEW_SPAN_COUNT_PRODUCT)
//            recyclerViewProduct.adapter = adapterProducts
//            appBarLayout.btnChangeView.setOnClickListener {
//                isLinearLayoutManager = !isLinearLayoutManager
//                if (isLinearLayoutManager) {
//                    recyclerViewProduct.layoutManager = LinearLayoutManager(context)
//                    recyclerViewProduct.adapter = adapterProducts
//                } else {
//                    recyclerViewProduct.layoutManager =
//                        GridLayoutManager(context, GRIDVIEW_SPAN_COUNT_PRODUCT)
//                    recyclerViewProduct.adapter = adapterProductGrid
//                }
//            }
//
//            appBarLayout.btnSort.setOnClickListener {
//                val select = if (viewModel.statusSort.value == DEFAULT_SORT) {
//                    1
//                } else {
//                    viewModel.statusSort.value ?: 0
//                }
//                val bottomSheetSort = BottomSheetSort(select)
//                bottomSheetSort.show(parentFragmentManager, BottomSheetSort.TAG)
//            }
//
//            // Handle Search Bar
//            appBarLayout.MaterialToolbar.setOnMenuItemClickListener {
//                when (it.itemId) {
//                    R.id.ic_search -> {
//                        findNavController().navigateUp()
//                        findNavController().navigate(R.id.searchFragment)
//                        true
//                    }
//
//                    else -> false
//                }
//            }
//        }
//        setFragmentListener()
//    }
//
//    private fun loadMoreProduct() {
//        if (viewModel.loadMore.value == true) {
//            viewModel.loadMore(listProduct)
//            viewModel.setSort(DEFAULT_SORT)
//        } else {
//            viewModel.isLoading.postValue(false)
//        }
//    }
//
//    private fun submitList(list: List<Product>) {
//        adapterProduct.submitList(list)
//        adapterProductGrid.submitList(list)
//    }
//
//    private fun setFragmentListener() {
//        setFragmentResultListener(REQUEST_KEY) { _, bundle ->
//            val result = bundle.getString(BUNDLE_KEY_NAME)
//            val position = bundle.getInt(BUNDLE_KEY_POSITION)
//            if (!result.isNullOrBlank()) {
//                binding.appBarLayout.btnSort.text = result
//                viewModel.setSort(position)
//            }
//            val min = bundle.getFloat(BUNDLE_KEY_MIN)
//            val max = bundle.getFloat(BUNDLE_KEY_MAX)
//            if (min >= 0 && max > 0) {
//                filterPrice = listOf(min, max)
//                viewModel.apply {
//                    submitList(filterPrice(min, max, listProduct))
//                }
//                binding.appBarLayout.btnFilter.text =
//                    "${min.roundToInt()}\$-${max.roundToInt()} ${getString(R.string.currency)}"
//                binding.nestedScrollView.scrollTo(0, 0)
//            } else {
//                filterPrice = emptyList()
//                submitList(listProduct)
//                binding.appBarLayout.btnFilter.text = getString(R.string.filters)
//                binding.nestedScrollView.scrollTo(0, 0)
//            }
//            val isFavorite = bundle.getBoolean(BUNDLE_KEY_IS_FAVORITE, false)
//            if (isFavorite) {
//                viewModel.btnFavorite.value?.let {
//                    it.background = ContextCompat.getDrawable(
//                        requireContext(), R.drawable.btn_favorite_active
//                    )
//                }
//            }
//        }
//    }
//
//    companion object {
//        const val DEFAULT_SORT = -1
//
//        @JvmStatic
//        fun newInstance(nameTitle: String, searchName: String) = CatalogFragment().apply {
//            arguments = Bundle().apply {
//                putString(NAME_CATEGORY, nameTitle)
//                putString(NAME_PRODUCT, searchName)
//            }
//        }
//    }
//}