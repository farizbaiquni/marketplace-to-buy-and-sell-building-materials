package com.example.e_commercetokobangunan_koma.models

import java.util.*

data class TransactionModel(
    val id_buyer: String? = null,
    val id_product: String? = null,
    val buyer_name: String? = null,
    val date: Date? = null,
    val total_unit_product: Int? = null,
    val unit_price: Int? = null,
    val delivery_price: Int? = null,
    val total_price: Int? = null,
    val provinsi_buyer: String? = null,
    val kabupaten_kota_buyer: String? = null,
    val kecamatan_buyer: String? = null,
    val kelurahan_desa_buyer: String? = null,
    val alamat_detail_buyer: String? = null,
)
