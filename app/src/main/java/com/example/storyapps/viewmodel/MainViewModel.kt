package com.example.storyapps.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapps.data.network.ApiConfig
import com.example.storyapps.utils.ResultStory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {

    val resultStory = MutableLiveData<ResultStory>()
    val storyAdded = MutableLiveData<Boolean>()

    fun getListStory(token: String) {
        viewModelScope.launch {
            resultStory.value = ResultStory.Loading(true)

            val response = try {
                withContext(Dispatchers.IO) {
                    ApiConfig.getApiService().getStories("Bearer $token")
                }
            } catch (e: Exception) {
                null
            }

            resultStory.value = ResultStory.Loading(false)

            response?.let {
                if (!it.error) {
                    resultStory.value = ResultStory.Success(it.listStory)
                } else {
                    resultStory.value = ResultStory.Error(Exception(it.message))
                }
            } ?: run {
                resultStory.value = ResultStory.Error(Exception("Failed to fetch stories"))
            }
        }
    }
}
