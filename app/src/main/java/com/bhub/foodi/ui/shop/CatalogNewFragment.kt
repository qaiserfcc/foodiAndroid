package com.bhub.foodi.ui.shop

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.bhub.foodi.R
import com.bhub.foodi.adapters.ListCategoriesAdapter
import com.bhub.foodi.adapters.ListProductAdapter
import com.bhub.foodi.core.BaseFragment
import com.bhub.foodi.data.Product
import com.bhub.foodi.databinding.FragmentCatalogBinding
import com.bhub.foodi.response.Category
import com.bhub.foodi.response.SubCategory
import com.bhub.foodi.ui.favorite.FavoriteViewModel
import com.bhub.foodi.ui.home.HomeViewModel
import com.bhub.foodi.utilities.BRAND_ID
import com.bhub.foodi.utilities.BUNDLE_KEY_IS_FAVORITE
import com.bhub.foodi.utilities.BUNDLE_KEY_MAX
import com.bhub.foodi.utilities.BUNDLE_KEY_MIN
import com.bhub.foodi.utilities.BUNDLE_KEY_NAME
import com.bhub.foodi.utilities.BUNDLE_KEY_POSITION
import com.bhub.foodi.utilities.GRIDVIEW_SPAN_COUNT_PRODUCT
import com.bhub.foodi.utilities.NAME_CATEGORY
import com.bhub.foodi.utilities.NAME_PRODUCT
import com.bhub.foodi.utilities.REQUEST_KEY
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt


