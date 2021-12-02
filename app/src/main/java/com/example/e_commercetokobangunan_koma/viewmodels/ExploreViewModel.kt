package com.example.e_commercetokobangunan_koma.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.e_commercetokobangunan_koma.models.ProductForListModel

class ExploreViewModel : ViewModel() {
    private var productList: MutableLiveData<MutableList<ProductForListModel>> = MutableLiveData<
        MutableList<ProductForListModel>>().apply { postValue(mutableListOf()) }

    fun getProductList(): MutableLiveData<MutableList<ProductForListModel>>{
        return this.productList
    }

    fun setProductList(products: MutableList<ProductForListModel>){
        this.productList.value = products
    }
}