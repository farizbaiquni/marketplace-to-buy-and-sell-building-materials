package com.example.e_commercetokobangunan_koma.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.e_commercetokobangunan_koma.models.ReviewShopModel
import com.example.e_commercetokobangunan_koma.models.ReviewShopUserModel

class ReviewShopViewModel: ViewModel() {

    private var permittedReview: MutableLiveData<MutableList<String>> = MutableLiveData<MutableList<String>>().apply {
        postValue(mutableListOf())
    }
    fun getPermittedReview(): MutableLiveData<MutableList<String>>{
        return this.permittedReview
    }
    fun setPermittedReview(data: MutableList<String>){
        this.permittedReview.value = data
    }



    private var historyReviewShopUser: MutableLiveData<MutableMap<String, ReviewShopUserModel>> = MutableLiveData<MutableMap<String, ReviewShopUserModel>>().apply {
        postValue(mutableMapOf())
    }
    fun setHistoryReviewShopUser(data: MutableMap<String, ReviewShopUserModel>){
        this.historyReviewShopUser.value = data
    }
    fun getHistoryReviewShopUser(): MutableLiveData<MutableMap<String, ReviewShopUserModel>>{
        return this.historyReviewShopUser
    }

}