@AndroidEntryPoint
class CatalogNewFragment : BaseFragment<FragmentCatalogBinding>(
    FragmentCatalogBinding::inflate
) {

    val homeViewModel: HomeViewModel by activityViewModels()
    override val viewModel: ShopViewModel by viewModels()
    private val favViewModel: FavoriteViewModel by viewModels()

    override var isHideBottom = false

    private var nameTitle: String? = "all"
    private var searchName: String? = null
    private var brandId: String? = null
    private lateinit var adapterProduct: ListProductAdapter
    private lateinit var adapterCategory: ListCategoriesAdapter
    private var prevList: List<Product> = emptyList()
    private var currentList: MutableList<Product> = arrayListOf()
    private var filterPrice: List<Float> = listOf(0.0f, 2500.0f)

    override fun setUpArgument(bundle: Bundle) {
        Log.d("TAG", "setUpObserve: nameTitle : ${arguments?.getString(NAME_CATEGORY)}")

        arguments?.let {
            nameTitle = it.getString(NAME_CATEGORY) ?: "all"
            searchName = it.getString(NAME_PRODUCT)
            brandId = it.getString(BRAND_ID)
            viewModel.setSearch(searchName ?: "")
        }
    }

    override fun setUpAdapter() {
        adapterProduct = ListProductAdapter({
            val action = CatalogNewFragmentDirections.actionCatalogFragmentToProductDetailFragment(
                idProduct = it.id
            )
            findNavController().navigate(action)
        }, { btnFavorite, product ->
            val tag = btnFavorite.tag as Boolean? ?: false
            Log.d("HomeFragment", "setUpAdapter: tag:$tag")
            if (tag) {
                favViewModel.removeFavorite(product)
                Log.d("HomeFragment", "setUpAdapter: removeFavorite")
                btnFavorite.background = ContextCompat.getDrawable(
                    requireContext(), R.drawable.btn_favorite_no_active
                )
            } else {
                favViewModel.insertFavorite(product)
                Log.d("HomeFragment", "setUpAdapter: insertFavorite")
                btnFavorite.background = ContextCompat.getDrawable(
                    requireContext(), R.drawable.btn_favorite_active
                )
            }
            btnFavorite.tag = !tag
//            viewModel.setButtonFavorite(requireContext(), btnFavorite, product.id)
        }, { view, product ->
            viewModel.setButtonFavorite(requireContext(), view, product.id)
        })

        adapterCategory = ListCategoriesAdapter { str ->
            if (binding.appBarLayout.topAppBar.title != str.name) {
                val action = CatalogNewFragmentDirections.actionCatalogFragmentSelf(
                    nameCategories = str.id.toString(), nameProduct = null, brandId = null
                )
                findNavController().navigate(action)
            } else {
                val action = CatalogNewFragmentDirections.actionCatalogFragmentSelf(
                    nameCategories = str.id.toString(), nameProduct = null, brandId = null
                )
                findNavController().navigate(action)
            }
        }
        adapterCategory.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                if (adapterCategory.selectedId != 1) {
//                    binding.appBarLayout.recyclerViewCategories.smoothScrollToPosition(adapterCategory.selectedId)
                }
            }
        })

        binding.apply {
            val layoutManager = FlexboxLayoutManager(context).apply {
                justifyContent = JustifyContent.SPACE_EVENLY
                alignItems = AlignItems.CENTER
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP

            }
//            recyclerViewProduct.layoutManager = layoutManager
            recyclerViewProduct.layoutManager =
                GridLayoutManager(context, GRIDVIEW_SPAN_COUNT_PRODUCT)
            recyclerViewProduct.adapter = adapterProduct

            Log.d("TAG", "setUpViews: nameTitle : $nameTitle")
            if (nameTitle == "all") {
                appBarLayout.topAppBar.title = getString(R.string.all_product)
            } else {
                val catId = nameTitle?.toIntOrNull() ?: 0
                Log.d("TAG", "setUpViews: catId : $catId")

                if (catId == 0) {
                    appBarLayout.topAppBar.title = getString(R.string.all_product)
                } else {
                    val category = homeViewModel.subCategories.firstOrNull {
                        it.id == catId
                    }
                    Log.d("TAG", "setUpAdapter: $category")
                    appBarLayout.topAppBar.title = category?.name
                }
            }


            if (viewModel.statusSort.value == 1) {
                appBarLayout.btnSort.text = getString(R.string.newest)
            }

            appBarLayout.MaterialToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            appBarLayout.recyclerViewCategories.layoutManager = LinearLayoutManager(
                context, LinearLayoutManager.HORIZONTAL, false
            )

            appBarLayout.recyclerViewCategories.adapter = adapterCategory

            appBarLayout.btnFilter.setOnClickListener {
                if (filterPrice.isNotEmpty()) {
                    val bottomFilter = BottomFilter(filterPrice[0], filterPrice[1])
                    bottomFilter.show(parentFragmentManager, BottomFilter.TAG)
                } else {
                    val bottomFilter = BottomFilter()
                    bottomFilter.show(parentFragmentManager, BottomFilter.TAG)
                }

            }
        }
    }

    override fun setUpObserve() {

        Log.d("TAG", "setUpObserve: nameTitle : $nameTitle")
        viewModel.setCategory(nameTitle.toString())
        viewModel.isLoading.postValue(true)
        viewModel.setSort(DEFAULT_SORT)

        checkInternet()

        loadMoreProduct()

        viewModel.apply {
            homeViewModel.apply {
                homeLiveData.observe(viewLifecycleOwner) {
                    it?.categories?.let { it1: List<Category> ->
                        val list = arrayListOf<SubCategory>()
                        for (category in it1) {
                            list.addAll(category.subs)
                        }
                        adapterCategory.submitList(list)
                        val index =
                            list.indexOfFirst { it.id.toString() == getCategorySelectedId() }
                        val selectedCat = if (index != -1) list[index] else null
                        adapterCategory.selectedId = selectedCat?.id ?: -1
                        if (index != -1) {
//                            binding.appBarLayout.recyclerViewCategories.smoothScrollToPosition(index)
                        }
                    }
                }
            }
            newListProductLiveData.observe(viewLifecycleOwner) { data ->
                data?.let {
                    Log.d(
                        "TAG",
                        "setUpObserve: listProductLiveData : page: $page size: ${it.pagination}"
                    )

                    // Check if currentList is not empty
                    if (currentList.isNotEmpty()) {
                        // Add products from it.products to currentList if not yet on list
                        it.products.forEach { product ->
                            if (!currentList.contains(product)) {
                                currentList.add(product)
                            }
                        }
                    } else {
                        // If currentList is empty, initialize it with new products
                        currentList = it.products.toMutableList()
                    }

                    // Submit the updated list
                    submitList(currentList)
                }
            }

//            statusSort.observe(viewLifecycleOwner) {
//                if (it == DEFAULT_SORT) {
//                    binding.appBarLayout.btnSort.text = getString(R.string.newest)
//                } else if (prevList.isNotEmpty() && it >= 0) {
//                    val list = filterSort(prevList)
//                    prevList = list
////                    updateList()
//                    binding.nestedScrollView.scrollTo(0, 0)
//                }
//            }
            statusSort.observe(viewLifecycleOwner) {
                if (it == DEFAULT_SORT) {
                    binding.appBarLayout.btnSort.text = getString(R.string.newest)
                } else if (currentList.isNotEmpty() && it >= 0) {
                    val list = filterSort(currentList)
                    currentList = list.toMutableList()

                    submitList(currentList)
//                    binding.nestedScrollView.scrollTo(0, 0)
                }
            }

            isLoading.observe(viewLifecycleOwner) {
                if (it) {
                    binding.progressBar.visibility = View.VISIBLE
                } else {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    fun showSort() {

        val select = if (viewModel.statusSort.value == DEFAULT_SORT) {
            1
        } else {
            viewModel.statusSort.value ?: 0
        }
        val bottomSheetSort = BottomSheetSort(select)
        bottomSheetSort.show(parentFragmentManager, BottomSheetSort.TAG)
    }

    private fun submitList(listProduct: MutableList<Product>) {

        currentList = listProduct.toMutableList()
        adapterProduct.submitList(listProduct)
        val index = adapterCategory.selectedId
        if (index != -1) {
            binding.appBarLayout.recyclerViewCategories.smoothScrollToPosition(index)
        }
    }


    override fun setUpViews() {

        binding.apply {
            Log.d("TAG", "setUpViews: nameTitle 1 2: $nameTitle")
            nestedScrollView.isNestedScrollingEnabled = false
            recyclerViewProduct.isNestedScrollingEnabled = false

            appBarLayout.btnSort.setOnClickListener {
                showSort()
            }
            recyclerViewProduct.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        Log.d("TAG", "onScrollStateChanged: ENd of scroll:: ${currentList.size}")
                    }
                }
            })
            nestedScrollView.viewTreeObserver.addOnScrollChangedListener {
                val view = nestedScrollView.getChildAt(nestedScrollView.childCount - 1) as View
//                Log.d("Catalog List", " ${nestedScrollView.childCount}:child")
                val diff: Int = view.bottom - (nestedScrollView.height + nestedScrollView.scrollY)
                if (diff == 0) {
                    Log.d("TAG", "setUpViews:END of scroll diff ${diff}")
                    loadMoreProduct()
                    appBarLayout.root.setExpanded(false, true)
                }
            }
        }
        setFragmentListener()
    }

    private fun loadMoreProduct() {
        viewModel.loadMoreProducts()
    }


    private fun setFragmentListener() {
        setFragmentResultListener(REQUEST_KEY) { _, bundle ->
            val result = bundle.getString(BUNDLE_KEY_NAME)
            val position = bundle.getInt(BUNDLE_KEY_POSITION)
            if (!result.isNullOrBlank()) {
                binding.appBarLayout.btnSort.text = result
                viewModel.setSort(position)
            }
            val min = bundle.getFloat(BUNDLE_KEY_MIN)
            val max = bundle.getFloat(BUNDLE_KEY_MAX)
            if (min >= 0 && max > 0) {
                filterPrice = listOf(min, max)
                viewModel.apply {
                    submitList(filterPrice(min, max, currentList))
                }
                binding.appBarLayout.btnFilter.text =
                    "${min.roundToInt()}-${max.roundToInt()} ${getString(R.string.currency)}"
                binding.nestedScrollView.scrollTo(0, 0)
            } else {
                filterPrice = emptyList()
                submitList(currentList)
                binding.appBarLayout.btnFilter.text = getString(R.string.filters)
                binding.nestedScrollView.scrollTo(0, 0)
            }
            val isFavorite = bundle.getBoolean(BUNDLE_KEY_IS_FAVORITE, false)
            if (isFavorite) {
                viewModel.btnFavorite.value?.let {
                    it.background = ContextCompat.getDrawable(
                        requireContext(), R.drawable.btn_favorite_active
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        viewModel.setSearch("")
//        viewModel.setCategory("")

    }

    companion object {
        const val DEFAULT_SORT = -1
    }
}