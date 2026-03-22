package com.example.pm2examengrupo2.data.model

import com.google.gson.annotations.SerializedName

data class ContactListResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: List<Contact>? = emptyList()
)
