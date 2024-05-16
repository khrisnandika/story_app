import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapps.data.network.ApiConfig
import com.example.storyapps.utils.ResultStory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// DetailViewModel.kt
class DetailViewModel : ViewModel() {

    val resultDetailStory = MutableLiveData<ResultStory>()

    fun getStoryDetail(token: String, id: String) {
        viewModelScope.launch {
            resultDetailStory.value = ResultStory.Loading(true)

            val response = try {
                withContext(Dispatchers.IO) {
                    ApiConfig.getApiService().getStoryDetail("Bearer $token", id)
                }
            } catch (e: Exception) {
                Log.e("Error", e.message.toString())
                null
            }

            resultDetailStory.value = ResultStory.Loading(false)

            response?.let {
                if (!it.error && it.story != null) {
                    resultDetailStory.value = ResultStory.Success(it.story)
                } else {
                    resultDetailStory.value = ResultStory.Error(Exception("Detail story not found"))
                }
            } ?: run {
                resultDetailStory.value = ResultStory.Error(Exception("Failed to fetch detail story"))
            }
        }
    }
}

