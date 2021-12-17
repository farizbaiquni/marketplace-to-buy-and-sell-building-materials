package com.example.e_commercetokobangunan_koma

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.e_commercetokobangunan_koma.databinding.ActivityUpdateProductBinding
import com.example.e_commercetokobangunan_koma.databinding.ActivityShopProductListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.io.IOException

class UpdateProductActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityUpdateProductBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateProductBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Action Bar Name
        supportActionBar?.title = "Edit Produk"

        // Initialize Firebase Auth
        auth = Firebase.auth

        val bundle: Bundle? = intent.extras
        val idProduct = bundle?.get("idProduct").toString()

        getProductDetail(idProduct)

        binding.btnEditProduct.setOnClickListener(View.OnClickListener {
            var nama = binding.etNama.text.toString().trim()
            var harga = binding.etHarga.text.toString().trim()
            var deskripsi = binding.etDeskripsi.text.toString().trim()
            var linkVideo = binding.etLinkVideo.text.toString().trim()
            var jumlahStok = binding.etJumlahStok.text.toString().trim()
            var berat = binding.etBerat.text.toString().trim()
            var kondisiBaru = binding.radioButtonBaru.isChecked
            var kondisiBekas = binding.radioButtonBekas.isChecked

            if(validateForm(nama, harga, deskripsi, jumlahStok, berat, kondisiBaru, kondisiBekas)){
                updateProduct(idProduct, nama, harga, deskripsi, linkVideo, berat, jumlahStok, kondisiBaru,)
            }

        })

    }


    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser == null){
            startActivity(Intent(this, MainActivity::class.java))
        }
    }


    private fun getProductDetail(idProduct: String){
        val docRef = Firebase.firestore.collection("product").document(idProduct)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    try{
                        Picasso.get().load(document.data?.get("default_photo").toString())
                            .resize(700, 700).centerCrop().into(binding.photo)
                    }catch (e: IOException){}

                    binding.etNama.setText(document.data?.get("name").toString())
                    binding.etHarga.setText(document.data?.get("price").toString())
                    binding.etDeskripsi.setText(document.data?.get("description").toString())
                    binding.etLinkVideo.setText(document.data?.get("link_video").toString())
                    binding.etJumlahStok.setText(document.data?.get("stock").toString())
                    binding.etBerat.setText(document.data?.get("weight").toString())
                    if(document.getBoolean("new_condition")!!.equals(true)){
                        binding.radioButtonBaru.isChecked = true
                    }else{
                        binding.radioButtonBekas.isChecked = true
                    }
                    binding.btnEditProduct.isEnabled = true
                } else {
                    Toast.makeText(this, "Gagal mendapatkan data", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                    Toast.makeText(this, "Gagal mendapatkan data", Toast.LENGTH_SHORT).show()
            }
    }


    private fun updateProduct(idProduct: String, nama: String, harga: String, deskripsi: String, linkVideo: String, berat: String,
                              jumlahStok: String, kondisiBaru: Boolean){

        val docRef = Firebase.firestore.collection("product").document(idProduct)

        Firebase.firestore.runTransaction { transaction ->

            transaction.update(docRef, "name", nama)
            transaction.update(docRef, "price", harga.toLong())
            transaction.update(docRef, "description", deskripsi)
            transaction.update(docRef, "link_video", linkVideo)
            transaction.update(docRef, "stock", jumlahStok.toLong())
            transaction.update(docRef, "weight", berat.toDouble())
            transaction.update(docRef, "new_condition", kondisiBaru)

        }.addOnSuccessListener {
            Toast.makeText(this, "Data Produk Berhasil Diupdate", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, ShopProductListActivity::class.java))
        }
            .addOnFailureListener {
                Toast.makeText(this, "Data Produk Gagal Diupdate", Toast.LENGTH_SHORT).show()
            }

    }



    fun validateForm(nama: String, harga: String, deskripsi: String, jumlakStok: String,
                     berat: String, kondisiBaru: Boolean, kondisiBekas: Boolean) : Boolean {

        if (nama.isBlank() || harga.isBlank() || deskripsi.isBlank() || jumlakStok.isBlank() || berat.isBlank()
            || kondisiBaru.equals(false) && kondisiBekas.equals(false)
        ) {

            if (nama.isBlank()) {
                binding.textFieldNama.error = "Tidak boleh kosong"
            } else {
                binding.textFieldNama.error = null
            }

            if (harga.isBlank()) {
                binding.textFieldHarga.error = "Tidak boleh kosong"
            } else {
                binding.textFieldHarga.error = null
            }

            if (deskripsi.isBlank()) {
                binding.textFieldDeskripsi.error = "Tidak boleh kosong"
            } else {
                binding.textFieldDeskripsi.error = null
            }

            if (jumlakStok.isBlank()) {
                binding.textFieldJumlahStok.error = "Tidak boleh kosong"
            } else {
                binding.textFieldJumlahStok.error = null
            }

            if (berat.isBlank()) {
                binding.textFieldBerat.error = "Tidak boleh kosong"
            } else {
                binding.textFieldBerat.error = null
            }

            if (kondisiBaru.equals(false) && kondisiBekas.equals(false)) {
                binding.textFieldKondisi.error = "Harus pilih salah satu"
            } else {
                binding.textFieldKondisi.error = null
            }

            return false
        } else {
            binding.textFieldNama.error = null
            binding.textFieldHarga.error = null
            binding.textFieldDeskripsi.error = null
            binding.textFieldLinkVideo.error = null
            binding.textFieldJumlahStok.error = null
            binding.textFieldBerat.error = null
            binding.textFieldKondisi.error = null
            return true
        }

    }


}