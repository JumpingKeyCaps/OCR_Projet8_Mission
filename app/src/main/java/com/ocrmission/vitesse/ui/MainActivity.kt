package com.ocrmission.vitesse.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ocrmission.vitesse.R
import com.ocrmission.vitesse.databinding.ActivityMainBinding
import com.ocrmission.vitesse.ui.home.HomeFragment
import dagger.hilt.android.AndroidEntryPoint

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

        //load fragment home by default
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, HomeFragment()).commit()



        //enable edge to edge (navigation feature)
        enableEdgeToEdge()
        // Set up the system bars for the edge to edge feature
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}