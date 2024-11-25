package com.hallyu.style.ui.profile

import android.content.Intent
import com.hallyu.style.R
import com.hallyu.style.core.BaseFragment
import com.hallyu.style.databinding.FragmentProfileNoLoginBinding
import com.hallyu.style.ui.auth.AuthActivity
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