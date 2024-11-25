package com.hallyu.style.ui.profile

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.hallyu.style.R
import com.hallyu.style.core.BaseFragment
import com.hallyu.style.databinding.FragmentProfileBinding
import com.hallyu.style.ui.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(
    FragmentProfileBinding::inflate
) {
    override val viewModel: AuthViewModel by viewModels()
    override var isHideBottom = false

    override fun setUpViews() {
        if (!viewModel.isLogged()) {
            findNavController().navigate(R.id.action_profileFragment_to_profileNoLoginFragment)
        } else {
            findNavController().navigate(R.id.action_profileFragment_to_profileLoginFragment)
        }
    }
}