package com.example.pm2examengrupo2.data.model

import com.google.gson.annotations.SerializedName

data class Contact(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("email") val email: String,
    @SerializedName("latitude") val latitude: String? = "0.0",
    @SerializedName("longitude") val longitude: String? = "0.0",
    @SerializedName("photo_path") val photoPath: String? = null,
    @SerializedName("signature_path") val signaturePath: String? = null
)
