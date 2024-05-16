
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapps.data.model.RegisterResponse
import com.example.storyapps.data.network.ApiConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterViewModel : ViewModel() {

    fun register(name: String, email: String, password: String, onResult: (RegisterResponse?) -> Unit) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    ApiConfig.getApiService().register(name, email, password)
                }
                onResult(response)
            } catch (e: Exception) {
                onResult(null)
            }
        }
    }
}
