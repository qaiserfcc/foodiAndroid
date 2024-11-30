package com.bhub.foodi.ui.checkout

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.bhub.foodi.R
import com.bhub.foodi.adapters.ListDeliveryAdapter
import com.bhub.foodi.core.BaseFragment
import com.bhub.foodi.data.*
import com.bhub.foodi.databinding.FragmentCheckoutBinding
import com.bhub.foodi.response.CartItem
import com.bhub.foodi.ui.bag.BagViewModel
import com.bhub.foodi.utilities.Notification
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckoutFragment : BaseFragment<FragmentCheckoutBinding>(
    FragmentCheckoutBinding::inflate
) {
    override val viewModel: CheckoutViewModel by viewModels()
    val bagViewModel: BagViewModel by viewModels()
    private lateinit var deliveryAdapter: ListDeliveryAdapter
    private val listDelivery: MutableList<Delivery> = mutableListOf()
    private var promotion: Promotion? = null
    private var totalOrder: Double = 0.0
    private var summary: Double = 0.0
    private var discount: Double = 0.0
    private var delivery: Delivery? = null
    private var address: ShippingAddress? = null
    private var phone: String = ""

    private var bags: List<CartItem> = emptyList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // add data Delivery
//        listDelivery.add(Delivery(R.drawable.cod, "Cash On Delivery", 10F))
//        listDelivery.add(Delivery(R.drawable.ic_usps, "USPS", 15F))
//        listDelivery.add(Delivery(R.drawable.ic_dhl, "DHL", 50F))
    }

    override fun setUpArgument(bundle: Bundle) {
//        arguments?.let { it ->
//            val promoDiscount = it.getString(ID_PROMOTION).toString().toDoubleOrNull()?:0.0
//            discount = promoDiscount
//        }
    }


    override fun setUpViews() {

        binding.apply {
            val dialog = getOTPDialog(requireContext())
            phone = viewModel.getPhone()
            editTextMobile.setText(phone)

            if (phone.isBlank()) {
                btnVerify.text = getString(R.string.verify)
                tlEditMobile.setEndIconVisible(false)
            } else {
                btnVerify.text = getString(R.string.verified)
                tlEditMobile.setEndIconVisible(true)
                btnVerify.setOnClickListener(null)
                viewModel.phoneVerified.postValue(true)
            }
            editTextMobile.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    btnVerify.text = getString(R.string.verify)
                    tlEditMobile.setEndIconVisible(false)
                    phone = ""
                    btnVerify.setOnClickListener {
                        (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                            editTextMobile.windowToken,
                            0
                        )
                        phone = editTextMobile.text.toString()
                        viewModel.sentOtpVerify(phone)
                            .observe(viewLifecycleOwner) {
                                if (it) {
                                    toastMessage("OTP sent Successfully")
                                    showOtpDialog(dialog) {
                                        viewModel.verifyOTP(phone, it)
                                            .observe(viewLifecycleOwner) {
                                                if (it) {
                                                    toastMessage("OTP Verified Successfully")
                                                    binding.btnVerify.text =
                                                        getString(R.string.verified)
                                                    binding.tlEditMobile.setEndIconVisible(true)
                                                    binding.btnVerify.setOnClickListener(null)
                                                    viewModel.setPhone(phone)
                                                    dialog.dismiss()
                                                } else {
                                                    toastMessage("Invalid OTP, Try Again")
                                                }
                                            }
                                    }
                                }
                            }
                    }
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })
            appBarLayout.MaterialToolbar.title = getString(R.string.checkout)
            appBarLayout.MaterialToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            deliveryAdapter = ListDeliveryAdapter {
                delivery = if (delivery == it) {
                    null
                } else {
                    it
                }
                setPrice()
            }

            recyclerViewDelivery.adapter = deliveryAdapter
            recyclerViewDelivery.layoutManager = LinearLayoutManager(
                context, LinearLayoutManager.HORIZONTAL, false
            )
            btnSubmitOrder.setOnClickListener {
                val promo = if (promotion?.value.equals("None", true)) null else promotion

                viewModel.checkOrder(bags, address, null, summary, delivery, promo)
                    .observe(viewLifecycleOwner) { order ->
                        if (order != null) {
                            viewModel.setOrderFirebase(order)
                                .observe(viewLifecycleOwner) { isSuccess ->
                                    if (isSuccess) {
                                        Notification(requireContext()).notifyOrder(order.user_id)
//                                        bagViewModel.clearCart()
                                        viewModel.isLoading.postValue(false)
                                        viewModel.setPromotionDefault()
                                        findNavController().navigate(R.id.successFragment)
                                    }
                                }
                        }
                    }
            }

            txtChangeAddress.setOnClickListener {
                findNavController().navigate(R.id.shippingAddressFragment)
            }

            btnAddShippingAddress.setOnClickListener {
                findNavController().navigate(R.id.shippingAddressFragment)
            }

            txtChangePayment.setOnClickListener {
                findNavController().navigate(R.id.paymentMethodFragment)
            }

            btnAddPayment.setOnClickListener {
                findNavController().navigate(R.id.paymentMethodFragment)
            }

        }
    }

    override fun setUpObserve() {
        viewModel.apply {
            setIdAddressDefault()
            setIdPaymentDefault()
            getOrderOptions()
            orderOptions.observe(viewLifecycleOwner) {
                val deliveryMethods: List<Delivery> = it.shipping
                if (deliveryMethods.isNotEmpty()) {
                    listDelivery.clear()
                    listDelivery.addAll(deliveryMethods)
                    delivery = listDelivery[0]
                    deliveryAdapter.positionCurrent = listDelivery.indexOf(delivery)
                    deliveryAdapter.submitList(listDelivery)
                    deliveryAdapter.notifyDataSetChanged()
                } else {
                    delivery = Delivery()
                    binding.recyclerViewDelivery.visibility = View.GONE
                    binding.txtDeliveryMethod.visibility = View.GONE
                }
            }

//            bag.observe(viewLifecycleOwner) {
//                bags = it
//                totalOrder = viewModel.calculatorTotalOrder(it)
//                promotion.value?.let { promo ->
//                    totalOrder = viewModel.calculatorTotalOrder(it, promo.salePercent)
//                }
//                setPrice()
//            }
            bag.observe(viewLifecycleOwner) {
                bags = it
                totalOrder = viewModel.calculatorTotalOrder(it, discount)
                promotion.value?.let { promo ->
                    Log.d("TAG", "setUpObserve: promotion.value is $promo")
                    discount = promo.discount.toDouble()
//                    totalOrder = viewModel.calculatorTotalOrder(it, promo.discount)
                }
                setPrice()
            }

            shippingAddress.observe(viewLifecycleOwner) {
                binding.apply {
                    if (it.address.isBlank()) {
                        layoutShippingAddress.visibility = View.INVISIBLE
                        btnAddShippingAddress.visibility = View.VISIBLE
                        address = null
                    } else {
                        layoutShippingAddress.visibility = View.VISIBLE
                        btnAddShippingAddress.visibility = View.GONE
                        setupShippingAddress(it)
                        address = it
                    }
                }
            }

            payment.observe(viewLifecycleOwner) {
                binding.apply {
                    itemPayment.visibility = View.VISIBLE
                    btnAddPayment.visibility = View.GONE
                    imgLogoCard.setImageResource(R.drawable.cod)
                    txtNumberCard.text = "${getString(R.string.cod)}"

                    /*if (it == null || it.id.isBlank()) {
                        itemPayment.visibility = View.INVISIBLE
                        btnAddPayment.visibility = View.VISIBLE
                        card = null
                    } else if (it.number[0] == '4') {
                        itemPayment.visibility = View.VISIBLE
                        btnAddPayment.visibility = View.GONE
                        imgLogoCard.setImageResource(R.drawable.cod)
                        txtNumberCard.text =
                            "${getString(R.string.cod)}"
                        card = it
                    } else if (it.number[0] == '5') {
                        itemPayment.visibility = View.VISIBLE
                        btnAddPayment.visibility = View.GONE
                        imgLogoCard.setImageResource(R.drawable.cod)
                        txtNumberCard.text =
                            "${getString(R.string.cod)}"
                        card = it
                    }*/
                }
            }

            toastMessage.observe(viewLifecycleOwner) {
                toastMessage(it)

            }
            isLoading.observe(viewLifecycleOwner) {
                setLoading(it)
            }
        }
    }

    private fun setPrice() {
        binding.apply {
            var priceDelivery = 0.0
            delivery?.let {
                priceDelivery = it.price.toDouble()
            }
            summary = (totalOrder + priceDelivery) - discount
            txtOrderPrice.text = "${totalOrder} ${getString(R.string.currency)}"
            txtDeliveryPrice.text = "${priceDelivery} ${getString(R.string.currency)}"
            txtPriceTotal.text = "${summary} ${getString(R.string.currency)}"
            txtDiscountPrice.text = "${discount} ${getString(R.string.currency)}"
        }
    }

    private fun setupShippingAddress(shippingAddress: ShippingAddress) {
        binding.apply {
            shippingAddress.apply {
                txtName.text = fullName
                txtAddress.text = "$address\n$city, $state $zipCode, $country"
            }
        }
    }

    fun getOTPDialog(context: Context): Dialog {
        val dialog = Dialog(context, R.style.AppTheme_Dialog_FullScreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_otp)
        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        return dialog
    }

    private fun showOtpDialog(dialog: Dialog, otpEntered: (String) -> Unit) {
        val editTextOtp = dialog.findViewById<TextInputEditText>(R.id.etOtp)
        val btnSubmit = dialog.findViewById<Button>(R.id.btnSubmit)

        btnSubmit.setOnClickListener {
            val otp = editTextOtp.text.toString()
            if (otp.length == 4) {
                otpEntered(otp)
            }
        }

        dialog.show()
    }


}