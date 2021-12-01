package com.example.e_commercetokobangunan_koma.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.e_commercetokobangunan_koma.models.ShopProductListModel

class ShopProductListViewModel: ViewModel() {

    private var productShop: MutableLiveData<MutableList<ShopProductListModel>> = MutableLiveData<MutableList<ShopProductListModel>>().apply { postValue(
        mutableListOf()
    )}
    fun getProductShop(): MutableLiveData<MutableList<ShopProductListModel>>{
        return this.productShop
    }
    fun setProductShop(productShop: MutableList<ShopProductListModel>){
        this.productShop.value = productShop
    }

    fun addProductShop(product: ShopProductListModel){
        this.productShop.value?.add(product)
    }

}