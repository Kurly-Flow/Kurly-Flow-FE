package com.example.kurlyflow.hr.worker.service

import com.example.kurlyflow.ApiClient
import com.example.kurlyflow.hr.worker.request.WorkerLoginRequest
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface WorkerLoginService {
    @POST("/api/workers/login")
    fun postWorkerLogin(@Body request: WorkerLoginRequest): Call<JsonObject>

    companion object{
        fun requestWorkerLogin(request: WorkerLoginRequest): Call<JsonObject> {
            return ApiClient.create(WorkerLoginService::class.java).postWorkerLogin(request)
        }
    }
}