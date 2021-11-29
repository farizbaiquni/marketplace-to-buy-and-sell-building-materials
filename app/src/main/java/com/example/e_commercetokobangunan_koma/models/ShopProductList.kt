package com.example.e_commercetokobangunan_koma.models

data class ShopProductList(
    val name: String? = null,
    val harga: Int? = null,
    val jumlahStok: Int? = null,
    @field:JvmField // use this annotation if your Boolean field is prefixed with 'is'
    val statusProduk: Boolean? = null,
)
