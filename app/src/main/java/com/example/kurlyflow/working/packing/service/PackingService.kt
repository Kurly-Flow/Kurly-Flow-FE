package com.example.kurlyflow.working.packing.service

import com.example.kurlyflow.ApiClient
import com.example.kurlyflow.hr.worker.request.WorkerLoginRequest
import com.example.kurlyflow.working.packing.model.PackingModel
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface PackingService {
    @POST("/api/packing/login")
    fun postWorkerLogin(@Body request: WorkerLoginRequest): Call<JsonObject>

    @GET("/api/packing/{invoice_id}")
    fun getPackingList(
        @Header("Authorization") token: String,
        @Path("invoice_id") invoiceId: String
    ): Call<PackingModel>

    @POST("/api/packing/{invoice_id}")
    fun postWrongProduct(
        @Header("Authorization") token: String,
        @Path("invoice_id") invoiceId: String
    ): Call<String>

    companion object {
        fun requestWorkerLogin(request: WorkerLoginRequest): Call<JsonObject> {
            return ApiClient.create(PackingService::class.java).postWorkerLogin(request)
        }

        fun getPackingList(token: String, invoiceId: String): Call<PackingModel> {
            return ApiClient.create(PackingService::class.java).getPackingList(token, invoiceId)
        }

        fun saveWrongProduct(token: String, invoiceId: String): Call<String> {
            return ApiClient.create(PackingService::class.java).postWrongProduct(token, invoiceId)
        }
    }
}