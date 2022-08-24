package com.example.kurlyflow.hr.manager

import android.content.DialogInterface
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.kurlyflow.databinding.ActivityManagersignupBinding
import com.example.kurlyflow.hr.manager.request.ManagerSignUpRequest
import com.example.kurlyflow.hr.manager.service.ManagerSignUpService
import com.example.kurlyflow.hr.worker.request.WorkerSignUpRequest
import com.example.kurlyflow.hr.worker.service.WorkerSignUpService
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class ManagerSignUpActivity : AppCompatActivity() {
    lateinit var binding: ActivityManagersignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManagersignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonManagersignupSubmit.setOnClickListener {
            var name = binding.edittextManagersignupName.text.toString()
            var id = binding.edittextManagersignupId.text.toString()
            id = PhoneNumberUtils.formatNumber(id, "KR")
            var password = binding.edittextManagersignupPassword.text.toString()
            var repassword = binding.edittextManagersignupRepassword.text.toString()

            val builder = AlertDialog.Builder(this)
            if (name.isBlank() || id.isBlank() || password.isBlank() || repassword.isBlank()) {
                builder.setMessage("빈칸 없이 모두 입력해주세요")
                    .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, _ ->
                        dialog.cancel()
                    })
                builder.show()
            } else if (password != repassword) {
                builder.setMessage("비밀번호 확인이 잘못되었습니다")
                    .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, _ ->
                        dialog.cancel()
                    })
                builder.show()
            } else {
                var request = ManagerSignUpRequest(name, id, password)
                ManagerSignUpService.requestManagerSignUp(request).enqueue(object :
                    retrofit2.Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        if (response.code() == 201) {
                            Toast.makeText(applicationContext, "회원가입을 완료했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Log.d("TAGe", t.localizedMessage)
                        Toast.makeText(applicationContext, "회원가입에 실패했습니다.", Toast.LENGTH_LONG).show()
                    }

                })
                finish()
            }
        }
    }
}