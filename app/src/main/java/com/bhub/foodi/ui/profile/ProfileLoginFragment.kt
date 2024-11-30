package com.bhub.foodi.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bhub.foodi.R
import com.bhub.foodi.core.BaseFragment
import com.bhub.foodi.data.UserData
import com.bhub.foodi.databinding.FragmentProfileLoginBinding
import com.bhub.foodi.ui.auth.AuthActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileLoginFragment : BaseFragment<FragmentProfileLoginBinding>(
    FragmentProfileLoginBinding::inflate
) {
    override val viewModel: ProfileViewModel by viewModels()
    override var isHideBottom = false

    private var totalAddress = 0
    private var paymentTxt = ""
    private var totalOrder = 0
    private var totalReview = 0
    private val ENGLISH = "en"
    private val ARABIC = "ar"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//
//        binding.apply {
//            val current = PreferencesManager.get<String>("lang")?:ENGLISH
//            Log.d("TAG", "setDefault:1 current language $current")
//            if (current== ARABIC){
//                btnChangeLang.text = "English"
//                Log.d("TAG", "setDefault:1 current language :Arabic")
//            }else{
//                btnChangeLang.text = "العربية"
//                Log.d("TAG", "setDefault:1 current language :English")
//            }
//            btnChangeLang.setOnClickListener {
//                // Change the locale to Arabic
//                val current = PreferencesManager.get<String>("lang")?:ENGLISH
//                Log.d("TAG", "setDefault:2 current language $current")
//                if (current==ARABIC){
//                    PreferencesManager.put(ENGLISH,"lang")
//                }else{
//                    PreferencesManager.put(ARABIC,"lang")
//                }
//
//                val newLang = PreferencesManager.get<String>("lang")?:ENGLISH
//                Log.d("TAG", "setDefault:2 current language :$newLang")
//                // Restart the activity to apply the new locale
//                val intent = requireActivity().intent
//                requireActivity().finish()
//                startActivity(intent)
//            }
//        }
    }

    override fun setUpViews() {
        binding.apply {
            appBarLayout.topAppBar.title = getString(R.string.my_profile)
            setDefault()
            imgAvatar.setOnClickListener {
                touchImage(listOf(viewModel.getAvatar()))
            }
            btnLogout.setOnClickListener {
                viewModel.logOut()
            }

            btnDeleteAccount.setOnClickListener {
                context?.let {
                    val builder = AlertDialog.Builder(it)
                    builder.setMessage(getString(R.string.delete_account_content))
                        .setCancelable(false).setPositiveButton(getString(R.string.yes)) { _, _ ->
                            viewModel.deleteAccount()
                        }.setNegativeButton(getString(R.string.no)) { dialog, _ ->
                            dialog.dismiss()
                        }
                    val alert = builder.create()
                    alert.show()
                }
            }

            myOrderLayout.setOnClickListener {
                findNavController().navigate(R.id.ordersFragment)
            }
            shippingLayout.setOnClickListener {
                findNavController().navigate(R.id.shippingAddressFragment)
            }
//            paymentLayout.setOnClickListener {
//                findNavController().navigate(R.id.paymentMethodFragment)
//            }
//            promocodesLayout.setOnClickListener {
//                findNavController().navigate(R.id.promoListFragment)
//            }
            myReviewLayout.setOnClickListener {
//                findNavController().navigate(R.id.reviewListFragment)
            }
            settingLayout.setOnClickListener {
                findNavController().navigate(R.id.settingFragment)
            }
        }
        viewModel.isLoading.postValue(false)
    }

    override fun setUpObserve() {
        viewModel.apply {
            isLoading.postValue(true)
            address.observe(viewLifecycleOwner) {
                setDefault()
            }
            userProfile.observe(viewLifecycleOwner) {
//                if (it?.isSuccessful() == true) {
//                    it.data.let { m -> setProfileDetails(m) }
//                } else {
//                try {//                    toastMessage(getString(R.string.failed))
//                } catch (e: Exception) {
//
//                }
//                }
            }
            payment.observe(viewLifecycleOwner) {
                setDefault()
            }

            totalOrder.observe(viewLifecycleOwner) {
                setDefault()
            }
            isLoading.observe(viewLifecycleOwner) {
                setLoading(it)
            }
            onDelete.observe(viewLifecycleOwner) {
                if (it == true) {
                    toastMessage(getString(R.string.delete_account_success))
                    viewModel.logOut()
                }

            }
        }

        lifecycleScope.launch {
            viewModel.isLogged.collectLatest {
                activity?.apply {
                    if (!it) {
                        startActivity(Intent(activity, AuthActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }

    private fun setProfileDetails(userData: UserData) {
//        userData.orders

    }

    private fun setDefault() {
        viewModel.apply {
            this@ProfileLoginFragment.totalAddress = address.value?.size ?: 0
            setSubTitleCard()
            this@ProfileLoginFragment.totalOrder = totalOrder.value ?: 0
            this@ProfileLoginFragment.totalReview = reviews.value?.size ?: 0
        }
        binding.apply {
            txtSubTitleShipping.text = "$totalAddress addresses"
            txtSubTitlePayment.text = paymentTxt
            txtSubTitleOrder.text = "Already have $totalOrder orders"
            txtSubTitleReview.text = "Reviews for $totalReview items"


        }
    }

    private fun freshData() {
        viewModel.apply {
            getAddress()
            getPayment()
            getTotalOrder()
            getReviews()
//            getUserInfo()
        }
    }

    private fun setSubTitleCard() {
        var str = ""
        viewModel.payment.value?.let {
            if (it.id.isNotBlank()) {
                str = if (it.number[0] == '4') {
                    "Visa  **${it.number.substring(it.number.length - 2)}"
                } else {
                    "Mastercard  **${it.number.substring(it.number.length - 2)}"
                }
            }
        }
        this@ProfileLoginFragment.paymentTxt = str
    }

    override fun onResume() {
        super.onResume()
        freshData()
        binding.apply {
            viewModel.setupProfileUI(this@ProfileLoginFragment, txtName, txtEmail, imgAvatar)
        }
    }
}