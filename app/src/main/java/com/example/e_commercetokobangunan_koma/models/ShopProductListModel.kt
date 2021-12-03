package com.example.e_commercetokobangunan_koma.models

data class ShopProductListModel(
    val name: String? = null,
    val sold: Long? = null,
    val jumlahStok: Long? = null,
    @field:JvmField // use this annotation if your Boolean field is prefixed with 'is'
    val statusProduk: Boolean? = null,
)