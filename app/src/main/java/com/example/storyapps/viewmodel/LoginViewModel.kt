package com.example.storyapps.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapps.data.network.ApiConfig
import com.example.storyapps.utils.ResultStory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel : ViewModel() {

    val loginResult = MutableLiveData<ResultStory>()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            loginResult.value = ResultStory.Loading(true)

            val response = try {
                withContext(Dispatchers.IO) {
                    ApiConfig.getApiService().login(email, password)
                }
            } catch (e: Exception) {
                Log.e("Error", e.message.toString())
                null
            }

            loginResult.value = ResultStory.Loading(false)

            response?.let {
                // Jika respons tidak null, cek apakah error
                if (!it.error) {
                    // Jika tidak ada kesalahan, kirim daftar cerita ke LoginResponse.Success
                    loginResult.value = ResultStory.Success(it.loginResult)
                } else {
                    // Jika ada kesalahan, kirim pesan kesalahan ke LoginResponse.Error
                    loginResult.value = ResultStory.Error(Exception(it.message))
                }
            } ?: run {
                // Jika respons null, kirim pesan kesalahan ke LoginResponse.Error
                loginResult.value = ResultStory.Error(Exception("Login failed"))
            }
        }
    }
}
