package com.example.e_commercetokobangunan_koma

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.e_commercetokobangunan_koma.adapters.SliderExploreAdapter
import com.example.e_commercetokobangunan_koma.databinding.ActivityProductDetailBinding
import com.example.e_commercetokobangunan_koma.models.ProductDetailModel
import com.example.e_commercetokobangunan_koma.models.SimpleShopProfileModel
import com.example.e_commercetokobangunan_koma.viewmodels.ProductDetailViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.smarteist.autoimageslider.SliderView
import com.squareup.picasso.Picasso
import java.lang.Exception
import java.text.NumberFormat
import java.util.*

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

        viewModel.getProductDetail().observe(this){ productDetail ->
            if(productDetail != null){

                val localeID = Locale("in", "ID")
                val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeID)
                formatRupiah.maximumFractionDigits = 0

                binding.productDetailName.text = productDetail.name.toString()
                binding.productDetailPrice.text = formatRupiah.format(productDetail.price.toString().toInt())
                binding.productDetailDescription.text = productDetail.description.toString()
                binding.productDetailWeight.text = productDetail.weight.toString()
                if(productDetail.condition_new == true){
                    binding.productDetailCondition.text = "Baru"
                } else {
                    binding.productDetailCondition.text = "Bekas"
                }
                binding.productDetailShopName.text = productDetail.shop_name.toString()

                binding.productDetail.visibility = View.VISIBLE
                binding.standardBottomSheet.visibility = View.VISIBLE
                binding.shimmerProductDetail.stopShimmer()
                binding.shimmerProductDetail.visibility = View.GONE

            }
        }

        viewModel.getProductPhotosUrl().observe(this){ photosUrl ->
            if(!photosUrl.isNullOrEmpty()){
                sliderImagesAdapter.setPhotosUrl(photosUrl)
            }
        }


        viewModel.getIdShop().observe(this){ idShop ->
            if(!idShop.isNullOrEmpty()){
                binding.btnReview.setOnClickListener(View.OnClickListener {
                    if(auth.currentUser != null){
                        val modalBottomSheet = TypeReviewBottomSheetFragment(idShop)
                        modalBottomSheet.show(supportFragmentManager, TypeReviewBottomSheetFragment.TAG)
                    }else{
                        Toast.makeText(this, "Harus Login Terlebih Dahulu", Toast.LENGTH_SHORT).show()
                    }
                })

                binding.productDetailShopLayout.setOnClickListener(View.OnClickListener {
                    startActivity(Intent(this, ShopActivity::class.java).apply {
                        putExtra("idShop", idShop)
                    })
                })
            }
        }


        getShopInformation(idProduct, idUser)
        binding.btnBuy.setOnClickListener(View.OnClickListener {
            if(auth.currentUser != null){
                if(viewModel.getProductDetail().value != null) {
                    startActivity(Intent(this, PaymentActivity::class.java).apply {
                        putExtra("id_product", viewModel.getProductDetail().value?.id_product)
                    })
                }
            }else{
                Toast.makeText(this, "Harus Login Terlebih Dahulu", Toast.LENGTH_SHORT).show()
            }

        })


    }// End onCreate


    fun getShopInformation(idProduct: String, idUser: String){
        Firebase.firestore.collection("shop")
            .whereEqualTo("id_user", idUser)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    try {
                        Picasso.get().load(document.data.get("photo_url").toString()).into(binding.productDetailShopPhoto);
                    }catch (e: Exception){ }
                    binding.productDetailShopName.text = document.data.get("nama").toString()
                    binding.productDetailShopProvinsi.text = document.data.get("provinsi").toString()

                    getPhotosUrl(idProduct, document.data?.get("photo_url").toString(), document.data?.get("nama").toString())
                    viewModel.setIdShop(document.id)
                }
            }
            .addOnFailureListener { exception ->
                //Log.d(TAG, "Error getting documents: ", exception)
                getPhotosUrl(idProduct, "", "")
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
                    condition = document.data?.get("new_condition") as Boolean

                    viewModel.setProductDetail(ProductDetailModel(id_product, name, price, description, stock, weight,
                        condition, shopPhoto, shopName))

                } else {
                    // Log.d(TAG, "No such document")
                    binding.shimmerProductDetail.stopShimmer()
                    binding.productDetail.visibility = View.GONE
                    binding.standardBottomSheet.visibility = View.GONE
                    Toast.makeText(this, "Produk tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                // Log.d(TAG, "get failed with ", exception)
                binding.shimmerProductDetail.stopShimmer()
                binding.productDetail.visibility = View.GONE
                binding.standardBottomSheet.visibility = View.GONE
                Toast.makeText(this, "Gagal mendapatkan data produk", Toast.LENGTH_SHORT).show()
            }
    }


}// End class