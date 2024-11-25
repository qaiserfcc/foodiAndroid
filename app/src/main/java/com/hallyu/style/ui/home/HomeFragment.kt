package com.hallyu.style.ui.home

import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.tabs.TabLayout
import com.hallyu.style.R
import com.hallyu.style.adapters.HomeBrandsAdapter
import com.hallyu.style.adapters.HomeCategoriesAdapter
import com.hallyu.style.adapters.ImageHomeAdapter
import com.hallyu.style.adapters.ListHomeAdapter
import com.hallyu.style.adapters.ListProductGridAdapter
import com.hallyu.style.adapters.ListProductGridAdapter2
import com.hallyu.style.core.BaseFragment
import com.hallyu.style.data.Product
import com.hallyu.style.data.ProductModel
import com.hallyu.style.databinding.FragmentHomeBinding
import com.hallyu.style.response.Category
import com.hallyu.style.response.HomeData
import com.hallyu.style.response.Slider
import com.hallyu.style.ui.favorite.BottomSheetFavorite
import com.hallyu.style.ui.favorite.FavoriteViewModel
import com.hallyu.style.utilities.BUNDLE_KEY_IS_FAVORITE
import com.hallyu.style.utilities.KEY_CATEGORY
import com.hallyu.style.utilities.NEW
import com.hallyu.style.utilities.NetworkHelper
import com.hallyu.style.utilities.PreferencesManager
import com.hallyu.style.utilities.REQUEST_KEY
import com.hallyu.style.utilities.SALE
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(
    FragmentHomeBinding::inflate
) {
    private lateinit var adapter: ListHomeAdapter
    override var isHideBottom = false
    override var isFullScreen = true

    override val viewModel: HomeViewModel by activityViewModels()
    private val favViewModel: FavoriteViewModel by viewModels()
//    private var category: List<String> = emptyList()
    private var product: MutableMap<String, List<Product>> = mutableMapOf()
    private var productModels: MutableMap<String, List<ProductModel>> = mutableMapOf()
    private val handlerFragment = Handler(Looper.getMainLooper())

    private val sliderUrls :MutableList<String> = mutableListOf()
    private val sliderTitles :MutableList<String> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Check Tutorial
//        val sharedPref = this.activity?.getPreferences(Context.MODE_PRIVATE)
//        val isFirst = sharedPref?.getBoolean(IS_FIRST, true);
//        if (isFirst == true) {
//            sharedPref.edit().putBoolean(IS_FIRST, false).apply()
//            findNavController().navigate(R.id.viewPageTutorialFragment)
//        }

        setFragmentListener()

    }

    override fun setUpAdapter() {
        viewModel.isLoading.postValue(true)
        adapter = ListHomeAdapter { recyclerView, textView, s ->
            val adapterItem = ListProductGridAdapter({
                viewModel.productSelected.value = it
                val action = HomeFragmentDirections.actionHomeFragmentToProductDetailFragment(
                    idProduct = it.id
                )
                findNavController().navigate(action)
            }, { btnFavorite, product ->
                val tag = btnFavorite.tag as Boolean? ?: false
                Log.d("HomeFragment", "setUpAdapter: tag:$tag")
                if (tag){
                    favViewModel.removeFavorite(product)
                    Log.d("HomeFragment", "setUpAdapter: removeFavorite")
                    btnFavorite.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.btn_favorite_no_active
                    )
                }else {
                    favViewModel.insertFavorite(product)
                    Log.d("HomeFragment", "setUpAdapter: insertFavorite")
                    btnFavorite.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.btn_favorite_active
                    )
                }
                btnFavorite.tag = !tag
//                viewModel.setButtonFavorite(requireContext(), btnFavorite, product.id)
//                val bottomSheetSize = BottomSheetFavorite(product, null, null)
//                bottomSheetSize.show(parentFragmentManager, BottomSheetFavorite.TAG)
//                    .observe(viewLifecycleOwner) {
//                        dismiss()
//                        sendData()
//                    }
//                btnFavorite.background = ContextCompat.getDrawable(
//                    requireContext(), R.drawable.btn_favorite_active
//                )
            }, { view, product ->
                viewModel.setButtonFavorite(requireContext(), view, product.id)
            })
            adapterItem.submitList(product[s])
            val layoutManager = FlexboxLayoutManager(context).apply {
                justifyContent = JustifyContent.SPACE_EVENLY
                alignItems = AlignItems.FLEX_START
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP

            }
