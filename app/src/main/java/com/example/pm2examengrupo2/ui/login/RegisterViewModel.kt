package com.example.pm2examengrupo2.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pm2examengrupo2.data.model.BasicResponse
import com.example.pm2examengrupo2.data.model.RegisterRequest
import com.example.pm2examengrupo2.data.repository.MainRepository
import kotlinx.coroutines.launch
import org.json.JSONObject

class RegisterViewModel(private val repository: MainRepository) : ViewModel() {

    private val _registerResult = MutableLiveData<BasicResponse?>()
    val registerResult: LiveData<BasicResponse?> = _registerResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun register(name: String, email: String, pass: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.apiService.register(RegisterRequest(name, email, pass))
                if (response.isSuccessful) {
                    _registerResult.value = response.body()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val message = try {
                        JSONObject(errorBody ?: "").getString("message")
                    } catch (e: Exception) {
                        "Error del servidor (${response.code()})"
                    }
                    _errorMessage.value = message
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de conexión: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
