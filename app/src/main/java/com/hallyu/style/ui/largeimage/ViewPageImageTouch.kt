package com.hallyu.style.ui.largeimage

import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.hallyu.style.R
import com.hallyu.style.core.BaseFragment
import com.hallyu.style.databinding.ItemViewPagerImageTouchBinding
import com.hallyu.style.utilities.FileUtil
import com.hallyu.style.utilities.GlideDefault


class ViewPageImageTouch(private val url: String) : BaseFragment<ItemViewPagerImageTouchBinding>(
    ItemViewPagerImageTouchBinding::inflate
) {
    override fun setUpViews() {
        Glide.with(requireContext())
            .load(url)
            .placeholder(GlideDefault.createCircularProgress(requireContext()))
            .error(R.drawable.img_default)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    p0: GlideException?,
                    p1: Any?,
                    p2: Target<Drawable>?,
                    p3: Boolean
                ): Boolean {
                    haveImage = false
                    return false
                }

                override fun onResourceReady(
                    p0: Drawable?,
                    p1: Any?,
                    p2: Target<Drawable>?,
                    p3: DataSource?,
                    p4: Boolean
                ): Boolean {
                    haveImage = true
                    return false
                }
            })
            .into(binding.touchImageView)
    }
    private var haveImage = false

    fun downloadImage() {
        if(haveImage){
            val bitmap = binding.touchImageView.drawable.toBitmap()
            FileUtil.getImageUri(requireContext(), bitmap)?.let {
                toastMessage(getString(R.string.save_success))
            }
        }
    }
}