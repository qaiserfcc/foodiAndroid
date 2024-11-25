package com.hallyu.style.ui.largeimage

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.hallyu.style.R
import com.hallyu.style.adapters.ImageTouchAdapter
import com.hallyu.style.core.BaseFragment
import com.hallyu.style.databinding.FragmentLargeImageBinding
import com.hallyu.style.utilities.BUNDLE_KEY_POSITION
import com.hallyu.style.utilities.URL_IMAGE


class LargeImageFragment : BaseFragment<FragmentLargeImageBinding>(
    FragmentLargeImageBinding::inflate
) {
    override val color = R.color.black
    private var argument: String = ""
    private var currentPosition: Int = 0

    override fun setUpArgument(bundle: Bundle) {
        arguments?.let {
            argument = it.getString(URL_IMAGE).toString()
            currentPosition = it.getInt(BUNDLE_KEY_POSITION)
        }
    }

    override fun setUpViews() {
        if (argument.isNotBlank() && argument != "null") {
            val listUrl = argument.split("`")
            val adapter = ImageTouchAdapter(this@LargeImageFragment, listUrl)
            binding.apply {
                viewPagerImage.adapter = adapter
                viewPagerImage.registerOnPageChangeCallback(object :
                    ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        currentPosition = position
                        super.onPageSelected(position)
                    }
                })
                btnBack.setOnClickListener {
                    findNavController().navigateUp()
                }
                btnDownload.setOnClickListener {
                    adapter.onClickDownload(currentPosition)
                }
                viewPagerImage.currentItem = currentPosition
            }
        }
    }
}