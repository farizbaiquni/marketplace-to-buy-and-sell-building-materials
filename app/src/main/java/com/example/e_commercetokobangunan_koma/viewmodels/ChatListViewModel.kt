package com.example.e_commercetokobangunan_koma.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.e_commercetokobangunan_koma.models.ChatListModel
import com.example.e_commercetokobangunan_koma.models.ProductListModel

class ChatListViewModel: ViewModel() {

    private var chatList: MutableLiveData<MutableList<ChatListModel>> = MutableLiveData<
            MutableList<ChatListModel>>().apply { postValue(mutableListOf()) }
    fun getChatList(): MutableLiveData<MutableList<ChatListModel>> {
        return this.chatList
    }
    fun setChatList(data: MutableList<ChatListModel>){
        this.chatList.value = data
    }


    private var idShop: MutableLiveData<String> = MutableLiveData<String>().apply { postValue(null) }
    fun getIdShop(): MutableLiveData<String> {
        return this.idShop
    }
    fun setIdShop(data: String){
        this.idShop.value = data
    }

}