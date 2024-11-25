package com.hallyu.style.response

import com.hallyu.style.data.Delivery

data class OptionsData(
    val shipping: List<Delivery>,
    val packages: List<Delivery>,
    val countries: List<Country>,
)

data class Country(
    val id: Int,
    val country_code: String,
    val country_name: String,
    val tax: Int,
    val status: Int,
    val states: List<State>
)

data class State(
    val id: Int,
    val country_id: Int,
    val state: String,
    val tax: Int,
    val status: Int,
    val owner_id: Int
)
