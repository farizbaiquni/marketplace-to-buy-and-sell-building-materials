package com.example.e_commercetokobangunan_koma

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.e_commercetokobangunan_koma.databinding.ActivityWelcomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class WelcomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Action Bar
        supportActionBar?.title = "Selamat Datang"

        // Initialize Firebase Auth
        auth = Firebase.auth

        binding.btnToSignInActivity.setOnClickListener( View.OnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        })

        binding.btnToSignUpActivity.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        })
    } // End onCreate


    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            startActivity(Intent(this, MainActivity::class.java))
        }
    } // End onStart



}// End class