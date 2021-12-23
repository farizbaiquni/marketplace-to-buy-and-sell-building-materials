package com.example.e_commercetokobangunan_koma.models

import com.google.firebase.Timestamp

data class ChatModel(
    val id_room: String? = null,
    val id_user: String? = null,
    val message: String? = null,
    val date: Timestamp? = null,
)
