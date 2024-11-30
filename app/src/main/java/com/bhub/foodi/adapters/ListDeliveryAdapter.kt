package com.bhub.foodi.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bhub.foodi.R
import com.bhub.foodi.data.Delivery
import com.bhub.foodi.databinding.ItemDeliveryBinding

class ListDeliveryAdapter(private val onItemClicked: (Delivery) -> Unit) :
    ListAdapter<Delivery, ListDeliveryAdapter.ItemViewHolder>(DiffCallback) {
    var positionCurrent = -1

    class ItemViewHolder(
        private var binding: ItemDeliveryBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(delivery: Delivery, positionCurrent: Int, position: Int) {
            binding.apply {
                imgLogo.visibility = View.GONE
                if (position == positionCurrent) {
                    layoutItem.strokeColor =
                        ContextCompat.getColor(itemView.context, R.color.colorPrimary)
                } else {
                    layoutItem.strokeColor =
                        ContextCompat.getColor(itemView.context, R.color.colorOnPrimary)
                }
                txtTitle.text = delivery.title
                txtDay.text = delivery.subtitle
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemDeliveryBinding.inflate(
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
        private val DiffCallback = object : DiffUtil.ItemCallback<Delivery>() {
            override fun areItemsTheSame(oldItem: Delivery, newItem: Delivery): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Delivery, newItem: Delivery): Boolean {
                return oldItem == newItem
            }
        }
    }
}