package com.ocrmission.vitesse.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.Navigation.findNavController
import com.ocrmission.vitesse.R
import com.ocrmission.vitesse.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.fragment.NavHostFragment

/**
 * Main activity of the application.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    /**
     * Activity life cycle - Called when the activity is first created.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //get the view
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init the navigation
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController



        //enable edge to edge (navigation feature)
        enableEdgeToEdge()
        // Set up the system bars for the edge to edge feature
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(findViewById(R.id.fragmentContainerView))
        return navController.navigateUp() || super.onSupportNavigateUp()
    }


}


