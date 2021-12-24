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
import com.example.e_commercetokobangunan_koma.databinding.ActivityAddProfileUserBinding
import com.example.e_commercetokobangunan_koma.models.AddProfileUserModel
import com.example.e_commercetokobangunan_koma.viewmodels.AddProfileUserViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.*

class AddProfileUserActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityAddProfileUserBinding
    private lateinit var viewModel: AddProfileUserViewModel
    private lateinit var builderLoadingDialog: AlertDialog.Builder
    private lateinit var loadingDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProfileUserBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Initialize Firebase Auth
        auth = Firebase.auth

        //Action Bar Name
        supportActionBar?.title = "Profile User"

        //Laoding Dialog
        builderLoadingDialog = AlertDialog.Builder(this)
        var inflater: LayoutInflater = layoutInflater
        builderLoadingDialog.setView(inflater.inflate(R.layout.loading_dialog, null))
        builderLoadingDialog.setCancelable(false)
        loadingDialog = builderLoadingDialog.create()

        viewModel = AddProfileUserViewModel()

        viewModel.getPhoto().observe(this){ photo ->
            if(photo != null){
                var bitmap: Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, photo)
                binding.profileImage.setImageBitmap(bitmap)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null) {

            binding.btnAddProfileUser.setOnClickListener(View.OnClickListener {
                var photo: Uri? = viewModel.getPhoto().value
                var username: String = binding.etUsername.text.toString().trim()
                var provinsi: String = binding.etProvinsi.text.toString().trim()
                var kabupatenKota: String = binding.etKabupatenKota.text.toString().trim()
                var kecamatan: String = binding.etKecamatan.text.toString().trim()
                var kelurahanDesa: String = binding.etKelurahanDesa.text.toString().trim()
                var alamatDetail: String = binding.etAlamatDetail.text.toString().trim()

                if(validateForm(photo, username, provinsi, kabupatenKota, kecamatan, kelurahanDesa, alamatDetail)){
                    uploadPhoto(photo, currentUser.uid, username, provinsi, kabupatenKota,
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


    private fun validateForm(photo: Uri?, username: String, provinsi: String, kabupatenKota: String,
                             kecamatan: String, kelurahanDesa: String, alamatDetail: String): Boolean{

        if (username.isBlank() || (username.length < 3) || provinsi.isBlank() || kabupatenKota.isBlank()
            || kecamatan.isBlank() || kelurahanDesa.isBlank() || alamatDetail.isBlank() || (photo == null)) {

            if(photo == null){
                binding.errorSelectImageShop.visibility = View.VISIBLE
            }else{
                binding.errorSelectImageShop.visibility = View.GONE
            }

            if(username.isBlank()){
                binding.textFieldUsername.error = "Input tidak boleh kosong"
            }else if(username.length < 3){
                binding.textFieldUsername.error = "Minimal username 3 karakter"
            }else{
                binding.textFieldUsername.error = null
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



    fun isAllPermissionsGranted(permissions: List<String>): Boolean{
        for(permission in permissions){
            if(ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED){
                return false
            }
        }
        return true
    }


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


    private val requestMultiplePermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        // Handle Permission granted/rejected
    }

    private fun uploadPhoto(photo: Uri?, idUser: String, username: String, provinsi: String,
                            kabupatenKota: String, kecamatan: String, kelurahanDesa: String,
                            alamatDetail: String){
        loadingDialog.show()
        var fileName: UUID = UUID.randomUUID()
        val ref = Firebase.storage.reference.child("user_profile_photos/$fileName")
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
                    addProfileUser(url, idUser, username, provinsi, kabupatenKota,
                        kecamatan, kelurahanDesa, alamatDetail)
                } else {
                    addProfileUser("", idUser, username, provinsi, kabupatenKota,
                        kecamatan, kelurahanDesa, alamatDetail)
                }
            }
        }
    }

    private fun addProfileUser(photoUrl: String, idUser: String, username: String, provinsi: String,
                               kabupatenKota: String, kecamatan: String, kelurahanDesa: String,
                               alamatDetail: String){

        val docData = AddProfileUserModel(idUser, photoUrl, username, Timestamp.now().toDate(), provinsi, kabupatenKota,
            kecamatan, kelurahanDesa, alamatDetail)

        val user = Firebase.auth.currentUser
        val profileUpdates = userProfileChangeRequest {
            displayName = username
        }

        user!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Firebase.firestore.collection("users")
                        .add(docData)
                        .addOnSuccessListener { documentReference ->
                            loadingDialog.dismiss()
                            Toast.makeText(this, "Profil user berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                        }
                        .addOnFailureListener { e ->
                            loadingDialog.dismiss()
                            Toast.makeText(this, "Gagal menambahkan profil user", Toast.LENGTH_SHORT).show()
                        }
                }else{
                    Toast.makeText(this, "Gagal menambah data profile", Toast.LENGTH_SHORT).show()
                }
            }

    }

}