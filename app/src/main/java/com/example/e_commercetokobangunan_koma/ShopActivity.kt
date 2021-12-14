package com.example.e_commercetokobangunan_koma

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.e_commercetokobangunan_koma.databinding.ActivityShopBinding
import com.example.e_commercetokobangunan_koma.models.ReviewShopModel
import com.example.e_commercetokobangunan_koma.models.SimpleShopProfileModel
import com.example.e_commercetokobangunan_koma.viewmodels.ShopViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class ShopActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityShopBinding
    private lateinit var viewModel: ShopViewModel
    private lateinit var idShop: String
    private lateinit var shopProfile: SimpleShopProfileModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.title = "Profil Toko"

        val bundle: Bundle? = intent.extras
        idShop = bundle?.get("idShop").toString()

        // Initialize Firebase Auth
        auth = Firebase.auth

        //View Model
        viewModel = ShopViewModel()
        viewModel.getReviewShop().observe(this){ reviewShop ->
            if(!reviewShop.isNullOrEmpty()){
                for(review in reviewShop){
                    when(review.key){
                        "kualitas_pengemasan" -> binding.badgeKualitasPengemasan.visibility = View.VISIBLE
                        "deskripsi_foto" -> binding.badgeDeskripsiFoto.visibility = View.VISIBLE
                        "fast_respone" -> binding.badgeFastRespone.visibility = View.VISIBLE
                        "keramahan" -> binding.badgeKeramahan.visibility = View.VISIBLE
                    }
                }
            }
        }

    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser == null){
            startActivity(Intent(this, WelcomeActivity::class.java))
        }else{
            getShopInformation(idShop)
        }
    }

    fun getShopInformation(idShop: String){
        var alamat = ""
        val docRef = Firebase.firestore.collection("shop").document(idShop)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    try {
                        Picasso.get().load(document.data?.get("photo_url").toString()).into(binding.shopProfilePhoto);
                    }catch (e: Exception){ }
                    binding.shopProfileName.text = document.data?.get("nama").toString()
                    alamat = document.data?.get("kabupaten_kota").toString() + ", " + document.data?.get("provinsi").toString()
                    binding.shopProfileAlamat.text = alamat

                    shopProfile = SimpleShopProfileModel(
                        document.id,
                        document.data?.get("photo_url").toString(),
                        document.data?.get("nama").toString(),
                        document.data?.get("provinsi").toString(),
                    )

                    getShopReview(idShop)

                } else {
                    // Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception -> }
    }

    fun getShopReview(idShop: String){
        var reviewShop: MutableMap<String, ReviewShopModel> = mutableMapOf()
        var docsRef = Firebase.firestore.collection("review_shop").whereEqualTo("id_shop", idShop)
            docsRef.whereGreaterThan("result", 4)
            docsRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    reviewShop.put(
                        document.data["category"].toString(),
                        ReviewShopModel(
                            document.data["id_review_shop"].toString(),
                            document.data["id_shop"].toString(),
                            document.data["shop_photo_url"].toString(),
                            document.data["shop_name"].toString(),
                            document.data["provinsi"].toString(),
                            document.data["category"].toString(),
                            document.data["result"].toString().toDouble(),
                            document.data["total_reviewer"].toString().toDouble().toInt(),
                        )
                    )
                }

                viewModel.setReviewShop(reviewShop)
            }
            .addOnFailureListener { exception ->
                // Log.w(TAG, "Error getting documents: ", exception)
            }
    }
}