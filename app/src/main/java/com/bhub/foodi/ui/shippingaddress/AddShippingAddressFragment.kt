package com.bhub.foodi.ui.shippingaddress

import android.os.Bundle
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bhub.foodi.R
import com.bhub.foodi.data.ShippingAddress
import com.bhub.foodi.databinding.FragmentAddShippingAddressBinding
import com.bhub.foodi.core.BaseFragment
import com.bhub.foodi.utilities.BUNDLE_KEY_NAME_COUNTRY
import com.bhub.foodi.utilities.ID_ADDRESS
import com.bhub.foodi.utilities.REQUEST_KEY
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddShippingAddressFragment : BaseFragment<FragmentAddShippingAddressBinding>(
    FragmentAddShippingAddressBinding::inflate
) {
    override val viewModel: ShippingAddressViewModel by viewModels()
    private var shippingAddress: ShippingAddress? = null
    private var idAddress: String = ""

    override fun setUpArgument(bundle: Bundle) {
        arguments?.let {
            idAddress = it.getString(ID_ADDRESS) ?: ""
            if (idAddress.isNotBlank()) {
                viewModel.getAddress(idAddress)
            }
        }
    }

    override fun setUpObserve() {
        viewModel.apply {
            dismiss.observe(viewLifecycleOwner) {
                if (it) {
                    findNavController().navigateUp()
                }
            }

            toastMessage.observe(viewLifecycleOwner) {
                toastMessage(it)

            }

            alertFullName.observe(viewLifecycleOwner) {
                binding.apply {
                    if (it) {
                        txtLayoutFullName.isErrorEnabled = true
                        txtLayoutFullName.error = ALERT_FULL_NAME
                    } else {
                        txtLayoutFullName.isErrorEnabled = false
                    }
                }
            }

            alertAddress.observe(viewLifecycleOwner) {
                binding.apply {
                    if (it) {
                        txtLayoutAddress.isErrorEnabled = true
                        txtLayoutAddress.error = ALERT_ADDRESS
                    } else {
                        txtLayoutAddress.isErrorEnabled = false
                    }
                }
            }

            alertCity.observe(viewLifecycleOwner) {
                binding.apply {
                    if (it) {
                        txtLayoutCity.isErrorEnabled = true
                        txtLayoutCity.error = ALERT_CITY
                    } else {
                        txtLayoutCity.isErrorEnabled = false
                    }
                }
            }

            alertSate.observe(viewLifecycleOwner) {
                binding.apply {
                    if (it) {
                        txtLayoutState.isErrorEnabled = true
                        txtLayoutState.error = ALERT_REGION
                    } else {
                        txtLayoutState.isErrorEnabled = false
                    }
                }
            }

//            alertZipCode.observe(viewLifecycleOwner) {
//                binding.apply {
//                    if (it) {
//                        txtLayoutZipCode.isErrorEnabled = true
//                        txtLayoutZipCode.error = ALERT_ZIP_CODE
//                    } else {
//                        txtLayoutZipCode.isErrorEnabled = false
//                    }
//                }
//            }

            alertCountry.observe(viewLifecycleOwner) {
                binding.apply {
                    if (it) {
                        txtLayoutCountry.isErrorEnabled = true
                        txtLayoutCountry.error = ALERT_COUNTRY
                    } else {
                        txtLayoutCountry.isErrorEnabled = false
                    }
                }
            }
            address.observe(viewLifecycleOwner) {
                it?.let { shipping ->
                    if (shipping.address.isNotBlank() && idAddress.isNotBlank()) {
                        binding.apply {
                            shippingAddress = shipping
                            editTextFullName.setText(shipping.fullName)
                            editTextAddress.setText(shipping.address)
                            editTextCity.setText(shipping.city)
                            editTextState.setText(shipping.state)
                            editTextZipCode.setText(shipping.zipCode)
                            editTextCountry.setText(shipping.country)
                        }
                    }
                }

            }
        }
    }

    override fun setUpViews() {
        binding.apply {
            if (idAddress.isNotBlank()) {
                appBarLayout.MaterialToolbar.title = getString(R.string.edit_shipping_address)
            } else {
                appBarLayout.MaterialToolbar.title = getString(R.string.adding_shipping_address)
            }
            appBarLayout.MaterialToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            editTextFullName.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    viewModel.checkFullName(editTextFullName.text.toString())
                }
            }

            editTextAddress.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    viewModel.checkAddress(editTextAddress.text.toString())
                }
            }

            editTextCity.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    viewModel.checkCity(editTextCity.text.toString())
                }
            }
            editTextState.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    viewModel.checkState(editTextState.text.toString())
                }
            }
//            editTextZipCode.setOnFocusChangeListener { _, hasFocus ->
//                if (!hasFocus) {
//                    viewModel.checkZipCode(editTextZipCode.text.toString())
//                }
//            }


            txtLayoutCountry.endIconMode = TextInputLayout.END_ICON_CUSTOM
            editTextCountry.setOnClickListener {
                val bottomSelectCountry = BottomSelectCountry()
                bottomSelectCountry.show(parentFragmentManager, BottomSelectCountry.TAG)
            }

            btnSaveAddress.setOnClickListener {
                if (shippingAddress != null) {
                    shippingAddress?.let {
                        viewModel.updateShippingAddress(
                            it,
                            editTextFullName.text.toString(),
                            editTextAddress.text.toString(),
                            editTextCity.text.toString(),
                            editTextState.text.toString(),
                            editTextZipCode.text.toString(),
                            editTextCountry.text.toString(),
                        )
                    }
                } else {
                    viewModel.insertShippingAddress(
                        editTextFullName.text.toString(),
                        editTextAddress.text.toString(),
                        editTextCity.text.toString(),
                        editTextState.text.toString(),
                        editTextZipCode.text.toString(),
                        editTextCountry.text.toString()
                    )
                }

            }
        }
        setFragmentListener()
    }

    private fun setFragmentListener() {
        setFragmentResultListener(REQUEST_KEY) { _, bundle ->
            val result = bundle.getString(BUNDLE_KEY_NAME_COUNTRY)
            result?.let {
                binding.editTextCountry.setText(it)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.setAddressLiveData()
    }

    companion object {
        const val ALERT_FULL_NAME = "Full Name must more than 1"
        const val ALERT_ADDRESS = "Address must more than 5"
        const val ALERT_CITY = "City must more than 2"
        const val ALERT_REGION = "Region must more than 1"
        const val ALERT_ZIP_CODE = "Zip Code must more than 1"
        const val ALERT_COUNTRY = "Please choose country"
    }
}