package com.example.e_commercetokobangunan_koma.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.Image
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commercetokobangunan_koma.R
import com.example.e_commercetokobangunan_koma.adapters.ChatListAdapter.ViewHolder
import com.example.e_commercetokobangunan_koma.models.ChatListModel

class ChatListAdapter(): RecyclerView.Adapter<ViewHolder>() {

    private var chatList: MutableList<ChatListModel> = mutableListOf()
    fun setChatList(data: MutableList<ChatListModel>){
        this.chatList = data
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val photo: ImageView
        val name: TextView
        val date: TextView
        val chat: TextView

        init {
            photo = view.findViewById(R.id.chat_list_photo)
            name = view.findViewById(R.id.chat_list_name)
            date = view.findViewById(R.id.chat_list_date)
            chat = view.findViewById(R.id.chat_list_last_chat)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_list, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 0
    }

}