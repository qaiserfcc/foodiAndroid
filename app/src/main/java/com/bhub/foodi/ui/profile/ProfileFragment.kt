package com.bhub.foodi.ui.profile

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bhub.foodi.R
import com.bhub.foodi.core.BaseFragment
import com.bhub.foodi.databinding.FragmentProfileBinding
import com.bhub.foodi.ui.auth.AuthViewModel
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