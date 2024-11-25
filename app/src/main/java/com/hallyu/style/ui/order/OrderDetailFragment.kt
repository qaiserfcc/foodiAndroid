package com.hallyu.style.ui.order

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hallyu.style.R
import com.hallyu.style.adapters.ListProductOrderAdapter
import com.hallyu.style.core.BaseFragment
import com.hallyu.style.data.Order
import com.hallyu.style.data.ProductOrder
import com.hallyu.style.databinding.FragmentOrderDetailBinding
import com.hallyu.style.ui.general.LoadingDialog
import com.hallyu.style.utilities.DateFormat
import com.hallyu.style.utilities.ID_ORDER
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderDetailFragment : BaseFragment<FragmentOrderDetailBinding>(
    FragmentOrderDetailBinding::inflate
) {
    override val viewModel: OrderViewModel by viewModels()
    private val adapter = ListProductOrderAdapter()
    private val loadingDialog = LoadingDialog(this)
    private lateinit var listProductOrder: List<ProductOrder>

    override fun setUpArgument(bundle: Bundle) {
        arguments?.let {
            val idOrder = it.getString(ID_ORDER) ?: ""
            if(idOrder.isNotBlank()){
                viewModel.setIdOrder(idOrder)
                viewModel.isLoading.postValue(true)
            }
        }
    }

    override fun setUpViews() {
        binding.apply {
            appBarLayout.MaterialToolbar.title = getString(R.string.order_details)
            appBarLayout.MaterialToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            btnReOrder.setOnClickListener {
//                loadingDialog.startLoading()
//                viewModel.reOrder(listProductOrder)
                findNavController().navigateUp()
            }
        }
    }

    override fun setUpObserve() {
        viewModel.apply {
            order.observe(viewLifecycleOwner) {
                if(it != null && it.id.isNotBlank()){
                    listProductOrder = it.products
                    setupUI(it)
                    viewModel.isLoading.postValue(false)
                }
            }
            dismiss.observe(viewLifecycleOwner) {
                if (it) {
                    loadingDialog.dismiss()
                    findNavController().navigateUp()
                }
            }
            viewModel.isLoading.observe(viewLifecycleOwner){
                setLoading(it)
            }
        }
    }

    private fun setupUI(order: Order) {
        binding.apply {
            order.apply {
                txtIdOrder.text = "Order No.$id"
                txtTrackingNumber.text = trackingNumber
                timeCreated.let {
                    txtTimeCreated.text = DateFormat.default.format(it).toString()
                }

                viewModel.setUIStatus(requireContext(), txtStatus, status)
                txtNumberProduct.text = "${products.size} items"
                adapter.submitList(products)
                recyclerViewProduct.adapter = adapter
                recyclerViewProduct.layoutManager = LinearLayoutManager(context)

                txtShippingAddress.text = shippingAddress?.address

                txtNumberCard.text = " $payment"
//                if (isTypePayment == 0) {
//                    imgLogoCard.setImageResource(R.drawable.ic_mastercard)
//                } else {
//                    imgLogoCard.setImageResource(R.drawable.ic_visa2)
//                }

                txtDeliveryMethod.text = "${delivery?.title}, ${delivery?.subtitle}, ${delivery?.price} ${getString(R.string.currency)}"

                viewModel.setUIStatus(requireContext(), txtStatus, status)
                if (promotion?.code.isNullOrEmpty()) {
                    txtDiscountMethod.text = ""
                } else {
                    txtDiscountMethod.text = "${promotion?.discount} ${getString(R.string.currency)}"
                }
                txtTotalAmount.text = "$total ${getString(R.string.currency)}"
            }
        }
    }
}