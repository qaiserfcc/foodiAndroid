package com.bhub.foodi.data


data class ProductModel(
    val id: Int,
    val sku: String,
    val affiliate_link: String?,
    val category_id: Int,
    val subcategory_id: Int,
    val childcategory_id: Int?,
    val name: String,
    val slug: String,
    val photo: String,
    val price: Double,
    val color_all: String?,
    val size_all: String?,
    val previous_price: Double,
    val sale_percent: Int? = null,
    val details: String,
    val stock: Int?,
    val policy: String?,
    val status: Int?,
    val views: Int?,
    val created_at: String?,
    val updated_at: String?,
    val is_discount: Int?,
    val discount_date: String?,
    val language_id: Int?,
    val minimum_qty: Int?,
    val product_shelf_expiry: String?,
    val brand_id: Int
) {
    fun getAllSize(): MutableList<String> {
        return this.size_all?.split(",")?.toMutableList() ?: mutableListOf()
    }

    fun getAllColor(): MutableList<String> {
        return this.color_all?.split(",")?.toMutableList() ?: mutableListOf()
    }

}