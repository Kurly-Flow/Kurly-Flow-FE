package com.example.kurlyflow.working.picking.service

import com.example.kurlyflow.ApiClient
import com.example.kurlyflow.hr.worker.request.WorkerLoginRequest
import com.example.kurlyflow.working.picking.model.PickingModel
import com.example.kurlyflow.working.picking.model.ToteModel
import com.example.kurlyflow.working.picking.request.MoveProductsToNewToteRequest
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface PickingService {
    @POST("/api/picking/login")
    fun postPickingLogin(@Body request: WorkerLoginRequest): Call<JsonObject>

    @GET("/api/picking/multi")
    fun getMultiPickingList(@Header("Authorization") token: String): Call<PickingModel>

    @POST("/api/picking/tote")
    fun postNewToteId(@Header("Authorization") token: String, @Body toteId: ToteModel): Call<String>

    @POST("/api/picking/barcode/{invoiceProductId}")
    fun postBarcode(
        @Header("Authorization") token: String,
        @Path("invoiceProductId") invoiceProductId: String,
        @Query("toteId") toteId: String
    ): Call<String>

    @PUT("/api/picking/tote")
    fun moveProductsToNewToteRequest(
        @Header("Authorization") token: String,
        @Body request: MoveProductsToNewToteRequest
    ): Call<String>

    companion object {
        fun requestPickingLogin(request: WorkerLoginRequest): Call<JsonObject> {
            return ApiClient.create(PickingService::class.java).postPickingLogin(request)
        }

        fun getMultiPickingList(token: String): Call<PickingModel> {
            return ApiClient.create(PickingService::class.java).getMultiPickingList(token)
        }

        fun saveNewToteId(token: String, toteId: ToteModel): Call<String> {
            return ApiClient.create(PickingService::class.java).postNewToteId(token, toteId)
        }

        fun saveBarcode(token: String, invoiceProductId: String, toteId: String): Call<String> {
            return ApiClient.create(PickingService::class.java)
                .postBarcode(token, invoiceProductId, toteId)
        }

        fun moveProductsToNewToteRequest(
            token: String,
            request: MoveProductsToNewToteRequest
        ): Call<String> {
            return ApiClient.create(PickingService::class.java)
                .moveProductsToNewToteRequest(token, request)
        }
    }
}