package com.bhub.foodi.ui.favorite

import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bhub.foodi.R
import com.bhub.foodi.adapters.ListFavoriteAdapter
import com.bhub.foodi.core.BaseFragment
import com.bhub.foodi.databinding.FragmentFavoritesBinding
import com.bhub.foodi.ui.bag.BagViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : BaseFragment<FragmentFavoritesBinding>(
    FragmentFavoritesBinding::inflate
) {
    override val viewModel: FavoriteViewModel by activityViewModels()
    val bagViewModel: BagViewModel by viewModels()
    private lateinit var adapterFavorite: ListFavoriteAdapter
    override var isHideBottom = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!viewModel.isLogged()) {
            findNavController().navigate(R.id.action_favoritesFragment_to_warningFragment)
        }
    }

    override fun setUpAdapter() {
        adapterFavorite = ListFavoriteAdapter({
            viewModel.removeFavorite(it)
        }, {
            val action = FavoritesFragmentDirections.actionFavoritesFragmentToProductDetailFragment(
                idProduct = it.id
            )
            findNavController().navigate(action)
        }, { buttonView, favoriteAndProduct ->
            bagViewModel.moveToCart(
                favoriteAndProduct
            )
            buttonView.background = ContextCompat.getDrawable(
                requireContext(), R.drawable.btn_bag_active
            )
        }, { view, favorite ->
            viewModel.setButtonBag(requireContext(), view, favorite)
        })
    }

    override fun setUpViews() {
        binding.apply {
            appBarLayout.topAppBar.title = getString(R.string.favorite)

            recyclerViewProduct.layoutManager = LinearLayoutManager(context)
            recyclerViewProduct.adapter = adapterFavorite

            // Handle Search Bar
            appBarLayout.MaterialToolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.ic_search -> {
                        val searchView = it.actionView as SearchView
                        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                            override fun onQueryTextSubmit(query: String?): Boolean {
                                return true
                            }

                            override fun onQueryTextChange(newText: String?): Boolean {
                                if (!newText.isNullOrEmpty()) {
                                    viewModel.favoriteAndProducts.value?.let { list ->
                                        adapterFavorite.submitList(list.filter { favorite ->
                                            favorite.title.lowercase().contains(newText.lowercase())
                                        })
                                    }
                                } else {
                                    adapterFavorite.submitList(
                                        viewModel.favoriteAndProducts.value ?: emptyList()
                                    )
                                }
                                return true
                            }
                        })

                        true
                    }

                    else -> false
                }
            }
        }
    }

    override fun setUpObserve() {
        bagViewModel.onAddToCart.observe(viewLifecycleOwner) {
            if (it != null) {
                viewModel.removeFavorite(it)
            } else {
                toastMessage(getString(R.string.failed))
            }
        }
        viewModel.apply {
            favoriteAndProducts.observe(viewLifecycleOwner) {
                adapterFavorite.submitList(it)
                adapterFavorite.notifyDataSetChanged()
            }

            isLoading.observe(viewLifecycleOwner) {
                setLoading(it)
            }

            isSuccess.observe(viewLifecycleOwner) {
                isLoading.postValue(!it)
            }
        }
    }
}