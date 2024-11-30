package com.bhub.foodi.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bhub.foodi.data.Review
import com.bhub.foodi.databinding.ItemReview2Binding
import com.bhub.foodi.utilities.DateFormat

class ListReviewAdapter2(
    private val setRecyclerView: (RecyclerView, Review) -> Unit,
) :
    ListAdapter<Review, ListReviewAdapter2.ItemViewHolder>(DiffCallback) {

    class ItemViewHolder(
        private val setRecyclerVIew: (RecyclerView, Review) -> Unit,
        private val binding: ItemReview2Binding,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(review: Review) {
            binding.apply {
                txtName.text = "ID Product: ${review.product_id}"
                txtDescription.text = review.review
                ratingBar.rating = review.rating.toFloat()

                val timeCreated = review.getReviewDate()?.toDate()

                timeCreated?.let {
                    txtCreated.text = DateFormat.review.format(it).toString()
                }

                setRecyclerVIew(recyclerViewImageReview, review)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            setRecyclerView,
            ItemReview2Binding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Review>() {
            override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
                return oldItem == newItem
            }
        }
    }
}