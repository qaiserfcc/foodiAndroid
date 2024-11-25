package com.hallyu.style.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hallyu.style.ui.largeimage.ViewPageImageTouch

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

    fun onClickDownload(position: Int){
        listFragment[position].downloadImage()
    }
}