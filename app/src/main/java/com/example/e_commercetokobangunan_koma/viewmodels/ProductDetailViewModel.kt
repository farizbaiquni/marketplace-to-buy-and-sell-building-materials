package com.example.e_commercetokobangunan_koma.viewmodels

import androidx.lifecycle.MutableLiveData
import com.example.e_commercetokobangunan_koma.models.ProductDetailModel

class ProductDetailViewModel {

    private var productDetail: MutableLiveData<ProductDetailModel> = MutableLiveData<ProductDetailModel>().apply {
        postValue(null)
    }
    fun getProductDetail(): MutableLiveData<ProductDetailModel>{
        return this.productDetail
    }
    fun setProductDetail(product: ProductDetailModel){
        this.productDetail.value = product
    }


    private var productPhotosUrl: MutableLiveData<MutableList<String>> = MutableLiveData<MutableList<String>>().apply {
        postValue(mutableListOf())
    }
    fun getProductPhotosUrl(): MutableLiveData<MutableList<String>>{
        return this.productPhotosUrl
    }
    fun setProductPhotosUrl(photosUrl: MutableList<String>){
        this.productPhotosUrl.value = photosUrl
    }


    private var idAndPhotoShop: MutableLiveData<MutableList<String>> = MutableLiveData<MutableList<String>>().apply {
        postValue(null)
    }
    fun getIdAndPhotoShop(): MutableLiveData<MutableList<String>>{
        return this.idAndPhotoShop
    }
    fun setIdAndPhotoShop(data: MutableList<String>){
        this.idAndPhotoShop.value = data
    }

}