package com.example.e_commercetokobangunan_koma.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commercetokobangunan_koma.R
import com.example.e_commercetokobangunan_koma.ShopProductListActivity
import com.example.e_commercetokobangunan_koma.adapters.ChatListAdapter.ViewHolder
import com.example.e_commercetokobangunan_koma.models.ChatListModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ChatListAdapter(context: Context): RecyclerView.Adapter<ViewHolder>() {

    var mContext: Context = context

    private var chatList: MutableList<ChatListModel> = mutableListOf()
    fun setChatList(data: MutableList<ChatListModel>){
        this.chatList = data
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: CardView
        val photo: ImageView
        val name: TextView
        val date: TextView
        val chat: TextView

        init {
            card = view.findViewById(R.id.chat_list_card)
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

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(!chatList.isEmpty()){
            val date: DateFormat = SimpleDateFormat("dd-MM-yyyy")
            val hour: DateFormat = SimpleDateFormat("HH-mm")
            holder.chat.text = chatList.get(position).chat
            holder.name.text = chatList.get(position).name_buyer
            if(date.format(Date()).equals(date.format(chatList.get(position).date as Date))){
                holder.date.text = hour.format(chatList.get(position).date)
            }else{
                holder.date.text = date.format(chatList.get(position).date)
            }
            holder.card.setOnClickListener(View.OnClickListener {
                mContext.startActivity(Intent(mContext, ShopProductListActivity::class.java))
            })
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

}