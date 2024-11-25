package com.hallyu.style.ui.shop

data class ProductQueryParams(
    val category: String? = null,
    val subCategory: String? = null,
    val brand: Int? = null,
    val minPrice: Int? = null,
    val maxPrice: Int? = null,
    val sort: String? = null,
    val search: String? = null,
    val limit: Int = 10,
    val page: Int = 1
)