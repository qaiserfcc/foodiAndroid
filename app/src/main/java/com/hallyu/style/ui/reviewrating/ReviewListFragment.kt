package com.hallyu.style.ui.reviewrating

import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hallyu.style.R
import com.hallyu.style.adapters.ListImageReview
import com.hallyu.style.adapters.ListReviewAdapter2
import com.hallyu.style.core.BaseFragment
import com.hallyu.style.data.TypeSort
import com.hallyu.style.databinding.FragmentReviewListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewListFragment : BaseFragment<FragmentReviewListBinding>(
    FragmentReviewListBinding::inflate
) {
    override val viewModel: ReviewRatingViewModel by viewModels()


    private lateinit var adapterReview: ListReviewAdapter2
    private val txtFilter = listOf("Date ASC", "Date DES", "Star ASC", "Star DES")

    override fun setUpAdapter() {
        adapterReview = ListReviewAdapter2 { recyclerView, review ->
            val adapter = ListImageReview(false, {
                touchImage(
                    review.listImage,
                    it
                )
            }, {})
            adapter.dataSet = review.listImage
            adapter.notifyDataSetChanged()

            recyclerView.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL, false
            )
            recyclerView.adapter = adapter
        }
    }

    override fun setUpObserve() {
        viewModel.apply {
            getAllReviewOfUser()

            reviews.observe(viewLifecycleOwner) {
                binding.nestedScrollView.scrollTo(0, 0)
                adapterReview.submitList(it)
            }
        }
    }

    override fun setUpViews() {
        binding.apply {
            appBarLayout.MaterialToolbar.title = getString(R.string.review_list)
            appBarLayout.MaterialToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            recyclerVIewReview.layoutManager = LinearLayoutManager(context)
            recyclerVIewReview.adapter = adapterReview

            appBarLayout.btnFilter.setOnClickListener {
                showMenu(it)
            }
        }
    }

    private fun showMenu(v: View) {
        val popup = PopupMenu(context, v)
        popup.menuInflater.inflate(R.menu.menu_filter_review, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.sortDateAsc -> {
                    binding.appBarLayout.txtNameFilter.text = txtFilter[0]
                    viewModel.filterReview(FilterReview.DATE, TypeSort.ASCENDING)
                    false
                }
                R.id.sortDateDes -> {
                    binding.appBarLayout.txtNameFilter.text = txtFilter[1]
                    viewModel.filterReview(FilterReview.DATE, TypeSort.DESCENDING)
                    false
                }
                R.id.sortStarAsc -> {
                    binding.appBarLayout.txtNameFilter.text = txtFilter[2]
                    viewModel.filterReview(FilterReview.STAR, TypeSort.ASCENDING)
                    false
                }
                R.id.sortStarDes -> {
                    binding.appBarLayout.txtNameFilter.text = txtFilter[3]
                    viewModel.filterReview(FilterReview.STAR, TypeSort.DESCENDING)
                    false
                }
                else -> false
            }
        }

        popup.setOnDismissListener {
        }
        popup.show()
    }
}