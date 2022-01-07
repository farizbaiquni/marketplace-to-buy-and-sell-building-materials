package com.example.e_commercetokobangunan_koma

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.io.IOException
import java.util.*

class ChatFromSellerActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityChatFromSellerBinding
    private lateinit var chatFromSellerAdapter: ChatFromSellerAdapter
    private lateinit var viewModel: ChatFromSellerViewModel
    private lateinit var idRoom: String
    private lateinit var idShop: String
    private lateinit var idBuyer: String
    private lateinit var nameBuyer: String
    private lateinit var photoBuyer: String

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
        idBuyer = bundle?.get("idBuyer").toString()
        nameBuyer = bundle?.get("nameBuyer").toString()
        photoBuyer = bundle?.get("photo")?.toString() ?: ""

        // Initialize Firebase Auth
        auth = Firebase.auth

        chatFromSellerAdapter = ChatFromSellerAdapter(idShop)
        binding.recyclerViewChatFromSeller.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewChatFromSeller.adapter = chatFromSellerAdapter

        try {
            Picasso.get().load(photoBuyer).centerCrop().resize(700, 700).into(binding.photoChatFromSeller)
        }catch (e: Exception){}
        binding.usernameChatFromSeller.text = nameBuyer
        binding.topAppBar.setNavigationOnClickListener {
            startActivity(Intent(this, ChatListActivity::class.java  ))
        }

        viewModel = ChatFromSellerViewModel()
        viewModel.getChats().observe(this){ chats ->
            if(!chats.isNullOrEmpty()){
                chatFromSellerAdapter.setChats(chats)
            }
        }

        viewModel.getisOnline().observe(this){ isOnline ->
            if(isOnline){
                binding.onlineStatusChatFromSeller.visibility = View.VISIBLE
            }else{
                binding.onlineStatusChatFromSeller.visibility = View.GONE

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

                        val connectionsRef = Firebase.database.getReference("/shopPresentStatus/${idBuyer}/connections")
                        val isOnlineListener = object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if(dataSnapshot.exists()){
                                    viewModel.setisOnline(true)
                                    Toast.makeText(this@ChatFromSellerActivity, "ONLINE", Toast.LENGTH_SHORT).show()
                                }else{
                                    viewModel.setisOnline(false)
                                    Toast.makeText(this@ChatFromSellerActivity, "OFFLINE", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                            }
                        }
                        connectionsRef.addValueEventListener(isOnlineListener)
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