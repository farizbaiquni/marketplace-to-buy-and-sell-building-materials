package com.example.e_commercetokobangunan_koma

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.e_commercetokobangunan_koma.adapters.SliderExploreAdapter
import com.example.e_commercetokobangunan_koma.databinding.ActivityMainBinding
import com.example.e_commercetokobangunan_koma.databinding.ActivityProductDetailBinding
import com.example.e_commercetokobangunan_koma.models.ProductDetailModel
import com.example.e_commercetokobangunan_koma.viewmodels.ProductDetailViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.smarteist.autoimageslider.SliderView

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityProductDetailBinding
    private lateinit var viewModel: ProductDetailViewModel
    private lateinit var idProduct: String
    private lateinit var idUser: String
    private lateinit var sliderImagesAdapter: SliderExploreAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Action Bar
        getSupportActionBar()?.setTitle("Detail Produk")

        // Initialize Firebase Auth
        auth = Firebase.auth

        //View Model
        viewModel = ProductDetailViewModel()

        val bundle: Bundle? = intent.extras
        idProduct = bundle?.get("idProduct").toString()
        idUser = bundle?.get("idUser").toString()

        //Slider Adapter
        sliderImagesAdapter = SliderExploreAdapter()
        binding.imagesSliderProductDetail.autoCycleDirection = SliderView.LAYOUT_DIRECTION_LTR
        binding.imagesSliderProductDetail.setSliderAdapter(sliderImagesAdapter)
        binding.imagesSliderProductDetail.setScrollTimeInSec(3)

        //Shimmer
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
                binding.productDetailShopName.text = productDetail.shop_name.toString()

                binding.productDetail.visibility = View.VISIBLE
                binding.shimmerProductDetail.stopShimmer()
                binding.shimmerProductDetail.visibility = View.GONE

            }
        }

        viewModel.getProductPhotosUrl().observe(this){ photosUrl ->
            if(!photosUrl.isNullOrEmpty()){
                sliderImagesAdapter.setPhotosUrl(photosUrl)
            }
        }


    }// End onCreate

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser == null){
            startActivity(Intent(this, WelcomeActivity::class.java))
        } else {
            binding.btnBuy.setOnClickListener(View.OnClickListener {
                if(viewModel.getProductDetail().value != null)
                    startActivity(Intent(this, PaymentActivity::class.java).apply {
                        putExtra("id_product", viewModel.getProductDetail().value?.id_product)
                    })
            })
        }
    }// End onStart


    fun getShopInformation(idProduct: String, idUser: String){
        var shopPhoto = ""
        var shopName = ""
        Firebase.firestore.collection("shop")
            .whereEqualTo("id_user", idUser)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    shopPhoto = document.data?.get("photo_url").toString()
                    shopName = document.data?.get("name").toString()
                    getPhotosUrl(idProduct, shopPhoto, shopName)
                }
            }
            .addOnFailureListener { exception ->
                //Log.d(TAG, "Error getting documents: ", exception)
                getPhotosUrl(idProduct, shopPhoto, shopName)
            }
    }

    fun getPhotosUrl(idProduct: String, shopPhoto: String, shopName: String){
        var photosUrl: MutableList<String> = mutableListOf()
        Firebase.firestore.collection("product").document(idProduct).collection("photos")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    photosUrl.add(document.data?.get("photo_url").toString())
                }
                viewModel.setProductPhotosUrl(photosUrl)
                getProduct(idProduct, shopPhoto, shopName)
            }
            .addOnFailureListener { exception ->
                getProduct(idProduct, shopPhoto, shopName)
            }
    }


    fun getProduct(idProduct: String, shopPhoto: String, shopName: String){
        var id_product = ""
        var name = ""
        var price: Long = 0
        var description = ""
        var stock: Long = 0
        var weight: Double = 0.0
        var condition = true

        var docRef = Firebase.firestore.collection("product").document(idProduct)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    // Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    id_product = document.id
                    name = document.data?.get("name").toString()
                    price = document.data?.get("price") as Long
                    description = document.data?.get("description").toString()
                    stock = document.data?.get("stock") as Long
                    weight = document.data?.get("weight") as Double
                    condition = document.data?.get("newCondition") as Boolean

                    viewModel.setProductDetail(ProductDetailModel(id_product, name, price, description, stock, weight,
                        condition, shopPhoto, shopName))

                } else {
                    // Log.d(TAG, "No such document")
                    binding.shimmerProductDetail.stopShimmer()
                    binding.productDetail.visibility = View.GONE
                    Toast.makeText(this, "Produk tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                // Log.d(TAG, "get failed with ", exception)
                binding.shimmerProductDetail.stopShimmer()
                binding.productDetail.visibility = View.GONE
                Toast.makeText(this, "Gagal mendapatkan data produk", Toast.LENGTH_SHORT).show()
            }
    }


}// End class