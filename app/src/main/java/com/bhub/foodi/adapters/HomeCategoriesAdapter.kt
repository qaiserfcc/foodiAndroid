package com.bhub.foodi.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bhub.foodi.databinding.ItemHomeCategoriesBinding
import com.bhub.foodi.response.SubCategory
import com.bhub.foodi.utilities.GlideDefault

class HomeCategoriesAdapter(private val onItemClicked: (SubCategory) -> Unit) :
    ListAdapter<SubCategory, HomeCategoriesAdapter.ItemViewHolder>(
        DiffCallback
    ) {
    class ItemViewHolder(
        private var binding: ItemHomeCategoriesBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cat: SubCategory) {
            binding.apply {
                cat.apply {
                    txtCategories.text = name
                    GlideDefault.show(
                        imgProduct.context, getImageUrl(), imgProduct, true
                    )

                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemHomeCategoriesBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<SubCategory>() {
            override fun areItemsTheSame(
                oldItem: SubCategory, newItem: SubCategory
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: SubCategory, newItem: SubCategory
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}