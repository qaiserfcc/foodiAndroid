package com.bhub.foodi.ui.shop

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bhub.foodi.R
import com.bhub.foodi.adapters.ListCategoriesAdapter2
import com.bhub.foodi.core.BaseFragment
import com.bhub.foodi.databinding.FragmentShopBinding
import com.bhub.foodi.ui.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ShopFragment : BaseFragment<FragmentShopBinding>(
    FragmentShopBinding::inflate
) {
    override val viewModel: HomeViewModel by activityViewModels()
    override var isHideBottom = false
    private lateinit var adapterCategory: ListCategoriesAdapter2


    override fun setUpAdapter() {
        adapterCategory = ListCategoriesAdapter2 { str ->
            val action = ShopFragmentDirections.actionShopFragmentToCatalogFragment(
                nameCategories = str,
                nameProduct = null, brandId = null
            )
            findNavController().navigate(action)
        }
        checkInternet()
    }

    override fun setUpObserve() {
        viewModel.apply {
//            homeLiveData.observe(viewLifecycleOwner) {
//                it?.categories?.let {
//                    val list = arrayListOf<String>()
//                    for (category in it) {
////                        list.addAll(category.getSubcategories())
//                    }
//                    Log.d("TAG", "setUpObserve: list.size = ${list.size}")
//                    adapterCategory.submitList(list)
//                }
//            }
        }
    }

    override fun setUpViews() {
        binding.apply {
            MaterialToolbar.title = getString(R.string.shop)
            recyclerViewCategories.layoutManager = LinearLayoutManager(context)
            recyclerViewCategories.adapter = adapterCategory

            btnViewAllItems.setOnClickListener {
                val action = ShopFragmentDirections.actionShopFragmentToCatalogFragment(
                    nameCategories = "",
                    nameProduct = null, brandId = null
                )
                findNavController().navigate(action)
            }

            // Handle Search Bar
            MaterialToolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.ic_search -> {
                        findNavController().navigate(R.id.searchFragment)
                        true
                    }

                    else -> false
                }
            }
        }
    }
}