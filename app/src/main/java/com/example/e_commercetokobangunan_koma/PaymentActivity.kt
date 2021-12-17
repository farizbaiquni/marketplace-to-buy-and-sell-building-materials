package com.example.e_commercetokobangunan_koma

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.e_commercetokobangunan_koma.databinding.ActivityPaymentBinding
import com.example.e_commercetokobangunan_koma.models.TransactionModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class PaymentActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityPaymentBinding
    private lateinit var idProduct: String
    private lateinit var idShop: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Initialize Firebase Auth
        auth = Firebase.auth

        val bundle: Bundle? = intent.extras
        idProduct = bundle?.get("idProduct").toString()
        idShop = bundle?.get("idShop").toString()

    }


    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, WelcomeActivity::class.java))
        }else{
            binding.btnBuy.setOnClickListener(View.OnClickListener {
                postTransaction(currentUser.uid, idProduct, idShop)
            })
        }
    }


    fun postTransaction(idBuyer: String, idProduct: String, idShop: String){
        var data: TransactionModel = TransactionModel(idBuyer, idProduct, idShop, "Baiquni", Date(), 2,
            20000, 20000, 40000, "Jawa Tengah", "Kendal",
            "Kendal", "Karangsari", "Rt.4 Rw.3")

        Firebase.firestore.collection("transaction")
            .add(data)
            .addOnSuccessListener { documentReference ->
                //Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
                Toast.makeText(this, "Transaksi Berhasil", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
            }
            .addOnFailureListener { e ->
                //Log.w(TAG, "Error adding document", e)
                Toast.makeText(this, "Transaksi Gagal", Toast.LENGTH_SHORT).show()
            }
    }

}// End class