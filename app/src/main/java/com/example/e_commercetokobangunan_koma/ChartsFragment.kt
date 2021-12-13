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
import com.example.e_commercetokobangunan_koma.databinding.ChartsFragmentBinding
import com.example.e_commercetokobangunan_koma.viewmodels.ChartsViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ChartsFragment(idUser: String) : Fragment(R.layout.charts_fragment) {

    private var idUser: String = idUser
    private var _binding: ChartsFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ChartsViewModel

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

        val languages = resources.getStringArray(R.array.charts_category)
        val arrayAdapter =ArrayAdapter(requireContext(), R.layout.item_drop_down_menu, languages)
        binding.autoCompleteChartsCategory.setAdapter(arrayAdapter)
        binding.autoCompleteChartsCategory.setText(languages.get(0), false)

        binding.autoCompleteChartsCategory.setOnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
            Toast.makeText(requireContext(), languages.get(position).toString(), Toast.LENGTH_SHORT).show()
        }

        getChartsList(this.idUser)

    }

    fun getChartsList(idUser: String){
        var fotoUrl: String = ""
        var namaToko: String = ""
        var provinsi: String = ""

        Firebase.firestore.collection("shop").whereEqualTo("id_user", idUser)
            .get()
            .addOnSuccessListener { documents ->
                if(documents.isEmpty.equals(false)){
                    for (document in documents) {
                        fotoUrl = document.data.get("photo_url").toString()
                        namaToko = document.data.get("nama").toString()
                        provinsi = document.data.get("provinsi").toString()
                    }



                }else{
                    // Data Shop not Found
                }
            }
            .addOnFailureListener { exception ->
                // Log.w(TAG, "Error getting documents: ", exception)
            }

    }

}