package com.bhub.foodi.adapters

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bhub.foodi.R
import com.bhub.foodi.databinding.ItemBagBinding
import com.bhub.foodi.response.CartItem
import com.bhub.foodi.utilities.GlideDefault

class ListBagAdapter2(
    private val onItemClicked: (CartItem) -> Unit,
    private val onDeleteClicked: (CartItem) -> Unit,
    private val onPlusQuantityClicked: (CartItem, TextView, TextView) -> Unit,
    private val onMinusQuantityClicked: (CartItem, TextView, TextView) -> Unit,
) :
    ListAdapter<CartItem, ListBagAdapter2.ItemViewHolder>(DiffCallback) {

    class ItemViewHolder(
        private val context: Context,
        private val onDeleteClicked: (CartItem) -> Unit,
        private val onPlusQuantityClicked: (CartItem, TextView, TextView) -> Unit,
        private val onMinusQuantityClicked: (CartItem, TextView, TextView) -> Unit,
        private var binding: ItemBagBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItem) {
            binding.apply {
                GlideDefault.show(
                    itemView.context,
                    cartItem.product.getThumbnails(),
                    imgProduct,
                    true
                )
                txtName.text = cartItem.product.title
                txtBrandName.text = cartItem.product.brandName
                txtQuantity.text = cartItem.quantity.toString()
                txtColorInput.visibility = View.GONE
                txtSizeInput.visibility = View.GONE
//                txtQuantity.visibility = View.GONE
//                txtColorInput.text = cartItem.bag.color
//                txtSizeInput.text = cartItem.bag.size

//                val size = cartItem.product.getColorAndSize(
//                    cartItem.bag.color,
//                    cartItem.bag.size
//                )
                txtPrice.text =
                    "${cartItem.getTotalQtyPrice()} ${itemView.context.getString(R.string.currency)}"
                if (cartItem.product.salePercent > 0) {
                    txtSalePrice.text = "${cartItem.product.previousPrice} "
                    txtSalePrice.paintFlags = txtSalePrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    txtPrice.visibility = View.VISIBLE
                    txtSalePercent.visibility = View.VISIBLE
                    txtSalePercent.text = "-${cartItem.product.salePercent}%"
//                        "${size.price * (100 - cartItem.product.salePercent) / 100} ${itemView.context.getString(R.string.currency)}"
                } else {
                    txtSalePrice.paintFlags = 0
                    txtPrice.visibility = View.VISIBLE
                    txtSalePercent.visibility = View.GONE
                    txtSalePrice.visibility = View.GONE
                }

                btnMinus.setOnClickListener {
                    onMinusQuantityClicked(cartItem, txtQuantity, txtPrice)
                }

                btnPlus.setOnClickListener {
                    onPlusQuantityClicked(cartItem, txtQuantity, txtPrice)
                }

                btnMore.setOnClickListener { view ->
                    val popup = PopupMenu(context, view)
                    popup.menuInflater.inflate(R.menu.more_bag, popup.menu)

                    popup.setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
//                            R.id.menu_add_to_favorites -> {
//                                onAddClicked(cartItem)
//                                true
//                            }

                            R.id.menu_delete -> {
                                onDeleteClicked(cartItem)
                                true
                            }

                            else -> false
                        }

                    }
                    popup.show()
                }

            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            parent.context,
            onDeleteClicked,
            onPlusQuantityClicked,
            onMinusQuantityClicked,
            ItemBagBinding.inflate(
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
        private val DiffCallback = object : DiffUtil.ItemCallback<CartItem>() {
            override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(
                oldItem: CartItem,
                newItem: CartItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }


}