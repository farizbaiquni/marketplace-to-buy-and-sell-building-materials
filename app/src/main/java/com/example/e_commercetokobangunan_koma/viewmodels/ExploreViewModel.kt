package com.example.e_commercetokobangunan_koma.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.e_commercetokobangunan_koma.models.ProductListModel

class ExploreViewModel : ViewModel() {
    private var productList: MutableLiveData<MutableList<ProductListModel>> = MutableLiveData<
        MutableList<ProductListModel>>().apply { postValue(mutableListOf()) }

    fun getProductList(): MutableLiveData<MutableList<ProductListModel>>{
        return this.productList
    }

    fun setProductList(products: MutableList<ProductListModel>){
        this.productList.value = products
    }
}