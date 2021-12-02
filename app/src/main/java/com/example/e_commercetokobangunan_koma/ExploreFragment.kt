package com.example.e_commercetokobangunan_koma

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.e_commercetokobangunan_koma.adapters.ExploreAdapter
import com.example.e_commercetokobangunan_koma.databinding.ExploreFragmentBinding
import com.example.e_commercetokobangunan_koma.models.ProductForListModel
import com.example.e_commercetokobangunan_koma.models.ShopProductListModel
import com.example.e_commercetokobangunan_koma.viewmodels.ExploreViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ExploreFragment : Fragment() {

    private var _binding: ExploreFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ExploreViewModel
    private lateinit var adapterProductList: ExploreAdapter

    companion object {
        fun newInstance() = ExploreFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ExploreFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this).get(ExploreViewModel::class.java)
        viewModel.getProductList().observe(viewLifecycleOwner){ products ->
            adapterProductList.setProducts(products)
        }

        //RecyclerView Adapter
        adapterProductList = ExploreAdapter()
        var gridLayoutManager: GridLayoutManager = GridLayoutManager(activity, 2)
        binding.recyclerProductList.layoutManager = gridLayoutManager
        binding.recyclerProductList.adapter = adapterProductList

        getProduct()

    }


    private fun getProduct(){
        var product: ProductForListModel = ProductForListModel("", 0)
        var productList: MutableList<ProductForListModel> = mutableListOf()

        Firebase.firestore.collection("product").limit(10)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    product = ProductForListModel(document.getString("name"), document.getLong("harga"))
                    productList.add(product)
                }
                viewModel.setProductList(productList)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(activity, "Gagal mendapatkan produk", Toast.LENGTH_SHORT).show()
            }
    }


} // End class