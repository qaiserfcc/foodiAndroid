package com.hallyu.style.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hallyu.style.R
import com.hallyu.style.adapters.ListOrderAdapter
import com.hallyu.style.databinding.ItemViewPagerListOrderBinding
import com.hallyu.style.core.BaseFragment
import com.hallyu.style.utilities.ID_ORDER
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

            isLoading.observe(viewLifecycleOwner){
                setLoadingCustom(binding.progressBar,it)
            }
        }

    }

}