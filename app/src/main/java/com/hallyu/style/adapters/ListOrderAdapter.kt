package com.hallyu.style.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hallyu.style.R
import com.hallyu.style.data.Order
import com.hallyu.style.databinding.ItemOrderBinding
import com.hallyu.style.utilities.DateFormat
import kotlin.math.roundToInt

class ListOrderAdapter(
    private val onDetailClicked: (String) -> Unit,
    private val setStatus: (Order, TextView) -> Unit,
) :
    ListAdapter<Order, ListOrderAdapter.ItemViewHolder>(DiffCallback) {

    class ItemViewHolder(
        private val onDetailClicked: (String) -> Unit,
        private val setStatus: (Order, TextView) -> Unit,
        private var binding: ItemOrderBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            binding.apply {
                order.apply {
                    txtIdOrder.text = "Order No.$id"
                    txtTrackingNumber.text = trackingNumber

                    timeCreated.let {
                        txtTimeCreated.text = DateFormat.default.format(it).toString()
                    }

                    txtTotalAmount.text = "${total} ${itemView.context.getString(R.string.currency)}"

                    txtQuantityNumber.text = "${order.getUnits()}"
                    setStatus(order, txtStatus)
                    btnDetail.setOnClickListener {
                        onDetailClicked(id)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            onDetailClicked,
            setStatus,
            ItemOrderBinding.inflate(
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
        private val DiffCallback = object : DiffUtil.ItemCallback<Order>() {
            override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
                return oldItem == newItem
            }
        }
    }
}