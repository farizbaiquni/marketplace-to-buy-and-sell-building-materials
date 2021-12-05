package com.example.e_commercetokobangunan_koma.models

import java.util.*

data class TransactionModel(
    val idBuyer: String? = null,
    val idProduct: String? = null,
    val buyerName: String? = null,
    val date: Date? = null,
    val totalUnitProduct: Int? = null,
    val unitPrice: Int? = null,
    val deliveryPrice: Int? = null,
    val totalPrice: Int? = null,
    val provinsiBuyer: String? = null,
    val kabupatenKotaBuyer: String? = null,
    val kecamatanBuyer: String? = null,
    val kelurahanDesaBuyer: String? = null,
    val alamatDetailBuyer: String? = null,
)
