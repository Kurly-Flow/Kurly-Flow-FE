package com.example.kurlyflow.hr.worker.service

import com.example.kurlyflow.ApiClient
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.POST

interface WorkerCallService {
    @POST("/api/workers/call")
    fun requestFcmCall(
        @Header("Authorization") loginToken: String,
        @Header("targetToken") fcmToken: String
    ): Call<String>

    companion object {
        fun requestFcmCall(loginToken: String, fcmToken: String): Call<String> {
            return ApiClient.create(WorkerCallService::class.java)
                .requestFcmCall(loginToken, fcmToken)
        }
    }
}