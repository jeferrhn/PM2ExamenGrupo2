package com.example.pm2examengrupo2.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pm2examengrupo2.data.model.LoginResponse
import com.example.pm2examengrupo2.data.repository.MainRepository
import kotlinx.coroutines.launch
import org.json.JSONObject

class LoginViewModel(private val repository: MainRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<LoginResponse?>()
    val loginResult: LiveData<LoginResponse?> = _loginResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _errorMessage.value = "Campos obligatorios"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.login(email, password)
                if (response.isSuccessful) {
                    _loginResult.value = response.body()
                } else {
                    val errorJson = response.errorBody()?.string() ?: ""
                    val message = try {
                        val obj = JSONObject(errorJson)
                        // Obtener error del servidor
                        if (obj.has("error")) obj.getString("error") else obj.getString("message")
                    } catch (e: Exception) {
                        "Error: ${response.code()}"
                    }
                    _errorMessage.value = message
                }
            } catch (e: Exception) {
                _errorMessage.value = "Sin conexión"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
