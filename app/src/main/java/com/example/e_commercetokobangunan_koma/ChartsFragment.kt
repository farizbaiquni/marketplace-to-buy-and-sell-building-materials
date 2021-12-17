package com.example.e_commercetokobangunan_koma

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commercetokobangunan_koma.adapters.ChartsAdapter
import com.example.e_commercetokobangunan_koma.databinding.ChartsFragmentBinding
import com.example.e_commercetokobangunan_koma.models.ChartsModel
import com.example.e_commercetokobangunan_koma.viewmodels.ChartsViewModel
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ChartsFragment() : Fragment(R.layout.charts_fragment) {

    private var _binding: ChartsFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ChartsViewModel
    private lateinit var chartsAdapter: ChartsAdapter
    val kategoriMenu = arrayListOf<String>("Kualitas Pengemasan", "Deskripsi dan Foto", "Fast Repone", "Keramahan")
    private var categories: MutableList<String> = mutableListOf<String>(
        "kualitas_pengemasan",
        "deskripsi_foto",
        "fast_respone",
        "keramahan", )


    override fun onResume() {
        super.onResume()
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_drop_down_menu, kategoriMenu)
        binding.autoCompleteChartsCategory.setAdapter(arrayAdapter)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ChartsFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ChartsViewModel::class.java)

        chartsAdapter = ChartsAdapter(requireContext())
        binding.recyclerViewCharts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewCharts.adapter = chartsAdapter

        binding.autoCompleteChartsCategory.setText(kategoriMenu.get(0), false)

        viewModel.getKategori().observe(viewLifecycleOwner){ kategori ->
            if(!kategori.isNullOrEmpty()){
                getChartsList(kategori)
            }
        }
        viewModel.getChartsList().observe(viewLifecycleOwner){ reviewShop ->
            if(!reviewShop.isNullOrEmpty()){
                chartsAdapter.setChartsList(reviewShop)
            }
        }

        binding.autoCompleteChartsCategory.setOnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
             viewModel.setKetegori(categories.get(position))
        }

    }



    fun getChartsList(kategori: String){
        var chartsModel: MutableList<ChartsModel> = mutableListOf()
        var docsRef = Firebase.firestore.collection("review_shop").whereEqualTo("category", kategori)
        docsRef.orderBy("result", Query.Direction.DESCENDING)
        docsRef.limit(10)
        docsRef
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    chartsModel.add(
                        ChartsModel(
                            document.data?.get("id_shop").toString(),
                            document.data?.get("shop_photo_url").toString(),
                            document.data?.get("shop_name").toString(),
                            document.data?.get("provinsi").toString(),
                            document.data?.get("result").toString(),
                            document.data?.get("total_reviewer").toString(),
                        )
                    )
                }
                chartsModel.sortByDescending { it.nilai_ulasan }
                viewModel.setChartsList(chartsModel)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Gagal mendapatkan data", Toast.LENGTH_SHORT).show()
            }
    }

}