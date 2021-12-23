package com.example.e_commercetokobangunan_koma

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.e_commercetokobangunan_koma.databinding.ActivityChatListBinding
import com.example.e_commercetokobangunan_koma.databinding.ActivityMainBinding
import com.example.e_commercetokobangunan_koma.models.ChatListModel
import com.example.e_commercetokobangunan_koma.viewmodels.ChatListViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.properties.Delegates

class ChatListActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityChatListBinding
    private lateinit var viewModel: ChatListViewModel
    private var isBuyer: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Action Bar
        supportActionBar?.title = "Pesan Masuk"

        // Initialize Firebase Auth
        auth = Firebase.auth

        val bundle: Bundle? = intent.extras
        isBuyer = bundle?.get("isBuyer") as Boolean

        viewModel = ChatListViewModel()

    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser == null){
            startActivity(Intent(this, WelcomeActivity::class.java))
        }else{

        }
    }

    private fun getIdShop(idUser: String){
        Firebase.firestore.collection("shop")
            .whereEqualTo("id_user", idUser)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {

                }
            }
            .addOnFailureListener { exception -> }
    }


    private fun getChatList(idSender: String){

        var chatList: MutableList<ChatListModel> = mutableListOf()

        Firebase.firestore.collection("rooms_chat")
            .whereArrayContains("id_users", idSender)
            .get()
            .addOnSuccessListener { documents ->
                chatList.clear()
                for (document in documents) {
                    chatList.add(ChatListModel(
                        document.id,
                        (document.data.get("id_users") as MutableList<*>).get(0).toString(),
                        (document.data.get("id_users") as MutableList<*>).get(1).toString(),
                        (document.data.get("photo_users") as MutableList<*>).get(1).toString(),
                        document.data.get("last_chat").toString(),
                        document.data.get("last_chat_name").toString(),
                        (document.data.get("last_chat_date") as Timestamp).toDate(),
                    ))
                }
            }
            .addOnFailureListener { exception ->

            }
    }

}