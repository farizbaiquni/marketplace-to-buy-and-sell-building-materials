package com.example.e_commercetokobangunan_koma.adapters

import android.content.Intent
import android.graphics.Color
import android.graphics.Color.*
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commercetokobangunan_koma.ProductDetailActivity
import com.example.e_commercetokobangunan_koma.R
import com.example.e_commercetokobangunan_koma.models.ChatModel
import com.squareup.picasso.Picasso
import java.io.IOException
import java.text.NumberFormat
import java.util.*

class ChatAdapter(authUser: String): RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    var authUser: String = authUser

    private var chats: MutableList<ChatModel> = mutableListOf()
    fun setChats(data: MutableList<ChatModel>){
        this.chats = data
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemChatLayout: LinearLayout
        val message: TextView

        init {
            itemChatLayout = view.findViewById(R.id.item_chat_layout)
            message = view.findViewById(R.id.chat_message)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat, parent, false)

        return ChatAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatAdapter.ViewHolder, position: Int) {
        if(!chats.isNullOrEmpty()){
            if(this.authUser.equals(chats.get(position).id_user.toString())){
                holder.message.text = chats.get(position).message
                holder.itemChatLayout.gravity = Gravity.END
                holder.message.background = ColorDrawable(parseColor("#40916c"))
                holder.message.setTextColor(Color.WHITE)
            }else{
                holder.message.text = chats.get(position).message
                holder.itemChatLayout.gravity = Gravity.START
                holder.message.setTextColor(Color.BLACK)
            }
        }
    }

    override fun getItemCount(): Int {
        return chats.size
    }

}