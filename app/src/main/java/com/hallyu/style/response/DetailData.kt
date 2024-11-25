package com.hallyu.style.response

import com.hallyu.style.data.Product

data class DetailData(val product : Product,val related : List<Product>,)