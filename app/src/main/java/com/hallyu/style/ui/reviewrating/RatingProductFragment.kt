package com.hallyu.style.ui.reviewrating

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hallyu.style.R
import com.hallyu.style.adapters.ListImageReview
import com.hallyu.style.adapters.ListReviewAdapter
import com.hallyu.style.data.Product
import com.hallyu.style.databinding.FragmentRatingProductBinding
import com.hallyu.style.core.BaseFragment
import com.hallyu.style.utilities.BUNDLE_DISMISS
import com.hallyu.style.utilities.GlideDefault
import com.hallyu.style.utilities.ID_PRODUCT
import com.hallyu.style.utilities.REQUEST_KEY
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt


@AndroidEntryPoint
class RatingProductFragment : BaseFragment<FragmentRatingProductBinding>(
    FragmentRatingProductBinding::inflate
) {
    override val viewModel: ReviewRatingViewModel by viewModels()

    private lateinit var idProduct: String
    private lateinit var adapterReview: ListReviewAdapter

    override fun setUpArgument(bundle: Bundle) {
        arguments?.let {
            idProduct = it.getString(ID_PRODUCT).toString()
            if (idProduct.isNotBlank()) {
                viewModel.setIdProduct(idProduct)
            } else {
                findNavController().popBackStack(R.id.productDetailFragment, false)
            }
        }
    }

    override fun setUpAdapter() {
        adapterReview = ListReviewAdapter({ txtName, imgAvatar, userID ->
            val result = viewModel.getNameAndAvatarUser(userID)
            result.observe(viewLifecycleOwner) {
                txtName.text = it.first
                GlideDefault.userImage(requireContext(), it.second, imgAvatar, false)
            }
        }, { review, txtHelpful, icLike, isHelpful ->
            if (isHelpful) {
                viewModel.addHelpful(review)

            } else {
                viewModel.removeHelpful(review)
            }
            viewModel.setColorHelpful(requireContext(), isHelpful, txtHelpful, icLike)

        }, { review, txtHelpful, icLike ->
            viewModel.checkHelpfulForUser(review).observe(viewLifecycleOwner) {
                    adapterReview.isHelpful = it
                    viewModel.setColorHelpful(requireContext(), it, txtHelpful, icLike)
                }
        }, { recyclerView, review ->
            val adapter = ListImageReview(false, {
                touchImage(
                    review.listImage, it
                )
            }, {})
            adapter.dataSet = review.listImage
            adapter.notifyDataSetChanged()

            recyclerView.layoutManager = LinearLayoutManager(
                context, LinearLayoutManager.HORIZONTAL, false
            )
            recyclerView.adapter = adapter
        })
    }

    override fun setUpObserve() {
        viewModel.apply {
            addReview.observe(viewLifecycleOwner) {
                it?.let{
                    if (it.isSuccessful()){
                        getReview(idProduct)
                        toastMessage("your review has been added Thank You")
                    }else{
                        toastMessage(getString(R.string.failed))
                    }
                }
            }
            productReviews.observe(viewLifecycleOwner) {
                Log.d("RatingViewModel", "getReview:observe: $idProduct ")
                if (it != null && it.isSuccessful()) {
                    it.data?.reviews.let {it1->
                        listReview.postValue(it1)
                    }
                } else {
                    toastMessage(getString(R.string.failed))
                }
            }
            listReview.observe(viewLifecycleOwner) {
                filterImage(statusFilterImage.value ?: false)?.let { list ->
                    adapterReview.submitList(list)
                    binding.txtNumberReview.text = getString(R.string.reviews, list.size)
                    val lists: MutableList<Int> = mutableListOf(0, 0, 0, 0, 0)
                    list.forEach {review->
                        if (review.rating in 1..5) {
                            lists[review.rating.toInt() - 1]++
                        }
                    }
                    listRating.postValue(lists)
                }
            }


//            product.observe(viewLifecycleOwner) { product ->
//                if (product != null) {
//                    getReview(product.id)
//                    getRatingProduct(product.id)
//                }
//            }

            listRating.observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    setupRatingStatistics(it)
                }
            }

            statusFilterImage.observe(viewLifecycleOwner) {
                filterImage(it)?.let { list ->
                    adapterReview.submitList(list)
                    binding.txtNumberReview.text = "${list.size} reviews"
                }
            }
            isLoading.observe(viewLifecycleOwner) {
                setLoading(it)
            }
            getReview(idProduct)
        }
    }

    override fun setUpViews() {
        binding.apply {
            appBarLayout.MaterialToolbar.title = getString(R.string.rating_and_reviews)
            btnAddReview.setOnClickListener {
                val bottomAddReview = BottomAddReview(idProduct)
                bottomAddReview.show(parentFragmentManager, BottomAddReview.TAG)
            }

            recyclerVIewReview.layoutManager = LinearLayoutManager(context)
            recyclerVIewReview.adapter = adapterReview

            appBarLayout.MaterialToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            checkboxWithPhoto.setOnClickListener {
                viewModel.statusFilterImage.postValue(checkboxWithPhoto.isChecked)
            }

            if (!viewModel.isLogged()) {
                btnAddReview.visibility = View.GONE
            }
        }
        setFragmentListener()
    }

    private fun setupRatingStatistics(ratingProduct: List<Int>) {
        binding.apply {
            val totalRating = Product().getTotalRating(ratingProduct).toFloat()
            txtRatingAverage.text = Product().getAverageRating(ratingProduct).toString()
            txtNumberRating.text = "${totalRating.roundToInt()} ratings"
            progressBar1.progress = ((ratingProduct[0] / totalRating) * 100).toInt()
            progressBar2.progress = ((ratingProduct[1] / totalRating) * 100).toInt()
            progressBar3.progress = ((ratingProduct[2] / totalRating) * 100).toInt()
            progressBar4.progress = ((ratingProduct[3] / totalRating) * 100).toInt()
            progressBar5.progress = ((ratingProduct[4] / totalRating) * 100).toInt()

            txtNumberRating1.text = ratingProduct[0].toString()
            txtNumberRating2.text = ratingProduct[1].toString()
            txtNumberRating3.text = ratingProduct[2].toString()
            txtNumberRating4.text = ratingProduct[3].toString()
            txtNumberRating5.text = ratingProduct[4].toString()
            viewModel.isLoading.postValue(false)
        }
    }

    private fun setFragmentListener() {
        setFragmentResultListener(REQUEST_KEY) { _, bundle ->
            val result = bundle.getBoolean(BUNDLE_DISMISS)
            println(result)
            if (result) {
                viewModel.apply {
                    getReview(idProduct)
                    getRatingProduct(idProduct)
                }
            }
        }
    }
}