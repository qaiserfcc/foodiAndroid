package com.bhub.foodi.response

import com.bhub.foodi.data.Product
import com.bhub.foodi.ui.shop.Pagination

data class PaginationData(val products: List<Product>, val pagination: Pagination)