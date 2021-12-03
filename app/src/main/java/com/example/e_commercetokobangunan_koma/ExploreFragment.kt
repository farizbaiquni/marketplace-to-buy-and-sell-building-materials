package com.example.e_commercetokobangunan_koma

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.e_commercetokobangunan_koma.adapters.ExploreAdapter
import com.example.e_commercetokobangunan_koma.databinding.ExploreFragmentBinding
import com.example.e_commercetokobangunan_koma.models.ProductListModel
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

        binding.shimmerProductList.startShimmer()

        viewModel = ViewModelProvider(this).get(ExploreViewModel::class.java)
        viewModel.getProductList().observe(viewLifecycleOwner){ products ->
            if(!products.isEmpty()){
                adapterProductList.setProducts(products)
                binding.shimmerProductList.stopShimmer()
                binding.shimmerProductList.visibility = View.GONE
                binding.recyclerProductList.visibility = View.VISIBLE
            }
        }

        //RecyclerView Adapter
        adapterProductList = context?.let { ExploreAdapter(it) }!!
        var gridLayoutManager: GridLayoutManager = GridLayoutManager(activity, 2)
        binding.recyclerProductList.layoutManager = gridLayoutManager
        binding.recyclerProductList.adapter = adapterProductList

        getProduct()

    }


    private fun getProduct(){
        var product: ProductListModel = ProductListModel()
        var productList: MutableList<ProductListModel> = mutableListOf()

        Firebase.firestore.collection("product").limit(10)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    product = ProductListModel(document.id, document.getString("id_user"),
                        document.getString("name"), document.getLong("price"))
                    productList.add(product)
                }
                viewModel.setProductList(productList)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(activity, "Gagal mendapatkan produk", Toast.LENGTH_SHORT).show()
            }
    }


} // End class