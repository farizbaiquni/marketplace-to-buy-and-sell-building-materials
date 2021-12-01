package com.example.e_commercetokobangunan_koma.models

import java.math.BigInteger

data class ProductModel(
    val name: String? = null,
    val harga: Long? = null,
    val deskripsi: String? = null,
    val linkVideo: String? = null,
    val jumlahStok: Long? = null,
    val berat: Float? = null,
    @field:JvmField // use this annotation if your Boolean field is prefixed with 'is'
    val kondisiBaru: Boolean? = null,
)
