package com.example.e_commercetokobangunan_koma.viewmodels

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddProductViewModel: ViewModel() {

    private var selectedPhotos: MutableLiveData<MutableList<Uri>> = MutableLiveData<MutableList<Uri>>().apply { postValue(
        mutableListOf()
    )}

    fun getSelectedPhotos(): MutableLiveData<MutableList<Uri>> {
        return this.selectedPhotos
    }
    fun setSelectedPhotos(images: MutableList<Uri>){
        this.selectedPhotos.value = images
    }


    private var photosProductUrl: MutableLiveData<MutableList<String>> = MutableLiveData<MutableList<String>>().apply { postValue(
        mutableListOf()
    )}
    fun getPhotosProductUrl(): MutableLiveData<MutableList<String>>{
        return this.photosProductUrl
    }
    fun setPhotosProductUrl(productsUrl: MutableList<String>){
        this.photosProductUrl.value = productsUrl
    }

    fun addPhotosProductUrl(url: String){
        this.photosProductUrl.value?.add(url)
    }



    private var idProduct: MutableLiveData<String> = MutableLiveData<String>().apply { postValue("") }
    fun getIdProduct(): MutableLiveData<String>{
        return this.idProduct
    }
    fun setIdProduct(id: String){
        this.idProduct.value = id
    }

}