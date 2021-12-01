package com.example.e_commercetokobangunan_koma

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.e_commercetokobangunan_koma.adapters.AddProductAdapter
import com.example.e_commercetokobangunan_koma.adapters.ShopProductListAdapter
import com.example.e_commercetokobangunan_koma.databinding.ActivityShopProductListBinding
import com.example.e_commercetokobangunan_koma.models.ShopProductListModel
import com.example.e_commercetokobangunan_koma.viewmodels.AddProductViewModel
import com.example.e_commercetokobangunan_koma.viewmodels.ShopProductListViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ShopProductListActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityShopProductListBinding
    private lateinit var adapterProductList: ShopProductListAdapter
    private lateinit var model: ShopProductListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopProductListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Initialize Firebase Auth
        auth = Firebase.auth

        //RecyclerView Adapter
        adapterProductList = ShopProductListAdapter(this)

        // ViewModel
        model = ViewModelProvider(this).get(ShopProductListViewModel::class.java)
        model.getProductShop().observe(this) { products ->
            if (products != null) {
                adapterProductList.setProducts(products)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser == null){
            startActivity(Intent(this, WelcomeActivity::class.java))
        } else {
            Firebase.firestore.collection("shop")
                .whereEqualTo("id_user", currentUser.uid)
                .get()
                .addOnSuccessListener { documents ->
                    if(documents.isEmpty){
                        startActivity(Intent(this, AddProfileShopActivity::class.java))
                    }else{
                        //RecyclerView Adapter
                        var linearLayoutManager: LinearLayoutManager = LinearLayoutManager(this)
                        binding.recyclerViewShopProductList.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
                        binding.recyclerViewShopProductList.layoutManager = linearLayoutManager
                        binding.recyclerViewShopProductList.adapter = adapterProductList

                        getProductShopList()
                    }
                }
                .addOnFailureListener { exception ->
                    //Log.w("TAG", "Error getting documents: ", exception)
                }

        }
    }

    private fun getProductShopList(){
        var product: ShopProductListModel = ShopProductListModel("", 0, 0, false)
        var productList: MutableList<ShopProductListModel> = mutableListOf()

        Firebase.firestore.collection("product").limit(10)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    product = ShopProductListModel(document.data.get("name").toString(),
                        0, document.getLong("jumlahStok"), true)
                    productList.add(product)
                }
                model.setProductShop(productList)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Gagal mendapatkan produk", Toast.LENGTH_SHORT).show()
            }

    }

}// End class