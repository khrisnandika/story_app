package com.example.storyapps.view

import DetailViewModel
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.example.storyapps.data.model.DetailStory
import com.example.storyapps.databinding.ActivityDetailBinding
import com.example.storyapps.utils.ResultStory

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: DetailViewModel
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi SharedPreferences
        sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        val storyId = intent.getStringExtra("story_id")

        // Inisialisasi DetailViewModel
        viewModel = ViewModelProvider(this).get(DetailViewModel::class.java)

        // Observe resultDetailStory untuk mendapatkan data detail story
        viewModel.resultDetailStory.observe(this) { result ->
            when (result) {
                is ResultStory.Success<*> -> {
                    val detailStory = result.data as? DetailStory
                    showDetailStory(detailStory)
                }
                is ResultStory.Error -> {
                    Toast.makeText(this, result.exception.message.toString(), Toast.LENGTH_SHORT).show()
                }
                is ResultStory.Loading -> {
                    // Handle loading state if needed
                }
            }
        }

        // Ambil token dari SharedPreferences
        val token = sharedPreferences.getString("token", "")

        // Ambil detail story berdasarkan ID yang diterima
        storyId?.let {
            token?.let { tokenValue ->
                viewModel.getStoryDetail(tokenValue, it)
            } ?: run {
                Toast.makeText(this, "Token not found", Toast.LENGTH_SHORT).show()
                // Redirect ke halaman login jika token tidak tersedia
                // Misalnya:
                // startActivity(Intent(this, LoginActivity::class.java))
                // finish()
            }
        }
    }

    private fun showDetailStory(detailStory: DetailStory?) {
        if (detailStory != null) {
            binding.imageView.load(detailStory.photoUrl)
            binding.txtJudul.text = detailStory.name
            binding.txtDeskripsi.text = detailStory.description
        } else {
            Toast.makeText(this, "Detail story not found", Toast.LENGTH_SHORT).show()
            // You can finish() the activity or handle the error state accordingly
        }
    }
}
