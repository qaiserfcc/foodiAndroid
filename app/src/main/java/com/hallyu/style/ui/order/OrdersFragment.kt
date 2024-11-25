package com.hallyu.style.ui.order

import androidx.navigation.fragment.findNavController
import com.hallyu.style.adapters.StatusPagerAdapter
import com.hallyu.style.core.BaseFragment
import com.hallyu.style.databinding.FragmentOrdersBinding
import com.hallyu.style.utilities.statuses
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrdersFragment : BaseFragment<FragmentOrdersBinding>(
    FragmentOrdersBinding::inflate
) {
    override var isHideBottom = false

    override fun setUpViews() {
        binding.apply {
            viewPager.adapter = StatusPagerAdapter(this@OrdersFragment)
            TabLayoutMediator(viewPagerTabs, viewPager) { tab, position ->
                tab.text = statuses[position]
            }.attach()

            appBarLayout.MaterialToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }
}