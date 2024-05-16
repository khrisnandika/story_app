package com.example.storyapps.view

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapps.R
import com.example.storyapps.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)

        val backgroundImage : ImageView = findViewById(R.id.logoSplash)
        val sideAnimation = AnimationUtils.loadAnimation(this, R.anim.slide)
        backgroundImage.startAnimation(sideAnimation)

        Handler().postDelayed({
            if (isLoggedIn()) {
                // Jika pengguna sudah login sebelumnya, arahkan ke MainActivity
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // Jika pengguna belum login sebelumnya, arahkan ke WelcomeActivity
                startActivity(Intent(this, WelcomeActivity::class.java))
            }
            finish()
        }, 3000)
    }

    private fun isLoggedIn(): Boolean {
        // Periksa apakah pengguna sudah login sebelumnya
        return sharedPreferences.getBoolean("is_logged_in", false)
    }
}
