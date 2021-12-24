package com.example.e_commercetokobangunan_koma.models

import com.google.firebase.Timestamp
import java.util.*

data class ChatModel(
    val id_room: String? = null,
    val id_user: String? = null,
    val message: String? = null,
    val date: Date? = null,
)
