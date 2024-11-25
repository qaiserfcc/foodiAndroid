package com.hallyu.style.response

import com.hallyu.style.data.Product
import com.hallyu.style.ui.shop.Pagination

data class PaginationData(val products : List<Product>, val pagination : Pagination,)