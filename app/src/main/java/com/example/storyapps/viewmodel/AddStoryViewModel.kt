package com.example.storyapps.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapps.data.model.AddStoryResponse
import com.example.storyapps.data.network.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryViewModel : ViewModel() {

    val addStoryResult = MutableLiveData<AddStoryResponse>()

    // MutableLiveData untuk memberitahu MainActivity bahwa cerita telah berhasil ditambahkan
    val storyAdded = MutableLiveData<Boolean>()

    fun addStory(token: String, description: String, photoFile: File, lat: Float? = null, lon: Float? = null) {
        val requestFile = photoFile.asRequestBody("image/*".toMediaType())
        val photoPart = MultipartBody.Part.createFormData("photo", photoFile.name, requestFile)
        val descriptionPart = description.toRequestBody("text/plain".toMediaType())

        viewModelScope.launch {
            addStoryResult.value = try {
                withContext(Dispatchers.IO) {
                    ApiConfig.getApiService().addStory(token, descriptionPart, photoPart, lat, lon)
                }
            } catch (e: Exception) {
                AddStoryResponse(true, "Failed to add story")
            }

            // Jika cerita berhasil ditambahkan, set nilai storyAdded menjadi true
            if (!addStoryResult.value?.error!!) {
                storyAdded.value = true
            }
        }
    }
}
