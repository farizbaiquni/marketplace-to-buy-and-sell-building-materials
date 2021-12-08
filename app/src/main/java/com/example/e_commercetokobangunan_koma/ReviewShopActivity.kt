package com.example.e_commercetokobangunan_koma

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.e_commercetokobangunan_koma.databinding.ActivityReviewShopBinding
import com.example.e_commercetokobangunan_koma.models.ReviewShopModel
import com.example.e_commercetokobangunan_koma.models.ReviewShopUserModel
import com.example.e_commercetokobangunan_koma.viewmodels.ReviewShopViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ReviewShopActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityReviewShopBinding
    private lateinit var viewModel: ReviewShopViewModel
    private lateinit var idShop: String
    private var db = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewShopBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.title = "Review Toko"

        // Initialize Firebase Auth
        auth = Firebase.auth

        val bundle: Bundle? = intent.extras
        idShop = bundle?.get("idShop").toString()

        viewModel = ViewModelProvider(this).get(ReviewShopViewModel::class.java)

        viewModel.getPermittedReview().observe(this){ permittedReview ->
            if(!permittedReview.isEmpty()){
                checkHistoryReview(idShop, auth.currentUser!!.uid)
                binding.btnSubmit.setOnClickListener(View.OnClickListener {
                    submitReview(idShop, auth.currentUser!!.uid)
                })
            }
        }

    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser == null){
            startActivity(Intent(this, WelcomeActivity::class.java))
        }else{
            checkPermittedReview(currentUser.uid)
        }
    }

    private fun checkPermittedReview(idUser: String){
        var permittedReview: MutableList<String> = mutableListOf()

        //Check permitted for history transaction
        db.collection("transaction")
            .whereEqualTo("id_buyer", idUser)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                if(!result.isEmpty){
                    permittedReview.add("permitted_kualitas_pengemasan")
                    permittedReview.add("permitted_deskripsi_foto")
                    permittedReview.add("permitted_fast_respone")
                    permittedReview.add("permitted_keramahan")
                    viewModel.setPermittedReview(permittedReview)
                }
            }
            .addOnFailureListener { exception ->
                // Log.d(TAG, "Error getting documents: ", exception)
            }
    }


    private fun checkHistoryReview(idShop: String, idUser: String){
        var reviewShopUser: MutableList<ReviewShopUserModel> = mutableListOf()
        var historyReview: MutableList<String> = mutableListOf()

        db.collection("review_shop")
            .whereEqualTo("id_shop", idShop)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    viewModel.setReviewShop(
                        ReviewShopModel(
                            document.data["id_shop"].toString(),
                            document.data["category"].toString(),
                            document.data["result"].toString(),
                            document.data["total_reviewer"].toString(),
                        )
                    )
                }

                db.collection("review_shop_user")
                    .whereEqualTo("id_shop", idShop)
                    .whereEqualTo("id_reviewer", idUser)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            reviewShopUser.add( ReviewShopUserModel(
                                document.id,
                                document.data.get("id_shop").toString(),
                                document.data.get("id_reviewer").toString(),
                                document.data.get("category").toString(),
                                document.data.get("value").toString().toDouble(),
                            ))
                            historyReview.add(document.data.get("category").toString())
                        }
                        viewModel.setHistoryReviewShopUser(reviewShopUser)
                        viewModel.setHistoryReview(historyReview)

                    }.addOnFailureListener { exception ->

                    }

            }.addOnFailureListener { exception ->

            }

    }


    fun submitReview(idShop: String, idUser: String){
        val ratingBarDeskripsiFoto: Double =  binding.ratingBarDeskripsiFoto.rating.toDouble()
        val ratingBarPengemasan: Double = binding.ratingBarPengemasan.rating.toDouble()
        val ratingBarFastRespone: Double = binding.ratingBarFastRespone.rating.toDouble()
        val ratingBarKeramahan: Double = binding.ratingBarKeramahan.rating.toDouble()

        var reviewShopUser = db.collection("review_shop_user").document()
        var reviewShop = db.collection("review_shop_user").document()


        db.runBatch { batch ->

            // =============== UPDATE HISTORY REVIEW ===============
            if(viewModel.getHistoryReviewShopUser().value.isNullOrEmpty() == false){
                for(item in viewModel.getHistoryReviewShopUser().value!!){

                    //KATEGORI DESKRIPSI-FOTO
                    if (item.category.toString().equals("deskripsi_foto") && item.value!! != ratingBarDeskripsiFoto){
                        reviewShopUser = db.collection("review_shop_user").document(item.id_shop_review_user.toString())
                        batch.update(reviewShopUser, "value", ratingBarDeskripsiFoto)
                    }
                    //KATEGORI FAST-RESPONE
                    if (item.category.toString().equals("fast_respone") && item.value!! != ratingBarFastRespone){
                        reviewShopUser = db.collection("review_shop_user").document(item.id_shop_review_user.toString())
                        batch.update(reviewShopUser,"value", ratingBarFastRespone)
                    }

                    //KATEGORI KERAMAHAN
                    if (item.category.toString().equals("keramahan") && item.value!! != ratingBarKeramahan){
                        reviewShopUser = db.collection("review_shop_user").document(item.id_shop_review_user.toString())
                        batch.update(reviewShopUser,"value", ratingBarKeramahan)
                    }

                    //KATEGORI KERAMAHAN
                    if (item.category.toString().equals("kualitas_pengemasan") && item.value!! != ratingBarPengemasan){
                        reviewShopUser = db.collection("review_shop_user").document(item.id_shop_review_user.toString())
                        batch.update(reviewShopUser,"value", ratingBarPengemasan)
                    }
                }
            }

            // =============== ADD A NEW REVIEW ===============
            //KATEGORI DESKRIPSI-FOTO
            if (viewModel.getPermittedReview().value!!.contains("permitted_deskripsi_foto").equals(true)
                && ("deskripsi_foto" in viewModel.getHistoryReview().value!!).equals(false)){
                reviewShopUser = db.collection("review_shop_user").document()
                batch.set(reviewShopUser, ReviewShopUserModel(reviewShopUser.id, idShop, idUser, "deskripsi_foto", ratingBarDeskripsiFoto))
            }

            //KATEGORI FAST-RESPONE
            if (viewModel.getPermittedReview().value!!.contains("permitted_fast_respone").equals(true)
                && ("fast_respone" in viewModel.getHistoryReview().value!!).equals(false)){
                reviewShopUser = db.collection("review_shop_user").document()
                batch.set(reviewShopUser, ReviewShopUserModel(reviewShopUser.id, idShop, idUser, "fast_respone", ratingBarFastRespone))
            }

            //KATEGORI KERAMAHAN
            if (viewModel.getPermittedReview().value!!.contains("permitted_keramahan").equals(true)
                && ("keramahan" in viewModel.getHistoryReview().value!!).equals(false)){
                reviewShopUser = db.collection("review_shop_user").document()
                batch.set(reviewShopUser, ReviewShopUserModel(reviewShopUser.id, idShop, idUser, "keramahan", ratingBarKeramahan))
            }

            //KATEGORI KUALITAS-PENGEMASAN
            if (viewModel.getPermittedReview().value!!.contains("permitted_kualitas_pengemasan").equals(true)
                && ("kualitas_pengemasan" in viewModel.getHistoryReview().value!!).equals(false)){
                reviewShopUser = db.collection("review_shop_user").document()
                batch.set(reviewShopUser, ReviewShopUserModel(reviewShopUser.id, idShop, idUser, "kualitas_pengemasan", ratingBarKeramahan))
            }

        }.addOnSuccessListener {

        }.addOnFailureListener {

        }

    }
} // End class