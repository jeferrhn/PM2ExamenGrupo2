package com.example.pm2examengrupo2.data.network

import com.example.pm2examengrupo2.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("login.php")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("register_user.php")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<BasicResponse>

    @POST("create_contact.php")
    suspend fun createContact(
        @Body request: ContactCreateRequest
    ): Response<BasicResponse>

    @GET("get_contacts.php")
    suspend fun getContacts(
        @Query("user_id") userId: Int
    ): Response<ContactListResponse>

    @POST("update_contact.php")
    suspend fun updateContact(
        @Body request: ContactUpdateRequest
    ): Response<BasicResponse>

    @POST("delete_contact.php")
    suspend fun deleteContact(
        @Body request: ContactDeleteRequest
    ): Response<BasicResponse>
}
