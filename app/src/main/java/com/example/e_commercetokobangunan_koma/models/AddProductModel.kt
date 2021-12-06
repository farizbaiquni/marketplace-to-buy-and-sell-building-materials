package com.example.e_commercetokobangunan_koma.models

import java.math.BigInteger

data class AddProductModel(
    val id_user: String? = null,
    val name: String? = null,
    val price: Long? = null,
    val description: String? = null,
    val link_video: String? = null,
    val stock: Long? = null,
    val weight: Double? = null,
    @field:JvmField // use this annotation if your Boolean field is prefixed with 'is'
    val new_condition: Boolean? = null,
)
