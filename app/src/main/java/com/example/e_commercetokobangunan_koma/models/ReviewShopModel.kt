package com.example.e_commercetokobangunan_koma.models

data class ReviewShopModel(
    val id_review_shop: String? = null,
    val id_shop: String? = null,
    val shop_photo_url: String? = null,
    val shop_name: String? = null,
    val provinsi: String? = null,
    val category: String? = null,
    val result: Double? = null,
    val total_reviewer: Int? = null,
)
