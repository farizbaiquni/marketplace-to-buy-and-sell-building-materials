package com.example.e_commercetokobangunan_koma.viewmodels

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddProfileUserViewModel: ViewModel() {
    private var photo: MutableLiveData<Uri> = MutableLiveData<Uri>().apply {
        postValue(null)
    }
    fun setPhoto(data: Uri){
        this.photo.value = data
    }
    fun getPhoto(): MutableLiveData<Uri> {
        return this.photo
    }
}