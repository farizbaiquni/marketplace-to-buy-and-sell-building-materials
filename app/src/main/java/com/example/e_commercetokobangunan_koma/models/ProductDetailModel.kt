package com.example.e_commercetokobangunan_koma.models

data class ProductDetailModel(
    var name: String? = null,
    var price: Long? = null,
    var description: String? = null,
    var stock: Long? = null,
    var weight: Double? = null,
    var conditionNew: Boolean? = null,
    var shop_photo: String? = null,
    var shop_name: String? = null,

)
