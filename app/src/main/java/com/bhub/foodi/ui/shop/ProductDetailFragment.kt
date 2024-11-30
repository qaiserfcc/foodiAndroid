package com.bhub.foodi.ui.shop

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bhub.foodi.R
import com.bhub.foodi.adapters.ImageProductAdapter
import com.bhub.foodi.adapters.ListProductGridAdapter
import com.bhub.foodi.core.BaseFragment
import com.bhub.foodi.data.Product
import com.bhub.foodi.databinding.FragmentProductDetailBinding
import com.bhub.foodi.ui.auth.AuthActivity
import com.bhub.foodi.ui.favorite.FavoriteViewModel
import com.bhub.foodi.ui.general.CartDialogFragment
import com.bhub.foodi.ui.home.HomeViewModel
import com.bhub.foodi.utilities.BUNDLE_KEY_IS_FAVORITE
import com.bhub.foodi.utilities.ID_PRODUCT
import com.bhub.foodi.utilities.NetworkHelper
import com.bhub.foodi.utilities.REQUEST_KEY
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDetailFragment : BaseFragment<FragmentProductDetailBinding>(
    FragmentProductDetailBinding::inflate
) {
    override val viewModel: ShopViewModel by viewModels()
    val homeViewModel: HomeViewModel by viewModels()
    private val favViewModel: FavoriteViewModel by viewModels()

    private lateinit var adapterRelated: ListProductGridAdapter
    private var isDialogShown = false

    //    private lateinit var colors: MutableList<String>
//    private lateinit var sizes: MutableList<String>
//    private var selectSize: Int = 0
//    private var selectColor: Int = 0
    private var idProduct = ""
    private val handlerFragment = Handler(Looper.getMainLooper())

    override fun setUpArgument(bundle: Bundle) {
        arguments?.let {
            idProduct = it.getString(ID_PRODUCT) ?: ""
            Log.d("TAG", "setUpArgument:idProduct: $idProduct ")
            if (idProduct.isNotBlank()) {
                viewModel.isLoading.postValue(true)
                viewModel.setProduct(idProduct)
                viewModel.getProductDetail(idProduct)
            } else {
                findNavController().navigateUp()
            }
        }
        checkInternet()
    }

    override fun setUpAdapter() {
        adapterRelated = ListProductGridAdapter({
            if (it.id != idProduct) {
                viewModel.isLoading.postValue(true)
                viewModel.setProduct(it.id)
                idProduct = it.id
                viewModel.getProductDetail(idProduct)
                binding.nestedScrollView.apply {
                    scrollTo(0, 0)
                }
            }
        }, { btnFavorite, product ->
//            val bottomSheetSize = BottomSheetFavorite(product, null, null)
//            viewModel.btnFavorite.postValue(btnFavorite)
//            bottomSheetSize.show(parentFragmentManager, BottomSheetFavorite.TAG)
        }, { view, product ->
            viewModel.setButtonFavorite(requireContext(), view, product.id)
        })
    }

    val cartDialogFragment by lazy {
        CartDialogFragment({}, {
            findNavController().navigate(R.id.bagFragment)
        })
    }

    override fun setUpObserve() {

        viewModel.apply {
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                homeViewModel.productSelected.collect {
                    it?.let { it1 -> bind(it1) }
                }
            }
            onAddToCart.observe(viewLifecycleOwner) { it ->
                if (it != null) {
                    if (!isDialogShown) {
                        cartDialogFragment.show(parentFragmentManager, "GoCart")
                        isDialogShown = true
                    }
                } else {
                    toastMessage(getString(R.string.failed))

                }
            }
            toastMessage.observe(viewLifecycleOwner) { str ->
                toastMessage(str)

            }
            onErrorLiveData.observe(viewLifecycleOwner) {
                toastMessage(it)
            }
            productDetailLiveData.observe(viewLifecycleOwner) { data ->
                data?.product?.let {
//                    colors = viewModel.getAllColor()
//                    sizes = viewModel.getAllSize()
//                    selectSize = 0
//                    selectColor = 0
                    viewModel.setCategory(it.categoryName)
                    bind(it)
                }
                setRelatedAdapter(data?.related)
            }

            products.observe(viewLifecycleOwner) {

            }

            isLoading.observe(viewLifecycleOwner) {
                setLoading(it)
            }
        }
    }

    private fun setRelatedAdapter(related: List<Product>?) {

        binding.recyclerViewProduct.layoutManager = LinearLayoutManager(
            context, LinearLayoutManager.HORIZONTAL, false
        )
        binding.recyclerViewProduct.adapter = adapterRelated

        adapterRelated.submitList(related)
//        binding.txtNumberRelated.text = getString(R.string.items, (related?.size ?: 1) - 1)
    }

    override fun setUpViews() {
        setFragmentListener()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

//    fun changePrice() {
//        val product = viewModel.product.value
//        product?.let {
//            binding.txtPrice.text = "\$${it.colors[selectColor].sizes[selectSize].price}"
//        }
//    }

    fun bind(product: Product) {
        binding.apply {
            MaterialToolbar.title = product.title

            MaterialToolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            MaterialToolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.btnShare -> {
                        shareProduct(product.title, product.getThumbnails())
                        false
                    }

                    else -> false
                }
            }
            //Set Detail
            viewPagerImageProduct.apply {
                if (NetworkHelper.isNetworkAvailable(requireContext())) {
                    Log.d("ProductDetailFragment", "bind: product:: $product")
                    val adapterImage =
                        ImageProductAdapter(this@ProductDetailFragment, product.gallery,
                            fun(it: Int) {
                                touchImage(
                                    product.gallery, it
                                )
                            })
                    adapter = adapterImage
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
                } else {
                    val images = listOf(product.getThumbnails())
                    val adapterImage = ImageProductAdapter(this@ProductDetailFragment, images) {
                        touchImage(
                            product.gallery, it
                        )
                    }
                    adapter = adapterImage
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

            txtBrandName.text = product.brandName
            txtTitle.text = product.title
//            txtDescription.text = product.description
            ratingBar.rating = product.reviewStars
            txtNumberVote.text = "(${product.numberReviews})"
            txtPrice.text = "${product.getPrice()} ${getString(R.string.currency)}"
            if (product.salePercent > 0) {
                txtPriceStrike.visibility = View.VISIBLE
                txtPriceStrike.text =
                    "${product.getPreviousPrice()} ${getString(R.string.currency)}"
                txtPriceStrike.paintFlags = txtPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                txtPriceStrike.visibility = View.GONE
            }
            if (product.stock != -1) {
                txtStock.text = "${product.stock} ${getString(R.string.in_stock)}"
            } else {
                txtStock.text = "${getString(R.string.in_stock)}"
            }
            txtDescription.text =
                HtmlCompat.fromHtml(product.description, HtmlCompat.FROM_HTML_MODE_LEGACY)

            //Spinner Size
//            val adapterSize = SpinnerAdapter(requireContext(), sizes)
//            spinnerSize.adapter = adapterSize
//            spinnerSize.setSelection(0)
//            spinnerSize.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(
//                    parent: AdapterView<*>?,
//                    view: View?,
//                    position: Int,
//                    id: Long
//                ) {
//                    selectSize = position
//                    changePrice()
//                }
//
//                override fun onNothingSelected(parent: AdapterView<*>?) {
//                }
//
//            }

            //Spinner Color
            /* val adapterColor = SpinnerAdapter(requireContext(), colors)
             spinnerColor.adapter = adapterColor
             spinnerColor.setSelection(0)
             spinnerColor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                 override fun onItemSelected(
                     parent: AdapterView<*>?,
                     view: View?,
                     position: Int,
                     id: Long
                 ) {
                     selectColor = position
                     changePrice()
                 }

                 override fun onNothingSelected(parent: AdapterView<*>?) {
                 }

             }*/


            //Set Button Favorite
            viewModel.setButtonFavorite(requireContext(), btnFavorite, product.id)

            btnFavorite.setOnClickListener {
                viewModel.btnFavorite.postValue(btnFavorite)
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
            }
            btnAddToCart.setOnClickListener {
                if (viewModel.userManager.isLogged()) {
                    viewModel.addToCart(product)
                } else {
                    toastMessage(getString(R.string.please_login_to_use_this_function))
                    startActivity(Intent(activity, AuthActivity::class.java))
                }
//                val bottomSheetCart = BottomSheetCart(product, selectSize, selectColor)
//                bottomSheetCart.show(parentFragmentManager, BottomSheetCart.TAG)
            }

            btnRatingBar.setOnClickListener {
                if (product.numberReviews == 0) {
                    toastMessage(getString(R.string.no_reviews))
                    return@setOnClickListener
                }
                val action =
                    ProductDetailFragmentDirections.actionProductDetailFragmentToRatingProductFragment(
                        idProduct = idProduct
                    )
                findNavController().navigate(action)
            }
            btnGeneral.setOnClickListener {
                if (product.numberReviews == 0) {
                    toastMessage(getString(R.string.no_reviews))
                    return@setOnClickListener
                }
                val action =
                    ProductDetailFragmentDirections.actionProductDetailFragmentToRatingProductFragment(
                        idProduct = idProduct
                    )
                findNavController().navigate(action)
            }

            viewModel.isLoading.postValue(false)
        }
    }

    fun shareProduct(productName: String, productLink: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, productName)
            putExtra(Intent.EXTRA_TEXT, productLink)
        }

        startActivity(Intent.createChooser(shareIntent, "Share Product via"))
    }


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
            binding.viewPagerImageProduct.setCurrentItem(
                binding.viewPagerImageProduct.currentItem + 1, true
            )
        }, 5000)
    }

    override fun onPause() {
        handlerFragment.removeMessages(0)
        super.onPause()
    }

    override fun onResume() {
        autoScroll()
        super.onResume()
    }

    override fun onDestroy() {
        handlerFragment.removeMessages(0)
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Reset the flag when the view is destroyed
        isDialogShown = false
    }
}