package com.example.pm2examengrupo2.ui.list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pm2examengrupo2.data.model.BasicResponse
import com.example.pm2examengrupo2.data.model.Contact
import com.example.pm2examengrupo2.data.repository.MainRepository
import kotlinx.coroutines.launch
import org.json.JSONObject

class ContactsViewModel(private val repository: MainRepository) : ViewModel() {

    private val _contacts = MutableLiveData<List<Contact>>()
    val contacts: LiveData<List<Contact>> = _contacts

    private val _deleteResult = MutableLiveData<BasicResponse?>()
    val deleteResult: LiveData<BasicResponse?> = _deleteResult

    private val _updateResult = MutableLiveData<BasicResponse?>()
    val updateResult: LiveData<BasicResponse?> = _updateResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun fetchContacts(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getContacts(userId)
                if (response.isSuccessful) {
                    _contacts.value = response.body()?.data ?: emptyList()
                } else {
                    _errorMessage.value = "No se pudieron cargar"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de red"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteContact(userId: Int, id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.deleteContact(id)
                if (response.isSuccessful) {
                    _deleteResult.value = response.body()
                    fetchContacts(userId)
                } else {
                    _errorMessage.value = "No se pudo borrar"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Sin conexión"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateContact(userId: Int, id: Int, name: String, phone: String, latitud: String, longitud: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Actualizar sin cambiar fotos
                val response = repository.updateContact(id, name, phone, latitud, longitud, null, null)
                
                if (response.isSuccessful) {
                    _updateResult.value = response.body()
                    fetchContacts(userId)
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
                _errorMessage.value = "Error: ${e.message}"
                Log.e("UPDATE_ERROR", "Error: ", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
