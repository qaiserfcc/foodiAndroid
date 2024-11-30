package com.bhub.foodi.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bhub.foodi.ui.largeimage.ViewPageImageTouch

class ImageTouchAdapter(private val fragment: Fragment, private val listImage: List<String>) :
    FragmentStateAdapter(fragment) {
    private val listFragment = mutableListOf<ViewPageImageTouch>()
    override fun getItemCount(): Int {
        return listImage.size
    }

    override fun createFragment(position: Int): ViewPageImageTouch {
        listFragment.add(ViewPageImageTouch(listImage[position]))
        return listFragment.last()
    }

    fun onClickDownload(position: Int) {
        listFragment[position].downloadImage()
    }
}