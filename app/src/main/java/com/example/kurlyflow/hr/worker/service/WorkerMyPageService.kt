package com.example.kurlyflow.hr.worker.service

import com.example.kurlyflow.ApiClient
import com.example.kurlyflow.hr.worker.model.MyPageModel
import com.example.kurlyflow.hr.worker.request.WorkerMyPageRequest
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface WorkerMyPageService {
    @POST("/api/workers")
    fun postWorkerMyPage(
        @Header("Authorization") token: String,
        @Body request: WorkerMyPageRequest
    ): Call<String>

    @GET("/api/workers")
    fun getWorkerRegion(@Header("Authorization") token: String): Call<JsonObject>

    @GET("/api/workers/my")
    fun getWorkerMyPage(@Header("Authorization") token: String): Call<MyPageModel>

    @POST("/api/workers/attendance")
    fun postWorkerAttendance(@Header("Authorization") token: String): Call<String>

    @GET("/api/workers/region")
    fun getWorkerDetailRegion(@Header("Authorization") token: String): Call<JsonObject>

    companion object {
        fun requestWorkerMyPage(token: String, request: WorkerMyPageRequest): Call<String> {
            return ApiClient.create(WorkerMyPageService::class.java)
                .postWorkerMyPage(token, request)
        }

        fun getWorkerRegion(token: String): Call<JsonObject> {
            return ApiClient.create(WorkerMyPageService::class.java).getWorkerRegion(token)
        }

        fun getWorkerMyPage(token: String): Call<MyPageModel> {
            return ApiClient.create(WorkerMyPageService::class.java).getWorkerMyPage(token)
        }

        fun requestWorkerAttendance(token: String): Call<String> {
            return ApiClient.create(WorkerMyPageService::class.java).postWorkerAttendance(token)
        }

        fun getWorkerDetailRegion(token: String): Call<JsonObject> {
            return ApiClient.create(WorkerMyPageService::class.java).getWorkerDetailRegion(token)
        }
    }
}