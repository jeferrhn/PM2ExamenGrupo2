package com.example.pm2examengrupo2.data.network

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    // IP local del servidor
    private const val BASE_URL = "http://192.168.125.50/PM2ExamenApi/"

    fun getApiService(): ApiService {
        // Gson flexible para el JSON
        val gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        return retrofit.create(ApiService::class.java)
    }
}
