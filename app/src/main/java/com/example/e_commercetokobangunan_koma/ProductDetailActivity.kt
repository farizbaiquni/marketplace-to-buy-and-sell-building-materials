package com.example.e_commercetokobangunan_koma

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.e_commercetokobangunan_koma.adapters.SliderExploreAdapter
import com.example.e_commercetokobangunan_koma.databinding.ActivityProductDetailBinding
import com.example.e_commercetokobangunan_koma.models.ProductDetailModel
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

                productDetail.photos?.let { sliderImagesAdapter.setPhotosUrl(it) }

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

        viewModel.getIdAndPhotoShop().observe(this){ list ->
            if(!list.isNullOrEmpty()){

                binding.btnChat.setOnClickListener(View.OnClickListener {
                    startActivity(Intent(this, ChatActivity::class.java).apply {
                        putExtra("idShop", list[0])
                        putExtra("photoShop", list[1])
                        putExtra("nameShop", list[2])
                        putExtra("isBuyer", false)
                    })
                })

                binding.btnReview.setOnClickListener(View.OnClickListener {
                    if(auth.currentUser != null){
                        val modalBottomSheet = TypeReviewBottomSheetFragment(list[0])
                        modalBottomSheet.show(supportFragmentManager, TypeReviewBottomSheetFragment.TAG)
                    }else{
                        Toast.makeText(this, "Harus Login Terlebih Dahulu", Toast.LENGTH_SHORT).show()
                    }
                })

                binding.productDetailShopLayout.setOnClickListener(View.OnClickListener {
                    startActivity(Intent(this, ShopActivity::class.java).apply {
                        putExtra("idShop", list[0])
                    })
                })

                binding.btnBuy.setOnClickListener(View.OnClickListener {
                    if(auth.currentUser != null){
                        if(viewModel.getProductDetail().value != null) {
                            startActivity(Intent(this, PaymentActivity::class.java).apply {
                                putExtra("idProduct", viewModel.getProductDetail().value?.id_product)
                                putExtra("idShop", list[0])
                            })
                        }
                    }else{
                        Toast.makeText(this, "Harus Login Terlebih Dahulu", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

        getShopInformation(idProduct, idUser)

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
                    viewModel.setIdAndPhotoShop(mutableListOf(document.id, document.data.get("photo_url").toString(), document.data.get("nama").toString() ))
                    getProduct(idProduct, document.data.get("photo_url").toString(), document.data.get("nama").toString())
                }
            }
            .addOnFailureListener { exception ->
                //Log.d(TAG, "Error getting documents: ", exception)
            }
    }



    fun getProduct(idProduct: String, shopPhoto: String, shopName: String){
        var id_product: String? = ""
        var name: String? = ""
        var price: Long? = 0
        var description: String? = ""
        var stock: Long? = 0
        var weight: Double? = 0.0
        var condition: Boolean? = true
        var photos: MutableList<String>? = mutableListOf()

        var docRef = Firebase.firestore.collection("product").document(idProduct)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    id_product = document.id
                    name = document.data?.get("name").toString()
                    price = document.data?.get("price")?.let { it as Long }
                    description = document.data?.get("description").toString()
                    stock = document.data?.get("stock")?.let { it as Long }
                    weight = document.data?.get("weight")?.let { it as Double }
                    condition = document.data?.get("new_condition")?.let { it as Boolean }
                    photos = document.data?.get("photos")?.let { it as MutableList<String> }

                    viewModel.setProductDetail(ProductDetailModel(id_product, photos, name,
                        price, description, stock, weight, condition, shopPhoto, shopName))

                } else {
                    binding.shimmerProductDetail.stopShimmer()
                    binding.productDetail.visibility = View.GONE
                    binding.standardBottomSheet.visibility = View.GONE
                    Toast.makeText(this, "Produk tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                binding.shimmerProductDetail.stopShimmer()
                binding.productDetail.visibility = View.GONE
                binding.standardBottomSheet.visibility = View.GONE
                Toast.makeText(this, "Gagal mendapatkan data produk", Toast.LENGTH_SHORT).show()
            }
    }


}// End class