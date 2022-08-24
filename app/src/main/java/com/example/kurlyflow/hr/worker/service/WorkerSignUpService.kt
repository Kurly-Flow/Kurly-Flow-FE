package com.example.kurlyflow.hr.worker.service

import com.example.kurlyflow.ApiClient
import com.example.kurlyflow.hr.worker.request.WorkerSignUpRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface WorkerSignUpService {
    @POST("/api/workers/signup")
    fun postWorkerSignUp(@Body request: WorkerSignUpRequest): Call<String>

    companion object{
        fun requestWorkerSignUp(request: WorkerSignUpRequest): Call<String> {
            return ApiClient.create(WorkerSignUpService::class.java).postWorkerSignUp(request)
        }
    }
}