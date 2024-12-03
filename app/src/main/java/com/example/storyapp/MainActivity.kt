package com.example.storyapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.storyapp.utils.DataStoreManager
import com.example.storyapp.presentation.login.LoginFragment
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var dataStoreManager: DataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataStoreManager = DataStoreManager(this)

        lifecycleScope.launch {
            val token = dataStoreManager.getToken()
            if (token.isNullOrEmpty()) {
                navigateToLoginFragment()
            }
//            else {
//                navigateToHome()
//            }
        }
    }

    private fun navigateToLoginFragment() {
        val loginFragment = LoginFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.loginFragment, loginFragment)
            .addToBackStack(null)
            .commit()
    }

//    private fun navigateToHomeFragment() {
//        val homeFragment = HomeFragment()
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container, homeFragment)
//            .addToBackStack(null)
//            .commit()
//    }

}
