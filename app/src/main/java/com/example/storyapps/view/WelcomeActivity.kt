package com.example.storyapps.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapps.R
import com.example.storyapps.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()

        // Get references to the views using view binding
        val title = binding.txtTittle
        val button = binding.btnWelcome

        // Animate the title
        val titleAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_top)
        title.startAnimation(titleAnimation)

        // Animate the button
        val buttonAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom)
        button.startAnimation(buttonAnimation)

        // Set visibility of views to VISIBLE
        title.visibility = View.VISIBLE
        button.visibility = View.VISIBLE
    }

    private fun setupAction() {
        binding.btnWelcome.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}