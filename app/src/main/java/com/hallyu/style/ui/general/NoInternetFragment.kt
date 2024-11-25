package com.hallyu.style.ui.general

import androidx.navigation.fragment.findNavController
import com.hallyu.style.R
import com.hallyu.style.core.BaseFragment
import com.hallyu.style.databinding.FragmentNoInternetBinding
import com.hallyu.style.utilities.NetworkHelper
import com.hallyu.style.utilities.WARNING_CHECK_AGAIN

class NoInternetFragment : BaseFragment<FragmentNoInternetBinding>(
    FragmentNoInternetBinding::inflate
) {
    override fun setUpViews() {
        binding.btnTryAgain.setOnClickListener {
            if(NetworkHelper.isNetworkAvailable(requireContext())){
                findNavController().navigateUp()
            }
            else{
                toastMessage(WARNING_CHECK_AGAIN)
            }
        }
    }

    override fun onBackPressed(): Boolean {
        findNavController().popBackStack(R.id.homeFragment,false)
        return true
    }
}