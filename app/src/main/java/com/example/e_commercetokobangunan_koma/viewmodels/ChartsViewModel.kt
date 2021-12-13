package com.example.e_commercetokobangunan_koma.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.e_commercetokobangunan_koma.models.ChartsModel

class ChartsViewModel : ViewModel() {

    private var chartsList: MutableLiveData<MutableList<ChartsModel>> = MutableLiveData<MutableList<ChartsModel>>().apply {
        postValue(mutableListOf())
    }
    fun setChartsList(data: MutableList<ChartsModel>){
        this.chartsList.value = data
    }
    fun getChartsList(): MutableLiveData<MutableList<ChartsModel>>{
        return this.chartsList
    }


    private var kategori: MutableLiveData<String> = MutableLiveData<String>().apply {
        postValue("kualitas_pengemasan")
    }
    fun setKetegori(data: String){
        this.kategori.value = data
    }
    fun getKategori(): MutableLiveData<String>{
        return this.kategori
    }


}