package com.bhub.foodi.response

import com.bhub.foodi.data.Product

data class DetailData(val product: Product, val related: List<Product>)