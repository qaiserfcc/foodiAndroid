package com.hallyu.style.data

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Review(
    val id: String = "",
    val user_id: String = "",
    val product_id: String = "",
    val review: String = "",
    val rating: Long = 0,
    val review_date: String? = null,
    var listImage: List<String> = emptyList()
){
    fun getReviewDate(): Timestamp {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        val date: Date = dateFormat.parse(review_date) ?: Date()

        return Timestamp(date)
    }
    fun getDateReview(): Date {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.parse(review_date) ?: Date()
    }
}
/*
*
                "id": 14,
                "user_id": 57,
                "product_id": 409,
                "review": "This product is excellent! High quality and very reliable.",
                "rating": 5,
                "review_date": "2024-07-20 14:01:45"
* */