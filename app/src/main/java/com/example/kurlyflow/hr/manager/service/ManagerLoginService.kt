package com.example.kurlyflow.hr.manager.service

import com.example.kurlyflow.ApiClient
import com.example.kurlyflow.hr.manager.request.ManagerLoginRequest
import com.example.kurlyflow.hr.worker.request.WorkerLoginRequest
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ManagerLoginService {
    @POST("/api/admins/login")
    fun postManagerLogin(@Body request: ManagerLoginRequest): Call<JsonObject>

    companion object {
        fun requestManagerLogin(request: ManagerLoginRequest): Call<JsonObject> {
            return ApiClient.create(ManagerLoginService::class.java).postManagerLogin(request)
        }
    }
}