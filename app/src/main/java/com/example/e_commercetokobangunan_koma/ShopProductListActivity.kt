package com.example.e_commercetokobangunan_koma

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.e_commercetokobangunan_koma.adapters.ShopProductListAdapter
import com.example.e_commercetokobangunan_koma.databinding.ActivityShopProductListBinding
import com.example.e_commercetokobangunan_koma.models.ShopProductListModel
import com.example.e_commercetokobangunan_koma.viewmodels.ShopProductListViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ShopProductListActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityShopProductListBinding
    private lateinit var adapterProductList: ShopProductListAdapter
    private lateinit var viewModel: ShopProductListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopProductListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Action Bar Name
        supportActionBar?.title = ""

        // Initialize Firebase Auth
        auth = Firebase.auth

        //RecyclerView Adapter
        adapterProductList = ShopProductListAdapter(this)

        // ViewModel
        viewModel = ViewModelProvider(this).get(ShopProductListViewModel::class.java)
        viewModel.getProductShop().observe(this) { products ->
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
                        supportActionBar?.title = "Produk Toko"
                        //RecyclerView Adapter
                        var gridLayoutManager: GridLayoutManager = GridLayoutManager(this, 2)
                        binding.recyclerViewShopProductList.layoutManager = gridLayoutManager
                        binding.recyclerViewShopProductList.adapter = adapterProductList

                        getProductShopList(currentUser.uid)
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Gagal mendapatkan data", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.option_menu_seller, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_add_product -> {
                startActivity(Intent(this, AddProductActivity::class.java))
                true
            }
            R.id.menu_seller_profile_toko -> {
                // startActivity(Intent(this, AddProductActivity::class.java))
                Toast.makeText(this, "Profile Toko", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.menu_seller_exit_toko -> {
                startActivity(Intent(this, MainActivity::class.java))
                true
            }
            R.id.menu_seller_chat_list -> {
                startActivity(Intent(this, ChatListActivity::class.java).apply {
                    putExtra("isBuyer", false)
                })
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun getProductShopList(idUser: String){
        var productList: MutableList<ShopProductListModel> = mutableListOf()

        Firebase.firestore.collection("product").whereEqualTo("id_user", idUser) .limit(10)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    productList.add(ShopProductListModel(
                        document.id,
                        document.data.get("default_photo").toString(),
                        document.data.get("name").toString(),
                        0,
                        document.getLong("stock"),
                        true,
                        document.data.get("photos_name") as MutableList<String>?,
                    ))
                }
                viewModel.setProductShop(productList)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Gagal mendapatkan produk", Toast.LENGTH_SHORT).show()
            }
    }

}// End class