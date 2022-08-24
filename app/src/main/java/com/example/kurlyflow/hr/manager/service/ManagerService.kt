package com.example.kurlyflow.hr.manager.service

import com.example.kurlyflow.ApiClient
import com.example.kurlyflow.hr.manager.model.AttendanceModel
import com.example.kurlyflow.hr.manager.model.HomeWorkerModel
import com.example.kurlyflow.hr.manager.model.RegionModel
import com.example.kurlyflow.hr.manager.request.SaveToRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ManagerService {
    @POST("/api/admins/region")
    fun postChooseRegion(@Header("Authorization") token: String, @Body region: RegionModel): Call<String>

    @GET("/api/admins")
    fun getHomeWorkers(@Header("Authorization") token: String): Call<List<HomeWorkerModel>>

    @POST("/api/admins/to")
    fun postTo(@Header("Authorization") token: String, @Body request: SaveToRequest): Call<String>

    @POST("/api/admins/assignment")
    fun tmp(@Header("Authorization") token: String): Call<String>

    @GET("/api/admins/attendance")
    fun getWorkerAttendance(@Header("Authorization") token: String): Call<ArrayList<AttendanceModel>>

    @POST("/api/admins/detail")
    fun postDetailRegionAssignment(@Header("Authorization") token: String): Call<String>

    companion object {
        fun requestChooseRegion(token: String, region: RegionModel): Call<String> {
            return ApiClient.create(ManagerService::class.java).postChooseRegion(token, region)
        }

        fun getHomeWorkers(token: String): Call<List<HomeWorkerModel>> {
            return ApiClient.create(ManagerService::class.java).getHomeWorkers(token)
        }

        fun saveTo(token: String, request: SaveToRequest): Call<String> {
            return ApiClient.create(ManagerService::class.java).postTo(token, request)
        }

        fun tmpppp(token: String): Call<String> {
            return ApiClient.create(ManagerService::class.java).tmp(token)
        }

        fun getWorkerAttendance(token: String): Call<ArrayList<AttendanceModel>> {
            return ApiClient.create(ManagerService::class.java).getWorkerAttendance(token)
        }

        fun requestDetailRegionAssignment(token: String): Call<String> {
            return ApiClient.create(ManagerService::class.java).postDetailRegionAssignment(token)
        }
    }
}