package com.example.e_commercetokobangunan_koma

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.e_commercetokobangunan_koma.databinding.ActivityAddProfileShopBinding
import com.example.e_commercetokobangunan_koma.models.ShopModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class AddProfileShopActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityAddProfileShopBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProfileShopBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Initialize Firebase Auth
        auth = Firebase.auth

        //Action Bar Name
        getSupportActionBar()?.setTitle("Tambah Profil Toko")

    }//End onCreate


    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null) {

            binding.btnAddProfileShop.setOnClickListener(View.OnClickListener {
                var namaToko: String = binding.etNama.text.toString().trim()
                var deskripsiToko: String = binding.etDeskripsi.text.toString().trim()
                var provinsi: String = binding.etProvinsi.text.toString().trim()
                var kabupatenKota: String = binding.etKabupatenKota.text.toString().trim()
                var kecamatan: String = binding.etKecamatan.text.toString().trim()
                var kelurahanDesa: String = binding.etKelurahanDesa.text.toString().trim()
                var alamatDetail: String = binding.etAlamatDetail.text.toString().trim()

                if(validateForm(namaToko, deskripsiToko, provinsi, kabupatenKota, kecamatan, kelurahanDesa, alamatDetail)){
                    addShopProfile(currentUser.uid, namaToko, deskripsiToko, provinsi, kabupatenKota,
                        kecamatan, kelurahanDesa, alamatDetail)
                }
            })
        }else{
            startActivity(Intent(this, WelcomeActivity::class.java))
        }
    }// End onSTart

    private fun validateForm(nama: String, deskripsiToko: String, provinsi: String, kabupatenKota: String,
                             kecamatan: String, kelurahanDesa: String, alamatDetail: String): Boolean{

        if (nama.isBlank() || (nama.length < 3) || deskripsiToko.isBlank() || (deskripsiToko.length < 3)
            || provinsi.isBlank() || kabupatenKota.isBlank() || kecamatan.isBlank() ||
            kelurahanDesa.isBlank() || alamatDetail.isBlank()) {

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


    private fun addShopProfile(idUser: String, namaToko: String, deskripsiToko: String, provinsi: String,
                               kabupatenKota: String, kecamatan: String, kelurahanDesa: String,
                               alamatDetail: String){

        val docData = ShopModel(idUser, "", namaToko, deskripsiToko, Date(), provinsi, kabupatenKota,
            kecamatan, kelurahanDesa, alamatDetail)

        Firebase.firestore.collection("shop")
            .add(docData)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Profil toko berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, ShopProductListActivity::class.java))
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal menambahkan profil toko", Toast.LENGTH_SHORT).show()
            }
    }


}