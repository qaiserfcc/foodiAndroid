package com.bhub.foodi.ui.promotion

import com.bhub.foodi.data.PromotionRepository
import com.bhub.foodi.core.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
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