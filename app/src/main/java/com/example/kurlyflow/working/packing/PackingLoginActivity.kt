package com.example.kurlyflow.working.packing

import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kurlyflow.working.WorkingLoginSharedPreference
import com.example.kurlyflow.databinding.ActivityPackingloginBinding
import com.example.kurlyflow.hr.worker.request.WorkerLoginRequest
import com.example.kurlyflow.working.end.service.EndService
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PackingLoginActivity : AppCompatActivity() {
    lateinit var binding : ActivityPackingloginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPackingloginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonPackingloginSubmit.setOnClickListener {
            var id = binding.edittextPackingloginId.text.toString()
            var password = binding.edittextPackingloginPassword.text.toString()
            id = PhoneNumberUtils.formatNumber(id, "KR")

            var request = WorkerLoginRequest(id, password)
            EndService.requestWorkerLogin(request).enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.code() == 200) {
                        Log.d("TAG", response.body().toString())
                        var token = response.body()?.get("accessToken").toString()
                        Log.d("TAG", "before: $token")
                        token = token.substring(1, token.length-1)
                        Log.d("TAG", "after: $token")
                        WorkingLoginSharedPreference.setUserAccessToken("packing", applicationContext, token)
                        Toast.makeText(applicationContext, "로그인에 성공했습니다", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(applicationContext, CheckProductActivity::class.java))
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