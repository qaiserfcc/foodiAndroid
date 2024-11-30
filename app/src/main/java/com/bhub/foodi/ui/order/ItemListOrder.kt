package com.bhub.foodi.ui.order

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bhub.foodi.R
import com.bhub.foodi.adapters.ListOrderAdapter
import com.bhub.foodi.databinding.ItemViewPagerListOrderBinding
import com.bhub.foodi.core.BaseFragment
import com.bhub.foodi.utilities.ID_ORDER
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ItemListOrder(private val statusType: Int) : BaseFragment<ItemViewPagerListOrderBinding>(
    ItemViewPagerListOrderBinding::inflate
) {
    override val viewModel: OrderViewModel by viewModels()
    private lateinit var adapter: ListOrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.isLoading.postValue(true)
    }

    override fun setUpAdapter() {
        adapter = ListOrderAdapter({
            findNavController().navigate(R.id.orderDetailFragment, bundleOf(ID_ORDER to it))
        }, { order, textView ->
            viewModel.setUIStatus(requireContext(), textView, order.status)
        })
    }

    override fun setUpViews() {
        binding.apply {
            recyclerViewOrder.adapter = adapter
            recyclerViewOrder.layoutManager = LinearLayoutManager(context)
        }
    }

    override fun setUpObserve() {
        viewModel.apply {
            getOrderStatus(statusType).observe(viewLifecycleOwner) { data ->
                adapter.submitList(data)
                viewModel.isLoading.postValue(false)
            }

            isLoading.observe(viewLifecycleOwner) {
                setLoadingCustom(binding.progressBar, it)
            }
        }

    }

}