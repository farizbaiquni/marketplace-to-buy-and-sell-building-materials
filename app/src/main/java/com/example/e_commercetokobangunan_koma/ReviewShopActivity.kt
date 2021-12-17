package com.example.e_commercetokobangunan_koma

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.e_commercetokobangunan_koma.databinding.ActivityReviewShopBinding
import com.example.e_commercetokobangunan_koma.models.ReviewShopModel
import com.example.e_commercetokobangunan_koma.models.ReviewShopUserModel
import com.example.e_commercetokobangunan_koma.models.SimpleShopProfileModel
import com.example.e_commercetokobangunan_koma.viewmodels.ReviewShopViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.lang.Exception

class ReviewShopActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityReviewShopBinding
    private lateinit var viewModel: ReviewShopViewModel
    private lateinit var builderLoadingDialog: AlertDialog.Builder
    private lateinit var loadingDialog: AlertDialog
    private lateinit var idShop: String

    private var db = Firebase.firestore

    var reviewShopDeskripsiFotoRef = db.collection("review_shop").document()
    var reviewShopFastResponeRef = db.collection("review_shop").document()
    var reviewShopKeramahanRef = db.collection("review_shop").document()
    var reviewShopKualitasPengemasanRef = db.collection("review_shop").document()

    var reviewShopUserDeskripsiFotoRef = db.collection("review_shop_user").document()
    var reviewShopUserFastResponeRef = db.collection("review_shop_user").document()
    var reviewShopUserKeramahanRef = db.collection("review_shop_user").document()
    var reviewShopUserKualitasPengemasanRef = db.collection("review_shop_user").document()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewShopBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.title = "Review Toko"

        // Initialize Firebase Auth
        auth = Firebase.auth

        //Shimmer
        binding.shimmerReviewShop.startShimmer()

        //Get data from previous activity
        val bundle: Bundle? = intent.extras
        idShop = bundle?.get("idShop").toString()

        //Laoding Dialog
        builderLoadingDialog = AlertDialog.Builder(this)
        var inflater: LayoutInflater = layoutInflater
        builderLoadingDialog.setView(inflater.inflate(R.layout.loading_dialog, null))
        builderLoadingDialog.setCancelable(false)
        loadingDialog = builderLoadingDialog.create()

        //ViewModel
        viewModel = ViewModelProvider(this).get(ReviewShopViewModel::class.java)
        viewModel.getPermittedReview().observe(this){ permittedReview ->
            if(!permittedReview.isEmpty()){
                for(permitted in permittedReview){
                    when(permitted){
                        "permitted_deskripsi_foto" -> binding.btnDisableReviewDeskripsiFoto.visibility = View.GONE
                        "permitted_fast_respone" -> binding.btnDisableReviewFastRespone.visibility = View.GONE
                        "permitted_keramahan" -> binding.btnDisableReviewKeramahan.visibility = View.GONE
                        "permitted_kualitas_pengemasan" -> binding.btnDisableReviewKualitasPengemasan.visibility = View.GONE
                    }
                }
                checkHistoryReview(idShop, auth.currentUser!!.uid)
                binding.btnSubmit.setOnClickListener(View.OnClickListener {
                    submitReview(idShop, auth.currentUser!!.uid)
                })
            }
        }
        viewModel.getHistoryReviewShopUser().observe(this){ reviewShopUser ->
            if(!reviewShopUser.isNullOrEmpty()){
                for(review in reviewShopUser){
                    when(review.key){
                        "deskripsi_foto" -> binding.ratingBarDeskripsiFoto.rating = review.value.value!!.toFloat()
                        "fast_respone" -> binding.ratingBarFastRespone.rating = review.value.value!!.toFloat()
                        "keramahan" -> binding.ratingBarKeramahan.rating = review.value.value!!.toFloat()
                        "kualitas_pengemasan" -> binding.ratingBarPengemasan.rating = review.value.value!!.toFloat()
                    }
                }
                binding.reviewShopLayout.visibility = View.VISIBLE
                binding.shimmerReviewShop.stopShimmer()
                binding.shimmerReviewShop.visibility = View.GONE
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
            getShopInformation(currentUser.uid, idShop)
        }
    }


    fun getShopInformation(idUser: String, idShop: String){
        var shopProfile: SimpleShopProfileModel? = null
        var alamat = "Kabupaten/Kota, Provinsi"
        val docRef = db.collection("shop").document(idShop)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    try {
                        Picasso.get().load(document.data?.get("photo_url").toString()).into(binding.shopPhotoUrl);
                    }catch (e: Exception){ }
                    binding.textViewShopName.text = document.data?.get("nama").toString()
                    alamat = document.data?.get("kabupaten_kota").toString() +
                            ", " +
                            document.data?.get("provinsi").toString()
                    binding.textViewShopAlamat.text = alamat

                    shopProfile = SimpleShopProfileModel(
                        document.id,
                        document.data?.get("photo_url").toString(),
                        document.data?.get("nama").toString(),
                        document.data?.get("provinsi").toString(),
                    )
                }else{
                    Toast.makeText(this, "TIDAK ADA DATA", Toast.LENGTH_SHORT).show()
                }

                shopProfile?.let { viewModel.setSimpleShopProfile(it) }
                checkPermittedReview(idUser, idShop)

            }
            .addOnFailureListener { exception ->
                binding.reviewShopLayout.visibility = View.VISIBLE
                binding.shimmerReviewShop.stopShimmer()
                binding.shimmerReviewShop.visibility = View.GONE
                Toast.makeText(this, "Gagal mendapatkan informasi", Toast.LENGTH_SHORT).show()
            }
    }


    private fun checkPermittedReview(idUser: String, idShop: String){
        var permittedReview: MutableList<String> = mutableListOf()

        //Check permitted for history transaction
        var ref = db.collection("transaction").whereEqualTo("id_buyer", idUser)
            ref = ref.whereEqualTo("id_shop", idShop)
            ref.limit(1).get().addOnSuccessListener { result ->
                if(!result.isEmpty){
                    permittedReview.add("permitted_kualitas_pengemasan")
                    permittedReview.add("permitted_deskripsi_foto")
                    permittedReview.add("permitted_fast_respone")
                    permittedReview.add("permitted_keramahan")
                    viewModel.setPermittedReview(permittedReview)
                }else{
                    permittedReview.add("permitted_fast_respone")
                    permittedReview.add("permitted_keramahan")
                    viewModel.setPermittedReview(permittedReview)
                }
            }
            .addOnFailureListener { exception ->

                binding.reviewShopLayout.visibility = View.VISIBLE
                binding.shimmerReviewShop.stopShimmer()
                binding.shimmerReviewShop.visibility = View.GONE
                Toast.makeText(this, "Gagal mendapatkan data", Toast.LENGTH_SHORT).show()
            }
    }


    private fun checkHistoryReview(idShop: String, idUser: String){
        var reviewShopUser: MutableMap<String, ReviewShopUserModel> = mutableMapOf()

        db.collection("review_shop_user")
            .whereEqualTo("id_shop", idShop)
            .whereEqualTo("id_reviewer", idUser)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    reviewShopUser.put(document.data.get("category").toString(), ReviewShopUserModel(
                        document.id,
                        document.data.get("id_shop").toString(),
                        document.data.get("id_reviewer").toString(),
                        document.data.get("category").toString(),
                        document.data.get("value").toString().toDouble(),
                    ))

                    when(document.data.get("category").toString()){
                        "deskripsi_foto" -> reviewShopUserDeskripsiFotoRef =  db.collection("review_shop_user").document(document.id)
                        "fast_respone" -> reviewShopUserFastResponeRef =  db.collection("review_shop_user").document(document.id)
                        "keramahan" -> reviewShopUserKeramahanRef =  db.collection("review_shop_user").document(document.id)
                        "kualitas_pengemasan" -> reviewShopUserKualitasPengemasanRef =  db.collection("review_shop_user").document(document.id)
                    }

                }
                viewModel.setHistoryReviewShopUser(reviewShopUser)
                binding.btnSubmit.isEnabled = true

                binding.reviewShopLayout.visibility = View.VISIBLE
                binding.shimmerReviewShop.stopShimmer()
                binding.shimmerReviewShop.visibility = View.GONE

            }.addOnFailureListener { exception ->
                binding.reviewShopLayout.visibility = View.VISIBLE
                binding.shimmerReviewShop.stopShimmer()
                binding.shimmerReviewShop.visibility = View.GONE

            }

    }


    fun submitReview(idShop: String, idUser: String){
        loadingDialog.show()

        val ratingBarDeskripsiFoto: Double =  binding.ratingBarDeskripsiFoto.rating.toDouble()
        val ratingBarPengemasan: Double = binding.ratingBarPengemasan.rating.toDouble()
        val ratingBarFastRespone: Double = binding.ratingBarFastRespone.rating.toDouble()
        val ratingBarKeramahan: Double = binding.ratingBarKeramahan.rating.toDouble()


        var reviewShop: MutableMap<String, ReviewShopModel> = mutableMapOf()

        db.collection("review_shop")
            .whereEqualTo("id_shop", idShop)
            .get()
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
                    when(document.data["category"].toString()){
                        "deskripsi_foto" -> reviewShopDeskripsiFotoRef =  db.collection("review_shop").document(document.id)
                        "fast_respone" -> reviewShopFastResponeRef =  db.collection("review_shop").document(document.id)
                        "keramahan" -> reviewShopKeramahanRef =  db.collection("review_shop").document(document.id)
                        "kualitas_pengemasan" -> reviewShopKualitasPengemasanRef =  db.collection("review_shop").document(document.id)
                    }
                }


                db.runBatch { batch ->
                    // ------------- UPDATE PREVIOUS REVIEW -------------
                    if(viewModel.getHistoryReviewShopUser().value.isNullOrEmpty() == false){
                        for(item in viewModel.getHistoryReviewShopUser().value!!){

                            // KATEGORI DESKRIPSI-FOTO
                            if (item.key.equals("deskripsi_foto") && item.value.value != ratingBarDeskripsiFoto && reviewShop.keys.contains("deskripsi_foto")){

                                Log.d("UPDATE IS CALLED ?", "1")
                                var newRating: Double = ((( reviewShop.get("deskripsi_foto")?.result.toString().toDouble() *
                                        reviewShop.get("deskripsi_foto")?.total_reviewer.toString().toDouble() ) -
                                        item.value.value!!.toDouble()) + ratingBarDeskripsiFoto) /
                                        reviewShop.get("deskripsi_foto")?.total_reviewer.toString().toDouble()

                                batch.update(reviewShopUserDeskripsiFotoRef, "value", ratingBarDeskripsiFoto)
                                batch.update(reviewShopDeskripsiFotoRef, "result", newRating)
                            }


                            // KATEGORI FAST-RESPONE
                            if (item.key.equals("fast_respone") && item.value.value != ratingBarFastRespone && reviewShop.keys.contains("fast_respone")){

                                Log.d("UPDATE IS CALLED ?", "2")
                                var newRating: Double = ((( reviewShop.get("fast_respone")?.result.toString().toDouble() *
                                        reviewShop.get("fast_respone")?.total_reviewer.toString().toDouble() ) -
                                        item.value.value!!.toDouble()) + ratingBarFastRespone) /
                                        reviewShop.get("fast_respone")?.total_reviewer.toString().toDouble()

                                batch.update(reviewShopUserFastResponeRef, "value", ratingBarFastRespone)
                                batch.update(reviewShopFastResponeRef, "result", newRating)
                            }


                            // KATEGORI KERAMAHAN
                            if (item.key.equals("keramahan") && item.value.value != ratingBarKeramahan && reviewShop.keys.contains("keramahan")){

                                Log.d("UPDATE IS CALLED ?", "3")
                                var newRating: Double = ((( reviewShop.get("keramahan")?.result.toString().toDouble() *
                                        reviewShop.get("keramahan")?.total_reviewer.toString().toDouble() ) -
                                        item.value.value!!.toDouble()) + ratingBarKeramahan) /
                                        reviewShop.get("keramahan")?.total_reviewer.toString().toDouble()

                                batch.update(reviewShopUserKeramahanRef, "value", ratingBarKeramahan)
                                batch.update(reviewShopKeramahanRef, "result", newRating)
                            }


                            // KATEGORI KERAMAHAN
                            if (item.key.equals("kualitas_pengemasan") && item.value.value != ratingBarPengemasan && reviewShop.keys.contains("kualitas_pengemasan")){

                                Log.d("UPDATE IS CALLED ?", "4")
                                var newRating: Double = ((( reviewShop.get("kualitas_pengemasan")?.result.toString().toDouble() *
                                        reviewShop.get("kualitas_pengemasan")?.total_reviewer.toString().toDouble() ) -
                                        item.value.value!!.toDouble()) + ratingBarPengemasan) /
                                        reviewShop.get("kualitas_pengemasan")?.total_reviewer.toString().toDouble()

                                batch.update(reviewShopUserKualitasPengemasanRef, "value", ratingBarPengemasan)
                                batch.update(reviewShopKualitasPengemasanRef, "result", newRating)
                            }

                        }// End for
                    }// End if


                    // ------------- ADD A NEW REVIEW -------------

                    // KATEGORI DESKRIPSI-FOTO
                    if (viewModel.getPermittedReview().value!!.contains("permitted_deskripsi_foto").equals(true)){
                        if((viewModel.getHistoryReviewShopUser().value?.keys?.contains("deskripsi_foto")) == false){
                            // Jika data history dikedua tabel tidak ada
                            if(reviewShop.keys.contains("deskripsi_foto") == false){
                                Log.d("ADD BOTH CALLED ?", "1")
                                batch.set(reviewShopUserDeskripsiFotoRef, ReviewShopUserModel(reviewShopUserDeskripsiFotoRef.id, idShop, idUser, "deskripsi_foto", ratingBarDeskripsiFoto))
                                batch.set(reviewShopDeskripsiFotoRef, ReviewShopModel(
                                    reviewShopDeskripsiFotoRef.id,
                                    idShop,
                                    viewModel.getSimpleShopProfile().value?.photo_url.toString(),
                                    viewModel.getSimpleShopProfile().value?.name.toString(),
                                    viewModel.getSimpleShopProfile().value?.provinsi.toString(),
                                    "deskripsi_foto",
                                    ratingBarDeskripsiFoto,
                                    1)
                                )

                            // Jika hanya menambah review pada tabel review_shop_user
                            }else{
                                Log.d("ADD ONE CALLED ?", "1")
                                var newResult = ((reviewShop.get("deskripsi_foto")?.result.toString().toDouble() *
                                        reviewShop.get("deskripsi_foto")?.total_reviewer.toString().toDouble()) +
                                        ratingBarDeskripsiFoto ) /
                                        (reviewShop.get("deskripsi_foto")?.total_reviewer.toString().toInt() + 1)

                                batch.set(reviewShopUserDeskripsiFotoRef, ReviewShopUserModel(reviewShopUserDeskripsiFotoRef.id, idShop, idUser, "deskripsi_foto", ratingBarDeskripsiFoto))
                                batch.update(reviewShopDeskripsiFotoRef, "result", newResult)
                                batch.update(reviewShopDeskripsiFotoRef, "total_reviewer", reviewShop.get("deskripsi_foto")?.total_reviewer.toString().toInt() + 1)
                            }
                        }
                    }


                    // KATEGORI FAST-RESPONE
                    if (viewModel.getPermittedReview().value!!.contains("permitted_fast_respone").equals(true)){
                        if((viewModel.getHistoryReviewShopUser().value?.keys?.contains("fast_respone")) == false){
                            // Jika data history dikedua tabel tidak ada
                            if(reviewShop.keys.contains("fast_respone") == false){
                                Log.d("ADD BOTH CALLED ?", "2")
                                batch.set(reviewShopUserFastResponeRef, ReviewShopUserModel(reviewShopUserFastResponeRef.id, idShop, idUser, "fast_respone", ratingBarFastRespone))
                                batch.set(reviewShopFastResponeRef, ReviewShopModel(
                                    reviewShopFastResponeRef.id,
                                    idShop,
                                    viewModel.getSimpleShopProfile().value?.photo_url.toString(),
                                    viewModel.getSimpleShopProfile().value?.name.toString(),
                                    viewModel.getSimpleShopProfile().value?.provinsi.toString(),
                                    "fast_respone",
                                    ratingBarFastRespone, 1)
                                )

                                // Jika hanya menambah review pada tabel review_shop_user
                            }else{
                                Log.d("ADD ONE CALLED ?", "2")
                                var newResult = ((reviewShop.get("fast_respone")?.result.toString().toDouble() *
                                        reviewShop.get("fast_respone")?.total_reviewer.toString().toDouble()) +
                                        ratingBarFastRespone ) /
                                        (reviewShop.get("fast_respone")?.total_reviewer.toString().toInt() + 1)

                                batch.set(reviewShopUserFastResponeRef, ReviewShopUserModel(reviewShopUserFastResponeRef.id, idShop, idUser, "fast_respone", ratingBarFastRespone))
                                batch.update(reviewShopFastResponeRef, "result", newResult)
                                batch.update(reviewShopFastResponeRef, "total_reviewer", reviewShop.get("fast_respone")?.total_reviewer.toString().toInt() + 1)
                            }
                        }
                    }


                    // KATEGORI KERAMAHAN
                    if (viewModel.getPermittedReview().value!!.contains("permitted_keramahan").equals(true)){
                        if((viewModel.getHistoryReviewShopUser().value?.keys?.contains("keramahan")) == false){
                            // Jika data history dikedua tabel tidak ada
                            if(reviewShop.keys.contains("keramahan") == false){
                                Log.d("ADD BOTH CALLED ?", "3")
                                batch.set(reviewShopUserKeramahanRef, ReviewShopUserModel(reviewShopUserKeramahanRef.id, idShop, idUser, "keramahan", ratingBarKeramahan))
                                batch.set(reviewShopKeramahanRef, ReviewShopModel(
                                    reviewShopKeramahanRef.id,
                                    idShop,
                                    viewModel.getSimpleShopProfile().value?.photo_url.toString(),
                                    viewModel.getSimpleShopProfile().value?.name.toString(),
                                    viewModel.getSimpleShopProfile().value?.provinsi.toString(),
                                    "keramahan",
                                    ratingBarKeramahan,
                                    1)
                                )

                                // Jika hanya menambah review pada tabel review_shop_user
                            }else{
                                Log.d("ADD ONE CALLED ?", "3")
                                var newResult = ((reviewShop.get("keramahan")?.result.toString().toDouble() *
                                        reviewShop.get("keramahan")?.total_reviewer.toString().toDouble()) +
                                        ratingBarKeramahan ) /
                                        (reviewShop.get("keramahan")?.total_reviewer.toString().toInt() + 1)

                                batch.set(reviewShopUserKeramahanRef, ReviewShopUserModel(reviewShopUserKeramahanRef.id, idShop, idUser, "keramahan", ratingBarKeramahan))
                                batch.update(reviewShopKeramahanRef, "result", newResult)
                                batch.update(reviewShopKeramahanRef, "total_reviewer", reviewShop.get("keramahan")?.total_reviewer.toString().toInt() + 1)
                            }
                        }
                    }


                    // KATEGORI KUALITAS-PENGEMASAN
                    if (viewModel.getPermittedReview().value!!.contains("permitted_kualitas_pengemasan").equals(true)){
                        if((viewModel.getHistoryReviewShopUser().value?.keys?.contains("kualitas_pengemasan")) == false){
                            // Jika data history dikedua tabel tidak ada
                            if(reviewShop.keys.contains("kualitas_pengemasan") == false){
                                Log.d("ADD BOTH CALLED ?", "4")
                                batch.set(reviewShopUserKualitasPengemasanRef, ReviewShopUserModel(reviewShopUserKualitasPengemasanRef.id, idShop, idUser, "kualitas_pengemasan", ratingBarPengemasan))
                                batch.set(reviewShopKualitasPengemasanRef, ReviewShopModel(
                                    reviewShopKualitasPengemasanRef.id,
                                    idShop,
                                    viewModel.getSimpleShopProfile().value?.photo_url.toString(),
                                    viewModel.getSimpleShopProfile().value?.name.toString(),
                                    viewModel.getSimpleShopProfile().value?.provinsi.toString(),
                                    "kualitas_pengemasan",
                                    ratingBarPengemasan,
                                    1)
                                )
                                // Jika hanya menambah review pada tabel review_shop_user
                            }else{
                                Log.d("ADD ONE CALLED ?", "4")
                                var newResult = ((reviewShop.get("kualitas_pengemasan")?.result.toString().toDouble() *
                                        reviewShop.get("kualitas_pengemasan")?.total_reviewer.toString().toDouble()) +
                                        ratingBarPengemasan ) /
                                        (reviewShop.get("kualitas_pengemasan")?.total_reviewer.toString().toInt() + 1)

                                batch.set(reviewShopUserKualitasPengemasanRef, ReviewShopUserModel(reviewShopUserKualitasPengemasanRef.id, idShop, idUser, "kualitas_pengemasan", ratingBarPengemasan))
                                batch.update(reviewShopKualitasPengemasanRef, "result", newResult)
                                batch.update(reviewShopKualitasPengemasanRef, "total_reviewer", reviewShop.get("kualitas_pengemasan")?.total_reviewer.toString().toInt() + 1)
                            }
                        }
                    }


                }//End runBatch
                    .addOnSuccessListener {
                        checkHistoryReview(idShop, idUser)
                        Toast.makeText(this, "Submit Review Berhasil", Toast.LENGTH_SHORT).show()
                        loadingDialog.dismiss()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Submit Review Gagal", Toast.LENGTH_SHORT).show()
                        loadingDialog.dismiss()
                    }


            }.addOnFailureListener { exception ->

            }//End fetch review_shop
    }//End submit review


} // End class