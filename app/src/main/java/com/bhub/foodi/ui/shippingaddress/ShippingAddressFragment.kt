package com.bhub.foodi.ui.shippingaddress

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bhub.foodi.R
import com.bhub.foodi.adapters.ListAddressAdapter
import com.bhub.foodi.core.BaseFragment
import com.bhub.foodi.databinding.FragmentShippingAddressBinding
import com.bhub.foodi.ui.general.ConfirmDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShippingAddressFragment : BaseFragment<FragmentShippingAddressBinding>(
    FragmentShippingAddressBinding::inflate
) {
    private lateinit var listAddressAdapter: ListAddressAdapter
    override val viewModel: ShippingAddressViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun setUpAdapter() {
        listAddressAdapter = ListAddressAdapter({ checkBox, shippingAddress ->
            checkBox.isChecked =
                viewModel.checkDefaultShippingAddress(shippingAddress.id.toString())
        }, { checkBox, shippingAddress ->
            if (checkBox.isChecked) {
                viewModel.setDefaultAddress(shippingAddress.id.toString())
                listAddressAdapter.notifyDataSetChanged()
            } else {
                viewModel.removeDefaultAddress()
                listAddressAdapter.notifyDataSetChanged()
            }
        }, {
            val action =
                ShippingAddressFragmentDirections.actionShippingAddressFragmentToAddShippingAddressFragment(
                    idAddress = it.id.toString()
                )
            findNavController().navigate(action)
        }, {
            ConfirmDialog(this) {
                viewModel.deleteShippingAddress(it)
            }.show()
        })
    }

    override fun setUpObserve() {
        viewModel.fetchAddress()

        viewModel.listAddress.observe(viewLifecycleOwner) {
            listAddressAdapter.submitList(it)
        }
    }

    override fun setUpViews() {
        binding.apply {
            appBarLayout.MaterialToolbar.title = getString(R.string.shipping_addresses)
            appBarLayout.MaterialToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            recyclerViewAddress.adapter = listAddressAdapter
            recyclerViewAddress.layoutManager = LinearLayoutManager(context)

            btnAddShippingAddress.setOnClickListener {
                findNavController().navigate(R.id.addShippingAddressFragment)
            }
        }
    }
}