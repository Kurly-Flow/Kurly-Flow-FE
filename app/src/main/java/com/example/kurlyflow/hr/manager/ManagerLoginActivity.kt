package com.example.kurlyflow.hr.manager

import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kurlyflow.databinding.ActivityManagerloginBinding
import com.example.kurlyflow.hr.manager.request.ManagerLoginRequest
import com.example.kurlyflow.hr.manager.service.ManagerLoginService
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManagerLoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityManagerloginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManagerloginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textviewManagerloginJoin.setOnClickListener {
            startActivity(Intent(this, ManagerSignUpActivity::class.java))
        }

        binding.buttonManagerloginSubmit.setOnClickListener {
            var id = binding.edittextManagerloginId.text.toString()
            var password = binding.edittextManagerloginPassword.text.toString()
            id = PhoneNumberUtils.formatNumber(id, "KR")

            var request = ManagerLoginRequest(id, password)
            ManagerLoginService.requestManagerLogin(request).enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    Log.d("TAGl", response.body().toString())
                    if (response.code() == 200) {
                        Log.d("TAG", response.body().toString())
                        var token = response.body()?.get("accessToken").toString()
                        Log.d("TAG", "before: $token")
                        token = token.substring(1, token.length-1)
                        Log.d("TAG", "after: $token")
                        ManagerLoginSharedPreference.setUserAccessToken(applicationContext, token)
                        var name = response.body()?.get("name").toString()
                        Log.d("TAG", "before: $name")
                        name = name.substring(1, name.length-1)
                        Log.d("TAG", "after: $name")
                        ManagerLoginSharedPreference.setUserName(applicationContext, name)

                        Toast.makeText(applicationContext, "로그인에 성공했습니다", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(applicationContext, ChooseRegionActivity::class.java))
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