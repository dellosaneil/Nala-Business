package com.example.nalasbusinesstracker

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.example.nalasbusinesstracker.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = findNavController(R.id.navHostFragment)
    }

    private val destinationChangeListener =
        NavController.OnDestinationChangedListener { _, destination, _ ->
            when(destination.id){
                R.id.addInventoryFragment -> binding.materialToolbar.visibility = View.GONE
                else -> binding.materialToolbar.visibility = View.VISIBLE
            }
        }


    override fun onResume() {
        super.onResume()
        navController.addOnDestinationChangedListener(destinationChangeListener)
    }

    override fun onPause() {
        super.onPause()
        navController.removeOnDestinationChangedListener(destinationChangeListener)
    }


}