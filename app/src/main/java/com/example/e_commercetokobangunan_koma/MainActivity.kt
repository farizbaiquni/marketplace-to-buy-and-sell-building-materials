package com.example.e_commercetokobangunan_koma

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.e_commercetokobangunan_koma.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    private lateinit var intentActivity: Intent
    private lateinit var tabOptions: Array<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Action Bar
        getSupportActionBar()?.setTitle("Explore")

        // Initialize Firebase Auth
        auth = Firebase.auth

        //Bottom Navigation
        val exploreFragment = ExploreFragment()
        val chartsFragment = ChartsFragment()

        setCurrentFragment(exploreFragment)

        binding.bottomNavigationMainActivity.setOnItemSelectedListener { it ->
            when(it.itemId){
                R.id.bottom_navigation_explore -> {
                    //Action Bar
                    getSupportActionBar()?.setTitle("Explore")
                    setCurrentFragment(exploreFragment)
                }
                R.id.bottom_navigation_chats -> {
                    //Action Bar
                    getSupportActionBar()?.setTitle("Top Charts")
                    setCurrentFragment(chartsFragment)
                }
            }
            true
        }


    }// End onCreate



    private fun setCurrentFragment(fragment: Fragment) = supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame_layout_main_activity, fragment)
            commit()
        }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        val inflater: MenuInflater = menuInflater

        if(auth.currentUser != null){
            inflater.inflate(R.menu.option_menu_buyer, menu)
        }else{
            inflater.inflate(R.menu.option_menu_guest_user, menu)
        }

        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_buyer_toko -> {
                if(auth.currentUser == null){
                    Toast.makeText(this, "Harus Login Terlebih Dahulu", Toast.LENGTH_SHORT).show()
                }else{
                    startActivity(Intent(this, ShopProductListActivity::class.java))
                }
                true
            }
            R.id.menu_buyer_profil -> {
                Toast.makeText(this, "PROFILE", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.menu_buyer_logout -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, MainActivity::class.java))
                true
            }
            R.id.menu_buyer_login -> {
                startActivity(Intent(this, WelcomeActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}