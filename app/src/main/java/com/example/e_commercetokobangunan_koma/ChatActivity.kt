package com.example.e_commercetokobangunan_koma

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commercetokobangunan_koma.adapters.ChatAdapter
import com.example.e_commercetokobangunan_koma.databinding.ActivityChatBinding
import com.example.e_commercetokobangunan_koma.models.ChatModel
import com.example.e_commercetokobangunan_koma.viewmodels.ChatViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class ChatActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityChatBinding
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var viewModel: ChatViewModel
    private lateinit var idShop: String
    private lateinit var photoShop: String
    private lateinit var nameShop: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Action Bar
        supportActionBar?.title = "Pesan"

        val bundle: Bundle? = intent.extras
        idShop = bundle?.get("idShop").toString()
        photoShop = bundle?.get("photoShop").toString()
        nameShop = bundle?.get("nameShop").toString()

        // Initialize Firebase Auth
        auth = Firebase.auth

        viewModel = ChatViewModel()

        viewModel.getIsFirst().observe(this){ isFirst ->
            if(isFirst != null){
                binding.sendMessage.setOnClickListener(View.OnClickListener {
                    sendMessage(idShop, auth.currentUser!!.uid, photoShop, "",
                        binding.chatMessage.text.toString(), isFirst, viewModel.getIdRoom().value.toString(), nameShop)
                })
            }
        }

        viewModel.getIdRoom().observe(this){ idRoom ->
            if(!idRoom.isNullOrEmpty()){
                getChats(idRoom)
            }
        }

        viewModel.getChats().observe(this){ chats ->
            if(!chats.isNullOrEmpty()){
                chatAdapter.setChats(chats)
            }
        }

    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            Firebase.firestore.collection("users")
                .whereEqualTo("id_user", currentUser.uid)
                .get()
                .addOnSuccessListener { documents ->
                    if(documents.isEmpty){
                        startActivity(Intent(this, AddProfileUserActivity::class.java))
                    }
                }
            chatAdapter = ChatAdapter(currentUser.uid)
            binding.recyclerViewChat.layoutManager = LinearLayoutManager(this)
            binding.recyclerViewChat.adapter = chatAdapter

            getRoomChat(idShop, currentUser.uid)
        }else{
            startActivity(Intent(this, WelcomeActivity::class.java))
        }
    }


    private fun getRoomChat(idShop: String, idUser: String){
        Firebase.firestore.collection("rooms_chat")
            .whereEqualTo("id_users", mutableListOf(idShop, idUser))
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    viewModel.setIdRoom(document.id)
                }
                if(documents.isEmpty){
                    viewModel.setIsFirst(true)

                }else{
                    viewModel.setIsFirst(false)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Gagal mendapatkan data", Toast.LENGTH_SHORT).show()
            }
    }


    private fun getChats(idRoom: String){
        var messages: MutableList<ChatModel> = mutableListOf()
        var chatsRef = Firebase.firestore.collection("chats").whereEqualTo("id_room", idRoom)
            chatsRef.addSnapshotListener { value, e ->
                if (e != null) {
                    return@addSnapshotListener
                }

                messages.clear()
                for (doc in value!!) {
                    messages.add(ChatModel(
                        doc.data.get("id_room").toString(),
                        doc.data.get("id_user").toString(),
                        doc.data.get("message").toString(),
                        (doc.data.get("date") as Timestamp).toDate(),
                    ))
                }

                messages.sortBy { it.date }
                viewModel.setChats(messages)

            }

    }// End getChats


    private fun sendMessage(idShop: String, idUser: String, photoShop: String, photoUser: String,
                            message: String, isFirst: Boolean, idRoom: String, nameShop: String){

        if(isFirst.equals(true)){
            val users = mutableListOf<String>(idShop, idUser)
            val photoUsers = mutableListOf<String>(photoShop, "")
            val names = mutableListOf<String>(nameShop, auth.currentUser?.displayName.toString())

            val rooms = hashMapOf(
                "id_users" to users,
                "photo_users" to photoUsers,
                "name_users" to names,
                "last_chat" to "",
                "last_chat_date" to Timestamp.now().toDate()
            )

            val roomRef = Firebase.firestore.collection("rooms_chat").document()
            val chatRef = Firebase.firestore.collection("chats").document()

            Firebase.firestore.runTransaction { transaction ->
                transaction.set(roomRef, rooms)
                transaction.set(chatRef, ChatModel(roomRef.id, idUser, message, Timestamp.now().toDate()))
                transaction.update(roomRef, "last_chat", message)

            }.addOnSuccessListener { result ->
                binding.chatMessage.setText("")
                getRoomChat(idShop, idUser)
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Chat gagal dikirim", Toast.LENGTH_SHORT).show()
            }
        
        }else{

            var dateChat: Date = Timestamp.now().toDate()

            val roomRef = Firebase.firestore.collection("rooms_chat").document(idRoom)
            val chatRef = Firebase.firestore.collection("chats").document()

            Firebase.firestore.runTransaction { transaction ->
                transaction.set(chatRef, ChatModel(idRoom, idUser, message, dateChat))
                transaction.update(roomRef, "last_chat", message)
                transaction.update(roomRef, "last_chat_date", dateChat)

            }.addOnSuccessListener { result ->
                binding.chatMessage.setText("")
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Chat gagal dikirim", Toast.LENGTH_SHORT).show()
            }
        }
    }// End fun

}