package com.example.e_commercetokobangunan_koma

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.e_commercetokobangunan_koma.databinding.ActivityAddProfileShopBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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
                var deskripsiToko: String = ""

                if(validateForm(namaToko).equals(true)){
                    if(!binding.etDeskripsi.text.isNullOrBlank()){
                        deskripsiToko = binding.etDeskripsi.text.toString().trim()
                    }
                    addShopProfile(currentUser.uid, namaToko, deskripsiToko)
                }
            })
        }else{
            startActivity(Intent(this, WelcomeActivity::class.java))
        }
    }// End onSTart

    private fun validateForm(nama: String): Boolean{
        if (nama.isBlank()) {
            binding.textFieldNama.error = "Input tidak boleh kosong"
            return false
        } else if (nama.length <= 2) {
            binding.textFieldNama.error = "Minimal nama  toko 3 karakter"
            return false
        } else {
            binding.textFieldNama.error = null
            return true
        }
    }


    private fun addShopProfile(idUser: String, nama: String, deskripsi: String){

        val docData = hashMapOf(
            "id_user" to idUser,
            "photo_url" to "",
            "name" to nama,
            "description" to deskripsi,
        )

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