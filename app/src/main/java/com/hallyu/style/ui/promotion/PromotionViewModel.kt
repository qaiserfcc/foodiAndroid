package com.hallyu.style.ui.promotion

import androidx.lifecycle.viewModelScope
import com.hallyu.style.data.PromotionRepository
import com.hallyu.style.data.TypeSort
import com.hallyu.style.core.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PromotionViewModel @Inject constructor(
    private val promotionRepository: PromotionRepository,
) : BaseViewModel() {
    val promotions = promotionRepository.promotions
    val promotion = promotionRepository.promotion

//    init {
//        fetchData()
//    }

//    fun fetchData() {
//        promotionRepository.fetchData()
//    }



}

enum class FilterPromotion(val value: String) {
    DATE("endDate"), PERCENT("salePercent")
}