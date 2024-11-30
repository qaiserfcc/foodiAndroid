package com.bhub.foodi.ui.general

import androidx.navigation.fragment.findNavController
import com.bhub.foodi.R
import com.bhub.foodi.core.BaseFragment
import com.bhub.foodi.databinding.FragmentNoInternetBinding
import com.bhub.foodi.utilities.NetworkHelper
import com.bhub.foodi.utilities.WARNING_CHECK_AGAIN

class NoInternetFragment : BaseFragment<FragmentNoInternetBinding>(
    FragmentNoInternetBinding::inflate
) {
    override fun setUpViews() {
        binding.btnTryAgain.setOnClickListener {
            if (NetworkHelper.isNetworkAvailable(requireContext())) {
                findNavController().navigateUp()
            } else {
                toastMessage(WARNING_CHECK_AGAIN)
            }
        }
    }

    override fun onBackPressed(): Boolean {
        findNavController().popBackStack(R.id.homeFragment, false)
        return true
    }
}