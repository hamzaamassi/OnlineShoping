package com.h.alamassi.onlineshoping

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.h.alamassi.onlineshoping.databinding.ActivityMainBinding
import com.h.alamassi.onlineshoping.fragment.CartItemFragment
import com.h.alamassi.onlineshoping.fragment.CategoryFragment
import com.h.alamassi.onlineshoping.fragment.ProfileShowFragment
import com.h.alamassi.onlineshoping.fragment_user.CategoryUserFragment


class MainActivity : AppCompatActivity() {
    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser!!.uid == "bH2ND7OZnvR0drNd0vHiGGxaez33") {

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CategoryFragment())
                .commit()
        } else {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CategoryUserFragment())
                .commit()
        }

        mainBinding.bottomNavigation.setOnItemSelectedListener {
            val fragment: Fragment =
                if (firebaseAuth.currentUser!!.uid == "bH2ND7OZnvR0drNd0vHiGGxaez33") {
                    when (it.itemId) {
                        R.id.nav_home -> CategoryFragment()
                        R.id.nav_profile -> ProfileShowFragment()
                        R.id.nav_catItem -> CartItemFragment()
                        else -> CategoryFragment()
                    }
                } else {
                    when (it.itemId) {
                        R.id.nav_home -> CategoryUserFragment()
                        R.id.nav_profile -> ProfileShowFragment()
                        R.id.nav_catItem -> CartItemFragment()
                        else -> CategoryUserFragment()
                    }
                }
            supportFragmentManager.beginTransaction()
                .addToBackStack("")
                .replace(R.id.fragment_container, fragment)
                .commit()
            false
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.logOutItem -> onClickLogout()
            R.id.searchItem -> search()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun search() {
    }

    private fun onClickLogout() {
        FirebaseAuth.getInstance().signOut()
        val i = Intent(this, LoginActivity::class.java)
        startActivity(i)
        firebaseAuth.signOut()
        finish()
    }
}
