package com.hallyu.style.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hallyu.style.R
import com.hallyu.style.data.Product
import com.hallyu.style.databinding.ItemProductBinding
import com.hallyu.style.utilities.GlideDefault

class ListProductAdapter(
    private val onItemClicked: (Product) -> Unit,
    private val onFavoriteClick: (View, Product) -> Unit,
    private val setFavoriteButton: (View, Product) -> Unit,
) : ListAdapter<Product, ListProductAdapter.ItemViewHolder>(DiffCallback) {

    class ItemViewHolder(
        private val onFavoriteClick: (View, Product) -> Unit,
        private val setFavoriteButton: (View, Product) -> Unit,
        private var binding: ItemProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {


        fun bind(product: Product) {
            binding.apply {
                GlideDefault.show(itemView.context, product.getThumbnails(), imgProduct, true)
                txtName.text = product.title
                txtBrandName.text = product.brandName
                ratingBar.rating = product.reviewStars
                txtNumberVote.text = "(${product.numberReviews})"
                txtPrice.text =
                    "${product.getPrice()} ${itemView.context.getString(R.string.currency)}"

                if (product.salePercent != null && product.salePercent > 0) {
                    txtPrice.paintFlags = txtPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    txtSalePrice.visibility = View.VISIBLE
                    txtSalePercent.visibility = View.VISIBLE
                    txtSalePercent.text = "-${product.salePercent}%"
                    txtSalePrice.text = "${product.getPrice()}"
//                        "${product.colors[0].sizes[0].price * (100 - product.salePercent) / 100} ${itemView.context.getString(R.string.currency)}"
                } else {
                    txtPrice.paintFlags = 0
                    txtSalePercent.visibility = View.GONE
                    txtSalePrice.visibility = View.GONE
                }
                setFavoriteButton(btnFavorite, product)
                btnFavorite.setOnClickListener {
                    onFavoriteClick(btnFavorite, product)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            onFavoriteClick, setFavoriteButton, ItemProductBinding.inflate(
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
        }
        holder.bind(current)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldProduct: Product, newProduct: Product): Boolean {
                return newProduct.id === oldProduct.id
            }

            override fun areContentsTheSame(oldProduct: Product, newProduct: Product): Boolean {
                return newProduct.id.contentEquals(oldProduct.id)
            }
        }
    }

}