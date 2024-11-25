package com.hallyu.style.ui.bag

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hallyu.style.R
import com.hallyu.style.adapters.ListBagAdapter2
import com.hallyu.style.core.BaseFragment
import com.hallyu.style.databinding.FragmentBagBinding
import com.hallyu.style.utilities.ID_PROMOTION
import com.hallyu.style.utilities.NetworkHelper
import com.hallyu.style.utilities.WARNING_CHECK_INTERNET
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BagFragment : BaseFragment<FragmentBagBinding>(
    FragmentBagBinding::inflate
) {
    override val viewModel: BagViewModel by activityViewModels()
    override val color = R.color.grey2
    override var isHideBottom = false

    private lateinit var adapterBag: ListBagAdapter2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!viewModel.isLogged()) {
            findNavController().navigate(R.id.action_bagFragment_to_warningFragment)
        }
    }

    override fun setUpAdapter() {
        adapterBag = ListBagAdapter2({
            val action = BagFragmentDirections.actionBagFragmentToProductDetailFragment(
                idProduct = it.product.id
            )
            findNavController().navigate(action)
        }, {//delete
            viewModel.removeCartItem(it)
        }, { list, textview, textviewPrice ->
            viewModel.plusQuantity(list, textview, textviewPrice)
        }, { list, textview, textviewPrice ->
            viewModel.minusQuantity(list, textview, textviewPrice)
        })
    }


    override fun setUpObserve() {
        viewModel.apply {
            fetchBag()
            onCartOrders.observe(viewLifecycleOwner) {
//                val sale = promotion.value?.discount ?: 0
//                getPromotion("")
                calculatorTotal(it)
                adapterBag.submitList(it)
                isLoading.postValue(false)
            }

            onUpdateCart.observe(viewLifecycleOwner) {
                getPromotion("")
                toastMessage(getString(R.string.updated_cart_item))
            }
            onRemoveCart.observe(viewLifecycleOwner) {
                if (it != null) {
                    toastMessage(getString(R.string.removed_cart_item))
                    fetchBag()
                } else {
                    toastMessage(getString(R.string.failed))
                }
            }


//            bagAndProduct.observe(viewLifecycleOwner) {
//                val sale = promotion.value?.salePercent ?: 0
//                calculatorTotal(it, sale)
//                adapterBag.submitList(it)
//                adapterBag.notifyDataSetChanged()
//                isLoading.postValue(false)
//            }

            toastMessage.observe(viewLifecycleOwner) {
                toastMessage(it)

            }

            totalPrice.observe(viewLifecycleOwner) { it1 ->
                binding.txtPriceTotal.text =
                    "${it1} ${requireContext().getString(R.string.currency)}"
                promotion.value?.let {it->
                    if (it.code.isNullOrEmpty()) {
                        binding.tvDiscount.text = getString(R.string.no_promo_code)
                        isRemoveButton.postValue(false)
                    }else if (it.value.equals("None",true)){
                        binding.tvDiscount.text = getString(R.string.invalid_coupon_code)
                        isRemoveButton.postValue(false)
                    }else{

                        binding.tvDiscount.text = getString(R.string.coupon_code_applied)
                        isRemoveButton.postValue(true)
                    }
                }

            }

            statusPromo.observe(viewLifecycleOwner) {
                Log.d("TAG", "setUpObserve: statusPromo.observe value: $it")
                if (it=="400") {
                    binding.tvDiscount.text = getString(R.string.coupon_code_not_valid)
                    binding.editPromoCode.setText("")
                }else{
                    binding.tvDiscount.text = getString(R.string.invalid_coupon_code)
                }
            }
            promotion.observe(viewLifecycleOwner) {
                Log.d("TAG", "setUpObserve: promotion.observe value: : $it")

                if (it.code.isNullOrEmpty()) {
                    binding.tvDiscount.text = getString(R.string.no_promo_code)
                    isRemoveButton.postValue(false)
                }else if (it.value.equals("None",true)){
                    binding.tvDiscount.text = getString(R.string.invalid_coupon_code)
                    isRemoveButton.postValue(false)
                }else{

                    binding.tvDiscount.text = getString(R.string.coupon_code_applied)
                    isRemoveButton.postValue(true)
                }
                totalPrice.postValue(it.newTotal.toDouble())
               /* if (it.value.equals("None", true)) {
                    if (!it.code.isNullOrEmpty()) {
                        binding.tvDiscount.text = getString(R.string.invalid_coupon_code)
                    } else {
                        binding.tvDiscount.text = getString(R.string.no_promo_code)
                    }
                    viewModel.isRemoveButton.postValue(false)
                } else {
                    if (it.code != null) {
                        binding.tvDiscount.text = getString(R.string.coupon_code_applied)

                        it.discount.let { it1 ->
                            if (it1 > 0) {
                                binding.tvDiscountPrice.text =
                                    "${it1} ${requireContext().getString(R.string.currency)}"
                            } else {
                                binding.tvDiscountPrice.text =
                                    "0 ${requireContext().getString(R.string.currency)}"

                            }
                        }
                    }
                    viewModel.isRemoveButton.postValue(true)
                } */
//                bagAndProduct.value?.let { list ->
//                    val sale = promotion.value?.salePercent ?: 0
//                    calculatorTotal(list, sale)
//                }
            }

            isRemoveButton.observe(viewLifecycleOwner) {
                setButtonRemove(it)

            }

            isLoading.observe(viewLifecycleOwner) {
                setLoading(it)
            }
        }
    }


    override fun setUpViews() {
        binding.apply {
            appBarLayout.topAppBar.title = getString(R.string.my_bag)

            recyclerViewBag.layoutManager = LinearLayoutManager(context)
            recyclerViewBag.adapter = adapterBag

            // Handle Search Bar
            appBarLayout.MaterialToolbar.setOnMenuItemClickListener { it ->
                when (it.itemId) {
                    R.id.ic_search -> {
                        val searchView = it.actionView as SearchView
                        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                            override fun onQueryTextSubmit(query: String?): Boolean {
                                return true
                            }

                            override fun onQueryTextChange(newText: String?): Boolean {
                                viewModel.onCartOrders.value?.let { list ->
                                    if (!newText.isNullOrEmpty()) {
                                        val newList = list.filter { bagAndProduct ->
                                            bagAndProduct.product.title.lowercase()
                                                .contains(newText.lowercase())
                                        }
                                        adapterBag.submitList(newList)
                                    } else {
                                        adapterBag.submitList(list)
                                    }
                                }
                                return true
                            }
                        })
                        true
                    }

                    else -> false
                }
            }

            btnRemove.setOnClickListener {
                binding.editPromoCode.clearFocus()
                (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                    binding.editPromoCode.windowToken,
                    0
                )
                viewModel.isRemoveButton.value?.let {
                    if (it) {
                        Log.d("TAG", "setUpViews: Remove Coupon")
                        viewModel.getPromotion("")
                    } else {
                        Log.d("TAG", "setUpViews: Apply Coupon")
                        val code = editPromoCode.text.toString()
                        viewModel.getPromotion(code)
                    }
                }
            }

            btnCheckOut.setOnClickListener {
                viewModel.onCartOrders.value?.let { list ->
                    if (list.isEmpty()) {
                        viewModel.toastMessage.postValue(ALERT_CHECKOUT)
                    } else {
                        if (!NetworkHelper.isNetworkAvailable(requireContext())) {
                            viewModel.toastMessage.postValue(WARNING_CHECK_INTERNET)
                        } else {
                            val bundle = Bundle()
                            viewModel.promotion.value?.discount?.let {
                                bundle.putString(ID_PROMOTION, it.toString())
                            }
                            findNavController().navigate(R.id.checkoutFragment, bundle)
                        }
                    }
                }
            }
        }
    }

    private fun setButtonRemove(isButtonRemove: Boolean) {
        if (isButtonRemove) {
            binding.editPromoCode.setText(viewModel.promotion.value?.code ?: "")
            binding.btnRemove.setBackgroundResource(R.drawable.ic_close)

        } else {
            binding.editPromoCode.setText("")
            binding.btnRemove.setBackgroundResource(R.drawable.btn_arrow_forward)
        }
        viewModel.promotion.value?.let {
            if (it.value.equals("None")){
                binding.tvDiscountPrice.text = ""
            }else {
                binding.tvDiscountPrice.text =
                    "${it.discount} ${requireContext().getString(R.string.currency)}"
            }
        }
    }

    companion object {
        const val ALERT_CHECKOUT = "Please choose one product"
    }
}