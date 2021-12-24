package com.example.e_commercetokobangunan_koma.models

import java.util.*

data class ChatListModel(
    val id_room: String? = null,
    val id_shop: String? = null,
    val id_buyer: String? = null,
    val name_shop: String? = null,
    val name_buyer: String? = null,
    val photo: String? = null,
    val chat: String? = null,
    val date: Date? = null,
)
