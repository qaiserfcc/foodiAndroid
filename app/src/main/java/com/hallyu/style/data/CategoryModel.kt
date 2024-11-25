package com.hallyu.style.data

data class CategoryModel(
    val id: Int? = null,
    val name: String? = null,
    val imageUrl: String?=null,
    val parentCategory: CategoryModel?= null,
)