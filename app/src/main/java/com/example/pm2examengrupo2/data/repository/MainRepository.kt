package com.example.pm2examengrupo2.data.repository

import com.example.pm2examengrupo2.data.network.ApiService
import com.example.pm2examengrupo2.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainRepository(val apiService: ApiService) {

    suspend fun login(email: String, password: String) = withContext(Dispatchers.IO) {
        apiService.login(LoginRequest(email, password))
    }

    suspend fun register(name: String, email: String, pass: String) = withContext(Dispatchers.IO) {
        apiService.register(RegisterRequest(name, email, pass))
    }

    suspend fun createContact(
        userId: Int,
        name: String,
        phone: String,
        email: String,
        latitude: String,
        longitude: String,
        fotoBase64: String,
        firmaBase64: String
    ) = withContext(Dispatchers.IO) {
        apiService.createContact(ContactCreateRequest(userId, name, phone, email, latitude, longitude, fotoBase64, firmaBase64))
    }

    // Obtener contactos por ID
    suspend fun getContacts(userId: Int) = withContext(Dispatchers.IO) {
        apiService.getContacts(userId)
    }

    suspend fun updateContact(
        id: Int,
        name: String,
        phone: String,
        latitud: String,
        longitud: String,
        fotoBase64: String? = null,
        firmaBase64: String? = null
    ) = withContext(Dispatchers.IO) {
        apiService.updateContact(ContactUpdateRequest(id, name, phone, latitud, longitud, fotoBase64, firmaBase64))
    }

    suspend fun deleteContact(id: Int) = withContext(Dispatchers.IO) {
        apiService.deleteContact(ContactDeleteRequest(id))
    }
}
