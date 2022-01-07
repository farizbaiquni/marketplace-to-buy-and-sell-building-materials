package com.example.e_commercetokobangunan_koma.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.e_commercetokobangunan_koma.models.ChatFromSellerModel

class ChatFromSellerViewModel: ViewModel() {

    private var chats: MutableLiveData<MutableList<ChatFromSellerModel>> = MutableLiveData<
            MutableList<ChatFromSellerModel>>().apply { postValue(mutableListOf()) }
    fun getChats(): MutableLiveData<MutableList<ChatFromSellerModel>> {
        return this.chats
    }
    fun setChats(data: MutableList<ChatFromSellerModel>){
        this.chats.value = data
    }

    private var isOnline: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply { postValue(false) }
    fun getisOnline(): MutableLiveData<Boolean> {
        return this.isOnline
    }
    fun setisOnline(data: Boolean){
        this.isOnline.value = data
    }

}