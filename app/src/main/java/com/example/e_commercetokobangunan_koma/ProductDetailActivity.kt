package com.example.e_commercetokobangunan_koma

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.e_commercetokobangunan_koma.databinding.ActivityMainBinding
import com.example.e_commercetokobangunan_koma.databinding.ActivityProductDetailBinding
import com.example.e_commercetokobangunan_koma.models.ProductDetailModel
import com.example.e_commercetokobangunan_koma.viewmodels.ProductDetailViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityProductDetailBinding
    private lateinit var viewModel: ProductDetailViewModel
    private lateinit var idProduct: String
    private lateinit var idUser: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Action Bar
        getSupportActionBar()?.setTitle("Detail Product")

        // Initialize Firebase Auth
        auth = Firebase.auth

        //View Model
        viewModel = ProductDetailViewModel()

        val bundle: Bundle? = intent.extras
        idProduct = bundle?.get("idProduct").toString()
        idUser = bundle?.get("idUser").toString()


        binding.shimmerProductDetail.startShimmer()
        getShopInformation(idProduct, idUser)

        viewModel.getProductDetail().observe(this){ productDetail ->
            if(productDetail != null){
                binding.productDetailName.text = productDetail.name.toString()
                binding.productDetailPrice.text = "Rp." + productDetail.price.toString()
                binding.productDetailDescription.text = productDetail.description.toString()
                binding.productDetailWeight.text = productDetail.weight.toString()
                if(productDetail.conditionNew == true){
                    binding.productDetailCondition.text = "Baru"
                } else {
                    binding.productDetailCondition.text = "Bekas"
                }
                binding.productDetail.visibility = View.VISIBLE
                binding.shimmerProductDetail.stopShimmer()
                binding.shimmerProductDetail.visibility = View.GONE
            }
        }

    }// End onCreate

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser == null){
            startActivity(Intent(this, WelcomeActivity::class.java))
        }
    }// End onStart


    fun getShopInformation(idProduct: String, idUser: String){
        var shopPhoto = ""
        var shopName = ""
        var docRef = Firebase.firestore.collection("shop").document(idProduct)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    // Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    shopPhoto = document.data?.get("photo_url").toString()
                    shopName = document.data?.get("name").toString()
                    getProduct(idUser, shopPhoto, shopName)
                } else {
                    // Log.d(TAG, "No such document")
                    getProduct(idUser, shopPhoto, shopName)
                }
            }
            .addOnFailureListener { exception ->
                // Log.d(TAG, "get failed with ", exception)
                getProduct(idUser, shopPhoto, shopName)
            }
    }

    fun getProduct(idUser: String, shopPhoto: String, shopName: String){
        var name = ""
        var price: Long = 0
        var description = ""
        var stock: Long = 0
        var weight: Double = 0.0
        var condition = true
        Firebase.firestore.collection("product")
            .whereEqualTo("id_user", idUser)
            .limit(1)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    name = document.data?.get("name").toString()
                    price = document.data?.get("price") as Long
                    description = document.data?.get("description").toString()
                    stock = document.data?.get("stock") as Long
                    weight = document.data?.get("weight") as Double
                    condition = document.data?.get("newCondition") as Boolean
                }
                viewModel.setProductDetail(ProductDetailModel(name, price, description, stock, weight,
                    condition, shopPhoto, shopName))

            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Gagal mendapatkan data produk", Toast.LENGTH_SHORT).show()
            }
    }

}// End class