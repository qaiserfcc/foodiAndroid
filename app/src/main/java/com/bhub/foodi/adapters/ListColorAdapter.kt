package com.bhub.foodi.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bhub.foodi.R
import com.bhub.foodi.databinding.ItemSelectColorBinding

enum class IdColor(val id: Int) {
    BLACK(R.color.black),
    RED(R.color.colorPrimary),
    BLUE(R.color.blue)
}

class ListColorAdapter(private val onItemClicked: (String) -> Unit) :
    ListAdapter<String, ListColorAdapter.ItemViewHolder>(DiffCallback) {
    var positionCurrent = -1

    class ItemViewHolder(
        private var binding: ItemSelectColorBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(color: String, positionCurrent: Int, position: Int) {
            binding.apply {
                txtNameColor.text = color.replaceFirstChar {
                    it.uppercase()
                }
                cardViewColor.setCardBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        IdColor.valueOf(color.uppercase()).id
                    )
                )

                if (position == positionCurrent) {
                    layoutItem.setCardBackgroundColor(
                        ContextCompat.getColor(
                            itemView.context, IdColor.valueOf(color.uppercase()).id
                        )
                    )
                    txtNameColor.setTextColor(
                        ContextCompat.getColor(
                            itemView.context, R.color.white
                        )
                    )
                } else {
                    layoutItem.setCardBackgroundColor(
                        ContextCompat.getColor(
                            itemView.context, R.color.colorOnPrimary
                        )
                    )
                    txtNameColor.setTextColor(
                        ContextCompat.getColor(
                            itemView.context, R.color.black
                        )
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemSelectColorBinding.inflate(
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
        holder.itemView.setOnClickListener {
            onItemClicked(current)
            positionCurrent = if (positionCurrent == position) {
                -1
            } else {
                position
            }
            notifyDataSetChanged()
        }
        holder.bind(current, positionCurrent, position)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }
}