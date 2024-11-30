package com.bhub.foodi.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bhub.foodi.R
import com.bhub.foodi.databinding.ItemCategoriesBinding
import com.bhub.foodi.response.SubCategory


class ListCategoriesAdapter(private val onItemClicked: (SubCategory) -> Unit) :
    ListAdapter<SubCategory, ListCategoriesAdapter.ItemViewHolder>(
        DiffCallback
    ) {
    var selectedId = -1

    class ItemViewHolder(
        private val context: Context,
        private var binding: ItemCategoriesBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: SubCategory, selectId: Int, position: Int) {
            binding.apply {
                txtCategory.text = category.name
                if (category.id != selectId) {
                    txtCategory.setTextColor(ContextCompat.getColor(context, R.color.black))
                    layoutItemCategory.background =
                        ContextCompat.getDrawable(context, R.drawable.btn_custom5)
                } else {
                    txtCategory.setTextColor(ContextCompat.getColor(context, R.color.white))
                    layoutItemCategory.background =
                        ContextCompat.getDrawable(context, R.drawable.btn_custom4)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            parent.context, ItemCategoriesBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(current)
            selectedId = if (selectedId == current.id) {
                -1
            } else {
                current.id
            }
//            notifyDataSetChanged()
        }

        holder.bind(current, selectedId, position)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<SubCategory>() {
            override fun areItemsTheSame(oldItem: SubCategory, newItem: SubCategory): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: SubCategory, newItem: SubCategory): Boolean {
                return oldItem == newItem
            }
        }
    }


}