package com.example.e_commercetokobangunan_koma.models

import java.util.*

data class ShopModel(
    val id_user: String? = null,
    val photo_url: String? = null,
    val nama: String? = null,
    val deskripsi: String? = null,
    val date: Date? = null,
    val provinsi: String? = null,
    val kabupaten_kota: String? = null,
    val kecamatan: String? = null,
    val kelurahan_desa: String? = null,
    val alamat_detail: String? = null,
)
