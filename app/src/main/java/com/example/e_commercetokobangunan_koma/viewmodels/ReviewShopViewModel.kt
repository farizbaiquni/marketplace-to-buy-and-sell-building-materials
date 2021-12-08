package com.example.e_commercetokobangunan_koma.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.e_commercetokobangunan_koma.models.ReviewShopModel
import com.example.e_commercetokobangunan_koma.models.ReviewShopUserModel

class ReviewShopViewModel: ViewModel() {

    private var historyReviewShopUser: MutableLiveData<MutableList<ReviewShopUserModel>> = MutableLiveData<MutableList<ReviewShopUserModel>>().apply {
        postValue(mutableListOf())
    }
    fun setHistoryReviewShopUser(data: MutableList<ReviewShopUserModel>){
        this.historyReviewShopUser.value = data
    }
    fun getHistoryReviewShopUser(): MutableLiveData<MutableList<ReviewShopUserModel>>{
        return this.historyReviewShopUser
    }


    private var historyReview: MutableLiveData<MutableList<String>> = MutableLiveData<MutableList<String>>().apply {
        postValue(mutableListOf())
    }
    fun getHistoryReview(): MutableLiveData<MutableList<String>>{
        return this.historyReview
    }
    fun setHistoryReview(data: MutableList<String>){
        this.historyReview.value = data
    }


    private var permittedReview: MutableLiveData<MutableList<String>> = MutableLiveData<MutableList<String>>().apply {
        postValue(mutableListOf())
    }
    fun getPermittedReview(): MutableLiveData<MutableList<String>>{
        return this.permittedReview
    }
    fun setPermittedReview(data: MutableList<String>){
        this.permittedReview.value = data
    }


    private var reviewShop: MutableLiveData<ReviewShopModel> = MutableLiveData<ReviewShopModel>().apply {
        postValue(null)
    }
    fun setReviewShop(data: ReviewShopModel){
        this.reviewShop.value = data
    }
    fun getReviewShop(): MutableLiveData<ReviewShopModel>{
        return this.reviewShop
    }

}