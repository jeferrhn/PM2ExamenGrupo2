package com.example.pm2examengrupo2.data.model

import com.google.gson.annotations.SerializedName

data class ContactUpdateRequest(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("latitud") val latitud: String,
    @SerializedName("longitud") val longitud: String,
    @SerializedName("photo_base64") val photoBase64: String? = null,
    @SerializedName("signature_base64") val signatureBase64: String? = null
)
