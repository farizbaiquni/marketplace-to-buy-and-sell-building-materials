package com.example.e_commercetokobangunan_koma.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.e_commercetokobangunan_koma.models.ChatModel

class ChatViewModel: ViewModel() {

    private var chats: MutableLiveData<MutableList<ChatModel>> = MutableLiveData<
            MutableList<ChatModel>>().apply { postValue(mutableListOf()) }
    fun getChats(): MutableLiveData<MutableList<ChatModel>> {
        return this.chats
    }
    fun setChats(data: MutableList<ChatModel>){
        this.chats.value = data
    }


    private var isFirst: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { postValue(null) }
    fun getIsFirst(): MutableLiveData<Boolean> {
        return this.isFirst
    }
    fun setIsFirst(data: Boolean){
        this.isFirst.value = data
    }


    private var idRoom: MutableLiveData<String> = MutableLiveData<String>().apply { postValue(null) }
    fun getIdRoom(): MutableLiveData<String> {
        return this.idRoom
    }
    fun setIdRoom(data: String){
        this.idRoom.value = data
    }

}