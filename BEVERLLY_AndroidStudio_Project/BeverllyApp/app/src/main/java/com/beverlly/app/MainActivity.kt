package com.beverlly.app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.beverlly.app.databinding.ActivityMainBinding
import com.beverlly.app.util.NetworkObserver

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNav.setupWithNavController(navController)

        NetworkObserver.isConnected.observe(this) { isConnected ->
            if (!isConnected) {
                Toast.makeText(this, "📶 You are offline. Changes will sync when reconnected.", Toast.LENGTH_LONG).show()
            }
        }
    }
}