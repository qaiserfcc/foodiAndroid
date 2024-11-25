package com.hallyu.style.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hallyu.style.data.Promotion
import com.hallyu.style.databinding.ItemPromotionBinding

class ListPromotionAdapter(private val onApplyClicked: (Promotion) -> Unit) :
    ListAdapter<Promotion, ListPromotionAdapter.ItemViewHolder>(DiffCallback) {

    class ItemViewHolder(
        private val onApplyClicked: (Promotion) -> Unit,
        private var binding: ItemPromotionBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(promotion: Promotion) {
            binding.apply {
                txtName.text = promotion.code
                txtIdPromotion.text = promotion.oldTotal

//                GlideDefault.show(itemView.context, promotion.backgroundImage, imgPromotion, true)

                btnApply.setOnClickListener {
                    onApplyClicked(promotion)
                }

//                promotion.endDate?.let {
//                    val diff: Long = promotion.endDate.time - Date().time
//                    txtTimeRemaining.text =
//                        "${TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)} days remaining"
//                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            onApplyClicked,
            ItemPromotionBinding.inflate(
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
        private val DiffCallback = object : DiffUtil.ItemCallback<Promotion>() {
            override fun areItemsTheSame(oldItem: Promotion, newItem: Promotion): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Promotion, newItem: Promotion): Boolean {
                return oldItem == newItem
            }
        }
    }


}