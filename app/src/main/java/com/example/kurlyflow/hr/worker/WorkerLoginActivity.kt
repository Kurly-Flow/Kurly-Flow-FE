package com.example.kurlyflow.hr.worker

import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kurlyflow.databinding.ActivityWorkerrloginBinding
import com.example.kurlyflow.hr.worker.request.WorkerLoginRequest
import com.example.kurlyflow.hr.worker.service.WorkerLoginService
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WorkerLoginActivity : AppCompatActivity() {
    lateinit var binding : ActivityWorkerrloginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkerrloginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textviewWorkerloginJoin.setOnClickListener {
            startActivity(Intent(this, WorkerSignUpActivity::class.java))
        }

        binding.buttonWorkerloginSubmit.setOnClickListener {
            var id = binding.edittextWorkerloginId.text.toString()
            var password = binding.edittextWorkerloginPassword.text.toString()
            id = PhoneNumberUtils.formatNumber(id, "KR")

            var request = WorkerLoginRequest(id, password)
            WorkerLoginService.requestWorkerLogin(request).enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.code() == 200) {
                        Log.d("TAG", response.body().toString())
                        var token = response.body()?.get("accessToken").toString()
                        Log.d("TAG", "before: $token")
                        token = token.substring(1, token.length-1)
                        Log.d("TAG", "after: $token")
                        WorkerLoginSharedPreference.setUserAccessToken(applicationContext, token)
                        Toast.makeText(applicationContext, "로그인에 성공했습니다", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(applicationContext, WorkerMyPageActivity::class.java))
                        finish()
                    }
                    else{
                        Toast.makeText(applicationContext, "아이디 혹은 비밀번호가 틀립니다", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Log.d("TAGe", t.localizedMessage)
                    Toast.makeText(applicationContext, "로그인에 실패했습니다", Toast.LENGTH_SHORT).show()
                }

            })
        }
    }
}