//            recyclerView.addItemDecoration(LastOddItemStartAlignmentDecoration())
            recyclerView.adapter = adapterItem
            recyclerView.layoutManager = layoutManager
            textView.visibility = View.GONE
//            textView.setOnClickListener {
//                val action = HomeFragmentDirections.actionHomeFragmentToCatalogFragment(
//                    nameCategories = "all", nameProduct = null
//                )
//
//                findNavController().navigate(action)
//            }
        }
    }

    override fun setUpViews() {
        binding.apply {
            MaterialToolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.ic_search -> {
                        findNavController().navigate(R.id.searchFragment)
                        true
                    }

                    else -> false
                }
            }
            recyclerListHome.adapter = adapter
            recyclerListHome.layoutManager = LinearLayoutManager(context)
            adapter.submitList(product.keys.toList())

//            if (viewModel.isLoading.value == false) {
//                viewModel.isLoading.postValue(false)
//            }

        }
    }


    override fun setUpObserve() {
        viewModel.apply {
//            category.observe(viewLifecycleOwner) {
//                this@HomeFragment.category = it
//                loadMore.postValue(true)
//            }
            homeError.observe(viewLifecycleOwner) {
//                showErrorFragment()
//                toastMessage(getString(R.string.failed))
                Log.d("TAG", "setUpObserve:toastMessage(getString(R.string.failed)) ")
            }
            homeLiveData.observe(viewLifecycleOwner) {
                Log.d("TAG", "setUpObserve: Api Response ${it != null}")
                if (it != null) {
                    updateUI(it)

                }else{
                    Toast.makeText(requireContext(), "Failed, please try again", Toast.LENGTH_SHORT).show()
                }

            }


            isLoading.observe(viewLifecycleOwner) {
                if (it) {
                    binding.progressBar.visibility = View.VISIBLE
//                    setupScroll()
                } else {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun updateUI(data: HomeData) {
        Log.d(this.javaClass.simpleName, "set adapter: debug::updateUI ")
        PreferencesManager.put(data.categories,KEY_CATEGORY)
        setSliders(data.sliders)

        binding.rvHomeCategories.layoutManager = GridLayoutManager(
            context, 3
        )
        val homeCategoriesAdapter = HomeCategoriesAdapter(){
            val action = HomeFragmentDirections.actionHomeFragmentToCatalogFragment(
                nameCategories = it.id.toString(), nameProduct = null, brandId = null
            )
            findNavController().navigate(action)
        }
        val brandsAdapter = HomeBrandsAdapter(){
            val action = HomeFragmentDirections.actionHomeFragmentToCatalogFragment(
                nameCategories = null, nameProduct = null, brandId = "${it.id}"
            )
            findNavController().navigate(action)
        }

        brandsAdapter.submitList(data.brands)
        binding.rvBrands.adapter = brandsAdapter

        product[getString(R.string.best)] = data.best
        product[getString(R.string.featured)] = data.featured
        adapter.submitList(product.keys.toList())

        binding.tabCategories.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val id = tab?.tag as Int?
                val categoryModels: Category = data.categories.firstOrNull { it.id == id } ?: return
                homeCategoriesAdapter.submitList(categoryModels.subs)
                binding.rvHomeCategories.adapter = homeCategoriesAdapter
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {
                    onTabSelected(tab)
                }
            }

        })
        binding.tabCategories.removeAllTabs()
        data.categories.forEachIndexed { i, category ->
            val first = i==0
            Log.i(this.javaClass.simpleName, "updateUI:forEachIndexed is it first: $first ")
            val newTab: TabLayout.Tab = binding.tabCategories.newTab()
            newTab.setText(category.name)
            newTab.tag = category.id
            binding.tabCategories.addTab(newTab, first)
        }
    }

    private fun setSliders(sliders: List<Slider>) {
        binding.viewPagerHome2.apply {
            Log.d(this.javaClass.simpleName, "set adapter: debug::2")

            sliders.forEach {
                sliderUrls.add(it.getImageUrl())
                sliderTitles.add(it.title?:"")
            }
            Log.d(this.javaClass.simpleName, "set adapter: debug::3 ::sliderUrls.size: ${sliderUrls.size}")


            if (NetworkHelper.isNetworkAvailable(requireContext())) {
                val adapterImage = ImageHomeAdapter(this@HomeFragment, sliderUrls, sliderTitles)
                adapter = adapterImage
                Log.d(this.javaClass.simpleName, "set adapter: debug::4")

                setCurrentItem(1, false)
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageScrollStateChanged(state: Int) {
                        super.onPageScrollStateChanged(state)
                        if (state == ViewPager2.SCROLL_STATE_IDLE) {
                            when (currentItem) {
                                adapterImage.itemCount - 1 -> setCurrentItem(1, false)
                                0 -> setCurrentItem(adapterImage.itemCount - 2, false)
                            }
                        }
                    }
                })
            }

            autoScroll()
            //Auto scroll
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    autoScroll()
                }
            })
        }

        viewModel.isLoading.postValue(false)
    }

    /*private fun setupScroll() {

        binding.apply {

            nestedScrollView.viewTreeObserver?.addOnScrollChangedListener {
                nestedScrollView.apply {
                    if (view != null) {
                        val view = getChildAt(0)
                        if (view != null) {
                            val diff = view.bottom - (height + scrollY)
                            if (diff <= 0) {
                                if (viewModel.loadMore.value == true) {
                                    viewModel.isLoading.postValue(true)
                                    viewModel.loadMore.postValue(false)
                                    if (product.size < category.size + 2) {
                                        viewModel.apply {
                                            if (product.isEmpty()) {
                                                getSaleProduct().observe(viewLifecycleOwner) {
                                                    if (it.isNotEmpty()) {
                                                        product[SALE] = it
                                                        adapter.submitList(product.keys.toList())
                                                        viewModel.isLoading.postValue(false)
                                                        viewModel.loadMore.postValue(true)
                                                    }
                                                }
                                            }
                                            else if (product.size == 1 && checkSale.value == false) {
                                                getNewProduct().observe(viewLifecycleOwner) { list ->
                                                    if (list.isNotEmpty()) {
                                                        product[NEW] = list
                                                        adapter.submitList(product.keys.toList())
                                                        viewModel.isLoading.postValue(false)
                                                        viewModel.loadMore.postValue(true)
                                                    }
                                                }
                                            } else {
                                                val index = product.size - 2
                                                if (index >= 0) {
                                                    getProductWithCategory(this@HomeFragment.category[index]).observe(
                                                            viewLifecycleOwner
                                                        ) {
                                                            if (it.isNotEmpty()) {
                                                                product[this@HomeFragment.category[index]] =
                                                                    it
                                                                adapter.submitList(product.keys.toList())
                                                                viewModel.isLoading.postValue(false)
                                                                viewModel.loadMore.postValue(true)
                                                            }
                                                        }
                                                }
                                            }
                                        }
                                    } else {
                                        viewModel.isLoading.postValue(false)
                                        viewModel.loadMore.postValue(false)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }*/

    private fun setFragmentListener() {
        setFragmentResultListener(REQUEST_KEY) { _, bundle ->
            val result = bundle.getBoolean(BUNDLE_KEY_IS_FAVORITE, false)
            if (result) {
                viewModel.btnFavorite.value?.let {
                    it.background = ContextCompat.getDrawable(
                        requireContext(), R.drawable.btn_favorite_active
                    )
                }
            }
        }
    }


    private fun autoScroll() {
        handlerFragment.removeMessages(0)
        handlerFragment.postDelayed({
            binding.viewPagerHome2.setCurrentItem(binding.viewPagerHome2.currentItem + 1, true)
        }, 5000)
    }

    override fun onPause() {
        handlerFragment.removeMessages(0)
        super.onPause()
    }

    override fun onResume() {
        autoScroll()
        viewModel.getHomeIndex()
        super.onResume()
    }

    override fun onDestroy() {
        handlerFragment.removeMessages(0)
        super.onDestroy()
    }
}

class LastOddItemStartAlignmentDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {

    }
}
