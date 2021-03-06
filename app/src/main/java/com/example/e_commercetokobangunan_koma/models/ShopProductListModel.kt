package com.example.e_commercetokobangunan_koma.models

data class ShopProductListModel(
    val idProduct: String? = null,
    val photo_url: String? = null,
    val name: String? = null,
    val sold: Long? = null,
    val jumlahStok: Long? = null,
    @field:JvmField // use this annotation if your Boolean field is prefixed with 'is'
    val statusProduk: Boolean? = null,
    val photos_name: MutableList<String>? = mutableListOf(),
)
