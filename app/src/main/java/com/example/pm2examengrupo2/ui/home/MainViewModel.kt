package com.example.pm2examengrupo2.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pm2examengrupo2.data.model.BasicResponse
import com.example.pm2examengrupo2.data.repository.MainRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch

class MainViewModel(private val repository: MainRepository) : ViewModel() {

    private val _saveResult = MutableLiveData<BasicResponse?>()
    val saveResult: LiveData<BasicResponse?> = _saveResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun saveContact(
        userId: Int,
        name: String,
        phone: String,
        email: String,
        latitude: String,
        longitude: String,
        fotoBase64: String,
        firmaBase64: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Crear contacto en la nube
                val response = repository.createContact(
                    userId, name, phone, email, latitude, longitude, fotoBase64, firmaBase64
                )

                if (response.isSuccessful) {
                    _saveResult.value = response.body()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorResponse = try {
                        Gson().fromJson(errorBody, BasicResponse::class.java)
                    } catch (e: Exception) {
                        null
                    }
                    _errorMessage.value = errorResponse?.message ?: "Error servidor: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de red: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
