package com.example.e_commercetokobangunan_koma

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.example.e_commercetokobangunan_koma.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var confirmPassowrd: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Initialize Firebase Auth
        auth = Firebase.auth


        binding.btnToSignInActivity.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        })

        binding.btnCreateAccount.setOnClickListener(View.OnClickListener {
            email = binding.etEmail.text.toString().trim()
            password = binding.etPassword.text.toString()
            confirmPassowrd = binding.etConfirmPassword.text.toString()

            if(validateFormSignUp(email, password, confirmPassowrd)){
                registrationAccount(email, password)
            }
        })

    } // End onCreate


    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            startActivity(Intent(this, MainActivity::class.java))
        }
    } // End onStart


    fun registrationAccount(email: String, password: String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign up success, update UI with the signed-in user's information
                    Toast.makeText(this, "Akun berhasil dibuat", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, SignInActivity::class.java))
                } else {
                    // If sign up fails, display a message to the user.
                    Toast.makeText(this, "Gagal membuat akun", Toast.LENGTH_SHORT).show()
                }
            }
    } // End registrationAccount


    fun validateFormSignUp(email: String, password: String, confirmPassword: String): Boolean{

        if(email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches() || password.isBlank()
            || password.length <= 5 || !confirmPassword.equals(password)){

            if(email.isBlank()){
                binding.textFieldEmail.error = "Input tidak boleh kosong"
            }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.textFieldEmail.error = "Format email tidak benar"
            }else{
                binding.textFieldEmail.error = null
            }

            if(password.isBlank()){
                binding.textFieldPassword.error = "Input tidak boleh kosong"
            }else if(password.length <= 5){
                binding.textFieldPassword.error = "Minimal password 6 karakter"
            }else{
                binding.textFieldPassword.error = null
            }

            if(!confirmPassword.equals(password)){
                binding.textFieldConfirmPassword.error = "Konfirmasi password tidak sama"
            }else{
                binding.textFieldConfirmPassword.error = null
            }

            return false
        } else {
            return true
        }
    } // End validateFormSignUp


} // End class