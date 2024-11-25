package com.hallyu.style.response

import com.hallyu.style.data.Product
import com.hallyu.style.data.Review

data class HomeData(
    val sliders: List<Slider>,
    val brands: List<Brand>,
    val categories: List<Category>,
    val featured: List<Product>,
    val best: List<Product>,
    val products: List<Product>
)

data class Brand(
    val id: Int,
    val name: String,
    val country: String,
    val logo: String,
)
data class Reviews(
    val reviews: List<Review> = emptyList()
)


data class Category(
    val id: Int, val name: String, val subs: List<SubCategory>
){
    fun getSubcategories(): List<SubCategory> {
        return this.subs
    }
}

data class SubCategory(
    val id: Int, val name: String, val image: String? = null, val childs: List<SubCategory>? = null
) {
    fun getImageUrl(): String {
        return image ?: ""
    }
}

data class Slider(
    val id: Int,
    val title: String?,
    val description: String?,
    val image: String?,
    val link: String?,
) {
    fun getImageUrl(): String {
        return image ?: ""
    }
}

