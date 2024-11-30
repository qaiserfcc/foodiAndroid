package com.bhub.foodi.ui.profile

import android.content.Intent
import com.bhub.foodi.R
import com.bhub.foodi.core.BaseFragment
import com.bhub.foodi.databinding.FragmentProfileNoLoginBinding
import com.bhub.foodi.ui.auth.AuthActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileNoLoginFragment : BaseFragment<FragmentProfileNoLoginBinding>(
    FragmentProfileNoLoginBinding::inflate
) {
    override var isHideBottom = false

    override fun setUpViews() {
        binding.apply {
            appBarLayout.topAppBar.title = getString(R.string.my_profile)

            profileNoLoginLayout.setOnClickListener {
                startActivity(Intent(activity, AuthActivity::class.java))
                activity?.finish()
            }
        }
    }
}