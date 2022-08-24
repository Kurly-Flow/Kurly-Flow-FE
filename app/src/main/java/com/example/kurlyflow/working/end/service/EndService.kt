package com.example.kurlyflow.working.end.service

import com.example.kurlyflow.ApiClient
import com.example.kurlyflow.hr.worker.request.WorkerLoginRequest
import com.example.kurlyflow.working.end.model.BasketModel
import com.example.kurlyflow.working.end.model.EndProductModel
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface EndService {
    @POST("/api/end/login")
    fun postWorkerLogin(@Body request: WorkerLoginRequest): Call<JsonObject>

    @GET("/api/end")
    fun getBasketList(@Header("Authorization") token: String): Call<List<BasketModel>>

    @GET("/api/end/{invoice_id}")
    fun getProductList(
        @Header("Authorization") token: String,
        @Path("invoice_id") invoiceId: String
    ): Call<ArrayList<EndProductModel>>

    companion object {
        fun requestWorkerLogin(request: WorkerLoginRequest): Call<JsonObject> {
            return ApiClient.create(EndService::class.java).postWorkerLogin(request)
        }

        fun getBasketList(token: String): Call<List<BasketModel>> {
            return ApiClient.create(EndService::class.java).getBasketList(token)
        }

        fun getProductList(token: String, invoiceId: String): Call<ArrayList<EndProductModel>> {
            return ApiClient.create(EndService::class.java).getProductList(token, invoiceId)
        }
    }
}