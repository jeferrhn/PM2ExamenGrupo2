package com.example.pm2examengrupo2.data.model

import com.google.gson.annotations.SerializedName

data class ContactCreateRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("email") val email: String,
    @SerializedName("latitude") val latitude: String,
    @SerializedName("longitude") val longitude: String,
    @SerializedName("photo_base64") val photoBase64: String,
    @SerializedName("signature_base64") val signatureBase64: String
)
