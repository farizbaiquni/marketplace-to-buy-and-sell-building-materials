package com.example.e_commercetokobangunan_koma.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.e_commercetokobangunan_koma.models.ReviewShopModel

class ShopViewModel: ViewModel() {

    private var reviewShop: MutableLiveData<MutableMap<String, ReviewShopModel>> = MutableLiveData<MutableMap<String, ReviewShopModel>>().apply {
        postValue(mutableMapOf())
    }
    fun getReviewShop(): MutableLiveData<MutableMap<String, ReviewShopModel>> {
        return this.reviewShop
    }
    fun setReviewShop(data: MutableMap<String, ReviewShopModel>){
        this.reviewShop.value = data
    }

}