package com.bhub.foodi.ui.order

import androidx.navigation.fragment.findNavController
import com.bhub.foodi.adapters.StatusPagerAdapter
import com.bhub.foodi.core.BaseFragment
import com.bhub.foodi.databinding.FragmentOrdersBinding
import com.bhub.foodi.utilities.statuses
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