package com.bhub.foodi.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bhub.foodi.R
import com.bhub.foodi.data.ProductOrder
import com.bhub.foodi.databinding.ItemProductOrderBinding
import com.bhub.foodi.utilities.GlideDefault
import kotlin.math.roundToInt

class ListProductOrderAdapter :
    ListAdapter<ProductOrder, ListProductOrderAdapter.ItemViewHolder>(DiffCallback) {

    class ItemViewHolder(private var binding: ItemProductOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(product: ProductOrder) {
            binding.apply {
                product.apply {
                    GlideDefault.show(itemView.context, image, imgProduct, true)
                    txtName.text = title
                    txtBrandName.text = brandName
                    txtColorInput.text = color
                    txtSizeInput.text = size
                    txtPrice.text =
                        "${price.roundToInt()} ${itemView.context.getString(R.string.currency)}"
                    txtUnitInput.text = units.toString()
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemProductOrderBinding.inflate(
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
        private val DiffCallback = object : DiffUtil.ItemCallback<ProductOrder>() {
            override fun areItemsTheSame(
                oldProduct: ProductOrder,
                newProduct: ProductOrder
            ): Boolean {
                return newProduct === oldProduct
            }

            override fun areContentsTheSame(
                oldProduct: ProductOrder,
                newProduct: ProductOrder
            ): Boolean {
                return newProduct == oldProduct
            }
        }
    }

}