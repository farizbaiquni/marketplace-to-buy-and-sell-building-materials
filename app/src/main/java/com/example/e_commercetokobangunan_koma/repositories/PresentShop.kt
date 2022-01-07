package com.example.e_commercetokobangunan_koma.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object PresentShop {

    var listenerStatus: Boolean = false

    fun checkPresentShop(){
        var userId: String = Firebase.auth.currentUser?.uid ?: ""

        val database = Firebase.database
        val myConnectionsRef = database.getReference("/shopPresentStatus/${userId}/connections")
        val lastOnlineRef = database.getReference("/users/${userId}/lastOnline")

        val connectedRef = database.getReference(".info/connected")
        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                listenerStatus = true
                if (connected) {
                    val con = myConnectionsRef.push()
                    con.onDisconnect().removeValue()
                    lastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP)
                    con.setValue(java.lang.Boolean.TRUE)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}