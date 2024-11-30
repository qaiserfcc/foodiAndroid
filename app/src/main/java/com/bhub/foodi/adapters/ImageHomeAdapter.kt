package com.bhub.foodi.adapters

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bhub.foodi.ui.home.ViewPageImageHome

class ImageHomeAdapter(fragment: Fragment, val listImage: List<String>, val title: List<String>) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return listImage.size
    }

    override fun createFragment(position: Int): Fragment {
        Log.d("TAG", "createFragment: position $position")
        return ViewPageImageHome(listImage[position], title[position])
    }
}