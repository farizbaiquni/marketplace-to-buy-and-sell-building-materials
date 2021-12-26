package com.example.e_commercetokobangunan_koma.models

data class ProductDetailModel(
    var id_product: String? = null,
    var photos: MutableList<String>? = null,
    var name: String? = null,
    var price: Long? = null,
    var description: String? = null,
    var stock: Long? = null,
    var weight: Double? = null,
    var condition_new: Boolean? = null,
    var shop_photo: String? = null,
    var shop_name: String? = null,

    )
