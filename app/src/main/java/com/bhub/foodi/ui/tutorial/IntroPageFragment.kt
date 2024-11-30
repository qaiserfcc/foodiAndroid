package com.bhub.foodi.ui.tutorial

import android.os.Bundle
import com.bhub.foodi.core.BaseFragment
import com.bhub.foodi.databinding.FragmentIntroPageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroPageFragment(img: Int = 0, nameTopic: String = "") :
    BaseFragment<FragmentIntroPageBinding>(
        FragmentIntroPageBinding::inflate
    ) {
    private var _img = img
    private var _nameTopic = nameTopic

    override fun setUpArgument(bundle: Bundle) {
        arguments?.apply {
            _img = getInt(IMG)
            _nameTopic = getString(TITLE) ?: _nameTopic
        }
    }

    override fun setUpViews() {
        binding.txtIntro.text = _nameTopic
        binding.imgIntro.setImageResource(_img)
    }

    companion object {
        const val IMG = "IMG"
        const val TITLE = "TITLE"

        @JvmStatic
        fun newInstance(_img: Int, _nameTopic: String) = IntroPageFragment().apply {
            arguments?.apply {
                putInt(IMG, _img)
                putString(TITLE, _nameTopic)
            }
        }
    }
}