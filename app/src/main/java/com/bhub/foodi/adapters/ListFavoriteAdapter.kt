package com.bhub.foodi.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bhub.foodi.R
import com.bhub.foodi.data.Product
import com.bhub.foodi.databinding.ItemProductFavoriteBinding
import com.bhub.foodi.utilities.GlideDefault

class ListFavoriteAdapter(
    private val onCloseClicked: (Product) -> Unit,
    private val onItemClicked: (Product) -> Unit,
    private val onBagClicked: (View, Product) -> Unit,
    private val setButtonBag: (View, Product) -> Unit
) :
    ListAdapter<Product, ListFavoriteAdapter.ItemViewHolder>(DiffCallback) {

    class ItemViewHolder(
        private val onCloseClicked: (Product) -> Unit,
        private val onBagClicked: (View, Product) -> Unit,
        private val setButtonBag: (View, Product) -> Unit,
        private var binding: ItemProductFavoriteBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(favoriteAndProduct: Product) {

            binding.apply {
                GlideDefault.show(
                    itemView.context,
                    favoriteAndProduct.getThumbnails(),
                    imgProduct,
                    true
                )
                txtName.text = favoriteAndProduct.title
                txtBrandName.text = favoriteAndProduct.brandName
                ratingBar.rating = favoriteAndProduct.reviewStars.toFloat()

                txtNumberVote.text = "(${favoriteAndProduct.numberReviews})"
                txtPrice.text =
                    "${favoriteAndProduct?.getPrice()} ${itemView.context.getString(R.string.currency)}"

                grayOutLayout.visibility = View.GONE
                txtSoldOut.visibility = View.GONE
                btnBag.visibility = View.VISIBLE

                if (favoriteAndProduct.salePercent > 0) {
                    txtPrice.paintFlags = txtPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    txtSalePrice.visibility = View.VISIBLE
                    txtSalePercent.visibility = View.VISIBLE
                    txtSalePercent.text = "-${favoriteAndProduct.salePercent}%"
                    txtSalePrice.text =
                        "${favoriteAndProduct.getPrice()} ${itemView.context.getString(R.string.currency)}"
                } else {
                    txtPrice.paintFlags = 0
                    txtSalePercent.visibility = View.GONE
                    txtSalePrice.visibility = View.GONE
                }

                btnRemoveFavorite.setOnClickListener {
                    onCloseClicked(favoriteAndProduct)
                }

                setButtonBag(btnBag, favoriteAndProduct)
                btnBag.setOnClickListener {
                    onBagClicked(btnBag, favoriteAndProduct)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            onCloseClicked,
            onBagClicked,
            setButtonBag,
            ItemProductFavoriteBinding.inflate(
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
        }
        holder.bind(current)
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(
                oldProduct: Product,
                newProduct: Product
            ): Boolean {
                return newProduct === oldProduct
            }

            override fun areContentsTheSame(
                oldProduct: Product,
                newProduct: Product
            ): Boolean {
                return newProduct == oldProduct
            }
        }
    }

}