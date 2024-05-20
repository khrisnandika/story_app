package com.example.storyapps.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapps.data.network.ApiConfig
import com.example.storyapps.utils.ResultStory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapsViewModel : ViewModel() {

    val resultMaps = MutableLiveData<ResultStory>()

    fun getMapsStory(token: String) {
        viewModelScope.launch {
            resultMaps.value = ResultStory.Loading(true)
            val response = try {
                withContext(Dispatchers.IO) {
                    ApiConfig.getApiService().getMaps("Bearer $token")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

            resultMaps.value = ResultStory.Loading(false)

            response?.let {
                if (!it.error) {
                    resultMaps.value = ResultStory.Success(it)
                } else {
                    resultMaps.value = ResultStory.Error(Exception(it.message))
                }
            } ?: run {
                resultMaps.value = ResultStory.Error(Exception("Failed to fetch maps stories"))
            }
        }
    }
}
