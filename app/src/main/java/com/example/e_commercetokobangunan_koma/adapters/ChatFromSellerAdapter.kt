package com.example.e_commercetokobangunan_koma.adapters

import android.graphics.Color
import android.graphics.Color.parseColor
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commercetokobangunan_koma.R
import com.example.e_commercetokobangunan_koma.models.ChatFromSellerModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ChatFromSellerAdapter(idShop: String): RecyclerView.Adapter<ChatFromSellerAdapter.ViewHolder>() {

    var idShop: String = idShop

    private var chats: MutableList<ChatFromSellerModel> = mutableListOf()
    fun setChats(data: MutableList<ChatFromSellerModel>){
        this.chats = data
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemChatLayout: LinearLayout
        val message: TextView
        val chatDate: TextView

        init {
            itemChatLayout = view.findViewById(R.id.item_chat_layout)
            message = view.findViewById(R.id.chat_message)
            chatDate = view.findViewById(R.id.chat_message_date)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatFromSellerAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat, parent, false)

        return ChatFromSellerAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatFromSellerAdapter.ViewHolder, position: Int) {
        if(!chats.isNullOrEmpty()){
            val date: DateFormat = SimpleDateFormat("dd-MM-yyyy")
            val hour: DateFormat = SimpleDateFormat("HH-mm")

            if(this.idShop.equals(chats.get(position).id_user.toString())){
                holder.message.text = chats.get(position).message
                holder.itemChatLayout.gravity = Gravity.END
                holder.message.background = ColorDrawable(parseColor("#40916c"))
                holder.message.setTextColor(Color.WHITE)
            }else{
                holder.message.text = chats.get(position).message
                holder.itemChatLayout.gravity = Gravity.START
                holder.message.setTextColor(Color.BLACK)
            }

            if(date.format(Date()).equals(date.format(chats.get(position).date as Date))){
                holder.chatDate.text = hour.format(chats.get(position).date).toString()
            }else{
                holder.chatDate.text = date.format(chats.get(position).date).toString()
            }

        }
    }

    override fun getItemCount(): Int {
        return chats.size
    }
}