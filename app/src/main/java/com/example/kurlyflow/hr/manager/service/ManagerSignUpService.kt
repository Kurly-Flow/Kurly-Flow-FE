package com.example.kurlyflow.hr.manager.service

import com.example.kurlyflow.ApiClient
import com.example.kurlyflow.hr.manager.request.ManagerSignUpRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ManagerSignUpService {
    @POST("/api/admins/signup")
    fun postManagerSignUp(@Body request: ManagerSignUpRequest): Call<String>

    companion object {
        fun requestManagerSignUp(request: ManagerSignUpRequest): Call<String> {
            return ApiClient.create(ManagerSignUpService::class.java).postManagerSignUp(request)
        }
    }
}