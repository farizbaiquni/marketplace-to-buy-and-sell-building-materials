package com.example.e_commercetokobangunan_koma

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.e_commercetokobangunan_koma.databinding.ActivityAddProfileShopBinding
import com.example.e_commercetokobangunan_koma.models.ShopModel
import com.example.e_commercetokobangunan_koma.viewmodels.AddProfileShopViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.*

class AddProfileShopActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityAddProfileShopBinding
    private lateinit var viewModel: AddProfileShopViewModel
    private lateinit var builderLoadingDialog: AlertDialog.Builder
    private lateinit var loadingDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProfileShopBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Initialize Firebase Auth
        auth = Firebase.auth

        //Action Bar Name
        getSupportActionBar()?.setTitle("Tambah Profil Toko")

        //View Model
        viewModel = AddProfileShopViewModel()

        //Laoding Dialog
        builderLoadingDialog = AlertDialog.Builder(this)
        var inflater: LayoutInflater = layoutInflater
        builderLoadingDialog.setView(inflater.inflate(R.layout.loading_dialog, null))
        builderLoadingDialog.setCancelable(false)
        loadingDialog = builderLoadingDialog.create()

        viewModel.getPhoto().observe(this){ photo ->
            if(photo != null){
                var bitmap: Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, photo)
                binding.profileImage.setImageBitmap(bitmap)
            }
        }

    }//End onCreate


    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null) {

            binding.btnAddProfileShop.setOnClickListener(View.OnClickListener {
                var photo: Uri? = viewModel.getPhoto().value
                var namaToko: String = binding.etNama.text.toString().trim()
                var deskripsiToko: String = binding.etDeskripsi.text.toString().trim()
                var provinsi: String = binding.etProvinsi.text.toString().trim()
                var kabupatenKota: String = binding.etKabupatenKota.text.toString().trim()
                var kecamatan: String = binding.etKecamatan.text.toString().trim()
                var kelurahanDesa: String = binding.etKelurahanDesa.text.toString().trim()
                var alamatDetail: String = binding.etAlamatDetail.text.toString().trim()

                if(validateForm(photo, namaToko, deskripsiToko, provinsi, kabupatenKota, kecamatan, kelurahanDesa, alamatDetail)){
                    uploadPhoto(photo, currentUser.uid, namaToko, deskripsiToko, provinsi, kabupatenKota,
                        kecamatan, kelurahanDesa, alamatDetail)
                }
            })

            val permissions = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
            )

            binding.profileImage.setOnClickListener(View.OnClickListener {
                if(isAllPermissionsGranted(permissions.toList()).equals(false)){
                    requestMultiplePermissionsLauncher.launch(permissions)
                }else {
                    var intent = Intent()
                    intent.type = "image/*"
                    intent.action = Intent.ACTION_GET_CONTENT
                    resultLauncher.launch(intent)
                }
            })

        }else{
            startActivity(Intent(this, WelcomeActivity::class.java))
        }
    }// End onSTart




    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            var imageUri: Uri? = data?.data
            if (imageUri != null) {
                binding.errorSelectImageShop.visibility = View.GONE
                viewModel.setPhoto(imageUri)
            }
        }
    }




    fun isAllPermissionsGranted(permissions: List<String>): Boolean{
        for(permission in permissions){
            if(ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED){
                return false
            }
        }
        return true
    }




    private val requestMultiplePermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            // Handle Permission granted/rejected
    }



    private fun validateForm(photo: Uri?, nama: String, deskripsiToko: String, provinsi: String, kabupatenKota: String,
                             kecamatan: String, kelurahanDesa: String, alamatDetail: String): Boolean{

        if (nama.isBlank() || (nama.length < 3) || deskripsiToko.isBlank() || (deskripsiToko.length < 3)
            || provinsi.isBlank() || kabupatenKota.isBlank() || kecamatan.isBlank() ||
            kelurahanDesa.isBlank() || alamatDetail.isBlank() || (photo == null)) {

            if(photo == null){
                binding.errorSelectImageShop.visibility = View.VISIBLE
            }else{
                binding.errorSelectImageShop.visibility = View.GONE
            }

            if(nama.isBlank()){
                binding.textFieldNama.error = "Input tidak boleh kosong"
            }else if(nama.length < 3){
                binding.textFieldNama.error = "Minimal nama toko 3 karakter"
            }else{
                binding.textFieldNama.error = null
            }

            if(deskripsiToko.isBlank()){
                binding.textFieldDeskripsi.error = "Input tidak boleh kosong"
            }else if(deskripsiToko.length < 3){
                binding.textFieldDeskripsi.error = "Minimal deskripsi toko 3 karakter"
            }else{
                binding.textFieldDeskripsi.error = null
            }

            if(provinsi.isBlank()){
                binding.textFieldProvinsi.error = "Input tidak boleh kosong"
            }else{
                binding.textFieldProvinsi.error = null
            }

            if(kabupatenKota.isBlank()){
                binding.textFieldKabupatenKota.error = "Input tidak boleh kosong"
            }else{
                binding.textFieldKabupatenKota.error = null
            }

            if(kecamatan.isBlank()){
                binding.textFieldKecamatan.error = "Input tidak boleh kosong"
            }else{
                binding.textFieldKecamatan.error = null
            }

            if(kelurahanDesa.isBlank()){
                binding.textFieldKelurahanDesa.error = "Input tidak boleh kosong"
            }else{
                binding.textFieldKelurahanDesa.error = null
            }

            if(alamatDetail.isBlank()){
                binding.textFieldAlamatDetail.error = "Input tidak boleh kosong"
            }else{
                binding.textFieldAlamatDetail.error = null
            }
            return false
        } else {
            return true
        }

    }



    private fun uploadPhoto(photo: Uri?, idUser: String, namaToko: String, deskripsiToko: String, provinsi: String,
                            kabupatenKota: String, kecamatan: String, kelurahanDesa: String,
                            alamatDetail: String){
        loadingDialog.show()
        var fileName: UUID = UUID.randomUUID()
        val ref = Firebase.storage.reference.child("shop_profile_photos/$fileName")
        var uploadTask = photo?.let { ref.putFile(it) }

        if (uploadTask != null) {
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                ref.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val url = task.result.toString()
                    addShopProfile(url, idUser, namaToko, deskripsiToko, provinsi, kabupatenKota,
                        kecamatan, kelurahanDesa, alamatDetail)
                } else {
                    addShopProfile("", idUser, namaToko, deskripsiToko, provinsi, kabupatenKota,
                        kecamatan, kelurahanDesa, alamatDetail)
                }
            }
        }
    }


    private fun addShopProfile(photoUrl: String, idUser: String, namaToko: String, deskripsiToko: String, provinsi: String,
                               kabupatenKota: String, kecamatan: String, kelurahanDesa: String,
                               alamatDetail: String){

        val docData = ShopModel(idUser, photoUrl, namaToko, deskripsiToko, Date(), provinsi, kabupatenKota,
            kecamatan, kelurahanDesa, alamatDetail)

        Firebase.firestore.collection("shop")
            .add(docData)
            .addOnSuccessListener { documentReference ->
                loadingDialog.dismiss()
                Toast.makeText(this, "Profil toko berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, ShopProductListActivity::class.java))
            }
            .addOnFailureListener { e ->
                loadingDialog.dismiss()
                Toast.makeText(this, "Gagal menambahkan profil toko", Toast.LENGTH_SHORT).show()
            }
    }

}// End Class