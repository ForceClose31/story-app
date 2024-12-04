package com.example.storyapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.storyapp.utils.DataStoreManager
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var dataStoreManager: DataStoreManager
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dataStoreManager = DataStoreManager(this)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        lifecycleScope.launch {
            val token = dataStoreManager.getToken()

            if (token.isNullOrEmpty()) {
                if (navController.currentDestination?.id != R.id.loginFragment) {
                    navController.navigate(R.id.loginFragment)
                }
            } else {
                if (navController.currentDestination?.id != R.id.homeFragment) {
                    navController.navigate(R.id.homeFragment)
                }
            }
        }
    }
}
