package com.example.kurlyflow

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters

class MyWorker(appContext: Context, workerParams:WorkerParameters): Worker(appContext, workerParams) {
    override fun doWork(): Result {
        return ListenableWorker.Result.success()
    }
}