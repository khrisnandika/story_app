package com.example.storyapps.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapps.adapter.AdapterStory
import com.example.storyapps.data.model.ListStoryItem
import com.example.storyapps.databinding.ActivityMainBinding
import com.example.storyapps.utils.ResultStory
import com.example.storyapps.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val mainViewModel by viewModels<MainViewModel>()
    private val adapter by lazy {
        AdapterStory { item -> navigateToDetail(item.id) }
    }

    // Menambahkan variabel boolean untuk menandai apakah operasi refresh sedang berlangsung
    private var isRefreshing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        // Mendapatkan nama pengguna dari SharedPreferences dan menampilkannya
        val username = sharedPreferences.getString("username", "")
        binding.txtUsername.text = username

        binding.recylerViewMain.layoutManager = LinearLayoutManager(this)
        binding.recylerViewMain.setHasFixedSize(true)
        binding.recylerViewMain.adapter = adapter

        val token = sharedPreferences.getString("token", "")

        mainViewModel.resultStory.observe(this) {
            when (it) {
                is ResultStory.Success<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    adapter.setUsersData(it.data as MutableList<ListStoryItem>)
                    // Jika operasi refresh sedang berlangsung, nonaktifkan animasi refresh
                    if (isRefreshing) {
                        binding.swipeRefreshLayout.isRefreshing = false
                        isRefreshing = false // Menandai bahwa operasi refresh telah selesai
                    }
                }
                is ResultStory.Error -> {
                    Toast.makeText(this, it.exception.message.toString(), Toast.LENGTH_SHORT).show()
                    // Jika operasi refresh sedang berlangsung dan terjadi kesalahan, nonaktifkan animasi refresh
                    if (isRefreshing) {
                        binding.swipeRefreshLayout.isRefreshing = false
                        isRefreshing = false // Menandai bahwa operasi refresh telah selesai dengan kesalahan
                    }
                }
                is ResultStory.Loading -> {
                    binding.progressStory.isVisible = it.isLoading
                }
            }
        }

        // Panggil getListStory dengan memberikan token
        token?.let {
            mainViewModel.getListStory(it)
        } ?: run {
            Toast.makeText(this, "Token not found", Toast.LENGTH_SHORT).show()
        }

        // Tambahkan ini untuk menangani tombol tambah cerita
        binding.btnAdd.setOnClickListener {
            startForResultAddStory.launch(Intent(this, AddStoryActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            // Menghapus semua data yang disimpan di SharedPreferences saat logout
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()
            // Mengarahkan pengguna ke halaman LoginActivity setelah logout
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // Menutup MainActivity setelah logout
        }

        // Setup SwipeRefreshLayout
        binding.swipeRefreshLayout.setOnRefreshListener {
            // Mengecek apakah operasi refresh sedang berlangsung
            if (!isRefreshing) {
                isRefreshing = true // Menandai bahwa operasi refresh sedang berlangsung
                val token = sharedPreferences.getString("token", "")
                token?.let {
                    mainViewModel.getListStory(it)
                }
            }
        }

        // Tambahan kode untuk otomatis memperbarui daftar cerita saat Activity dimulai
        binding.swipeRefreshLayout.isRefreshing = true // Aktifkan animasi refresh
        binding.swipeRefreshLayout.postDelayed({
            token?.let {
                mainViewModel.getListStory(it)
            }
            binding.swipeRefreshLayout.isRefreshing = false // Nonaktifkan animasi refresh
        }, 1000)
    }

    private fun navigateToDetail(id: String) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("story_id", id)
        startActivity(intent)
    }
    private val startForResultAddStory = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            // Refresh story list
            val token = sharedPreferences.getString("token", "")
            token?.let {
                mainViewModel.getListStory(it)
            }
        }
    }

}

