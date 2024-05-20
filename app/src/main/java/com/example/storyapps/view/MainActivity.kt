package com.example.storyapps.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapps.R
import com.example.storyapps.adapter.AdapterStory
import com.example.storyapps.adapter.LoadStateAdapter
import com.example.storyapps.data.database.StoryDatabase
import com.example.storyapps.data.database.StoryRepository
import com.example.storyapps.data.network.ApiConfig
import com.example.storyapps.databinding.ActivityMainBinding
import com.example.storyapps.viewmodel.MainViewModel
import com.example.storyapps.viewmodel.factory.MainViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences

    private val mainViewModel by viewModels<MainViewModel> {
        MainViewModelFactory(
            StoryRepository(
                ApiConfig.getApiService(),
                StoryDatabase.getDatabase(this),
                sharedPreferences.getString("token", "") ?: ""
            )
        )
    }

    private val adapter by lazy {
        AdapterStory(
            { item -> navigateToDetail(item.id) }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        val username = sharedPreferences.getString("username", "")
        binding.txtUsername.text = username

        binding.recylerViewMain.layoutManager = LinearLayoutManager(this)
        binding.recylerViewMain.setHasFixedSize(true)
        binding.recylerViewMain.adapter = adapter.withLoadStateHeaderAndFooter(
            header = LoadStateAdapter { adapter.retry() }, // Retry function untuk header
            footer = LoadStateAdapter { adapter.retry() }  // Retry function untuk footer
        )

        lifecycleScope.launch {
            mainViewModel.getStories().collectLatest {
                adapter.submitData(it)
                // Hentikan animasi swipe refresh setelah pengambilan data selesai
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            adapter.refresh()
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                // Tampilkan loading ketika memuat data
                binding.progressStory.isVisible = loadStates.refresh is LoadState.Loading
                // Tampilkan pesan informasi ketika tidak ada data
                binding.emptyView.isVisible = loadStates.refresh is LoadState.NotLoading && adapter.itemCount == 0
            }
        }

    }

    private fun navigateToDetail(id: String) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("story_id", id)
        startActivity(intent)
    }

    private val startForResultAddStory =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                adapter.refresh()
            }
        }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                val editor = sharedPreferences.edit()
                editor.clear()
                editor.apply()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
            R.id.add -> {
                startForResultAddStory.launch(Intent(this, AddStoryActivity::class.java))
            }
            R.id.maps -> {
                startActivity(Intent(this, MapsActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
