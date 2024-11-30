package com.bhub.foodi.adapters

import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bhub.foodi.R
import com.bhub.foodi.data.Product
import com.bhub.foodi.databinding.ItemProductBinding
import com.bhub.foodi.utilities.GlideDefault

class ProductPagingDataAdapter :
    PagingDataAdapter<Product, ProductPagingDataAdapter.ProductViewHolder>(PRODUCT_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        Log.d("TAG", "onCreateViewHolder: ProductPagingDataAdapter")
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        Log.d("Sruthi", "onBindViewHolder: $position")
        val product = getItem(position)
        if (product != null) {
            holder.bind(product)
        }
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.apply {

                GlideDefault.show(itemView.context, product.getThumbnails(), imgProduct, true)
                txtName.text = product.title
                txtBrandName.text = product.brandName
                ratingBar.rating = product.reviewStars.toFloat()
                txtNumberVote.text = "(${product.numberReviews})"
                txtPrice.text = "110 " + itemView.context.getString(R.string.currency)

                if (product.salePercent != null && product.salePercent > 0) {
                    txtPrice.paintFlags = txtPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    txtSalePrice.visibility = View.VISIBLE
                    txtSalePercent.visibility = View.VISIBLE
                    txtSalePercent.text = "-${product.salePercent}%"
                    txtSalePrice.text =
                        "${110 * (100 - product.salePercent) / 100} ${itemView.context.getString(R.string.currency)}"
                } else {
                    txtPrice.paintFlags = 0
                    txtSalePercent.visibility = View.GONE
                    txtSalePrice.visibility = View.GONE

                }
//                setFavoriteButton(btnFavorite, product)
//                btnFavorite.setOnClickListener {
//                    onFavoriteClick(btnFavorite, product)
//                }
            }
        }
    }

    companion object {
        private val PRODUCT_COMPARATOR = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem.id == newItem.id // Use a unique identifier for comparison
            }

            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem == newItem // Check if the entire product object is the same
            }
        }
    }
}