package com.example.e_commercetokobangunan_koma

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commercetokobangunan_koma.adapters.ChatFromSellerAdapter
import com.example.e_commercetokobangunan_koma.databinding.ActivityChatFromSellerBinding
import com.example.e_commercetokobangunan_koma.models.ChatFromSellerModel
import com.example.e_commercetokobangunan_koma.models.ChatModel
import com.example.e_commercetokobangunan_koma.viewmodels.ChatFromSellerViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class ChatFromSellerActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityChatFromSellerBinding
    private lateinit var chatFromSellerAdapter: ChatFromSellerAdapter
    private lateinit var viewModel: ChatFromSellerViewModel
    private lateinit var idRoom: String
    private lateinit var idShop: String
    private lateinit var nameBuyer: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatFromSellerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Action Bar
        supportActionBar?.title = "Pesan"

        val bundle: Bundle? = intent.extras
        idRoom = bundle?.get("idRoom").toString()
        idShop = bundle?.get("idShop").toString()
        nameBuyer = bundle?.get("nameBuyer").toString()

        // Initialize Firebase Auth
        auth = Firebase.auth

        chatFromSellerAdapter = ChatFromSellerAdapter(idShop)
        binding.recyclerViewChatFromSeller.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewChatFromSeller.adapter = chatFromSellerAdapter

        viewModel = ChatFromSellerViewModel()
        viewModel.getChats().observe(this){ chats ->
            if(!chats.isNullOrEmpty()){
                chatFromSellerAdapter.setChats(chats)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, WelcomeActivity::class.java))
        }else{
            Firebase.firestore.collection("users")
                .whereEqualTo("id_user", currentUser.uid)
                .get()
                .addOnSuccessListener { documents ->
                    if(documents.isEmpty){
                        startActivity(Intent(this, AddProfileUserActivity::class.java))
                    }else{
                        getChats(idRoom)
                        binding.sendMessage.setOnClickListener(View.OnClickListener {
                            sendMessage(idShop, binding.chatMessage.text.toString(), idRoom)
                        })
                    }
                }
        }
    }//End onStart


    private fun getChats(idRoom: String){
        var chats: MutableList<ChatFromSellerModel> = mutableListOf()

        var chatsRef = Firebase.firestore.collection("chats").whereEqualTo("id_room", idRoom)
        chatsRef.addSnapshotListener { value, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            chats.clear()
            for (doc in value!!) {
                chats.add(ChatFromSellerModel(
                    doc.id,
                    doc.data.get("id_user").toString(),
                    doc.data.get("message").toString(),
                    (doc.data.get("date") as Timestamp).toDate(),
                ))
            }

            chats.sortBy { it.date }
            viewModel.setChats(chats)

        }
    }

    private fun sendMessage(idShop: String, message: String,  idRoom: String){
        binding.sendMessage.isEnabled = false
        var dateChat: Date = Timestamp.now().toDate()
        val roomRef = Firebase.firestore.collection("rooms_chat").document(idRoom)
        val chatRef = Firebase.firestore.collection("chats").document()

        Firebase.firestore.runTransaction { transaction ->
            transaction.set(chatRef, ChatModel(idRoom, idShop, message, dateChat))
            transaction.update(roomRef, "last_chat", message)
            transaction.update(roomRef, "last_chat_date", dateChat)

        }.addOnSuccessListener { result ->
            binding.chatMessage.setText("")
            binding.sendMessage.isEnabled = true
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Chat gagal dikirim", Toast.LENGTH_SHORT).show()
            binding.sendMessage.isEnabled = true
        }
    }// End fun

}