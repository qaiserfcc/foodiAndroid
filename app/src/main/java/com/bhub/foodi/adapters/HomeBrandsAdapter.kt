package com.bhub.foodi.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bhub.foodi.databinding.ItemHomeBrandsBinding
import com.bhub.foodi.response.Brand
import com.bhub.foodi.utilities.GlideDefault

class HomeBrandsAdapter(val onItemClicked: (Brand) -> Unit) :
    ListAdapter<Brand, HomeBrandsAdapter.ItemViewHolder>(
        DiffCallback
    ) {
    class ItemViewHolder(
        private var binding: ItemHomeBrandsBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(brands: Brand) {
            binding.apply {

                brands.apply {
                    txtCategories.text = name
                    GlideDefault.show(
                        imgProduct.context, brands.logo, imgProduct, true
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemHomeBrandsBinding.inflate(
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
        private val DiffCallback = object : DiffUtil.ItemCallback<Brand>() {
            override fun areItemsTheSame(
                oldItem: Brand, newItem: Brand
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: Brand, newItem: Brand
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}