package com.example.kurlyflow.hr.worker

import android.content.DialogInterface
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.kurlyflow.databinding.ActivityWorkersignupBinding
import com.example.kurlyflow.hr.worker.request.WorkerSignUpRequest
import com.example.kurlyflow.hr.worker.service.WorkerSignUpService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WorkerSignUpActivity : AppCompatActivity() {
    lateinit var binding: ActivityWorkersignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkersignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonWorkersignupSubmit.setOnClickListener {
            var name = binding.edittextWorkersignupName.text.toString()
            var id = binding.edittextWorkersignupId.text.toString()
            id = PhoneNumberUtils.formatNumber(id, "KR")
            var password = binding.edittextWorkersignupPassword.text.toString()
            var repassword = binding.edittextWorkersignupRepassword.text.toString()

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
                var request = WorkerSignUpRequest(name, id, password)
                WorkerSignUpService.requestWorkerSignUp(request).enqueue(object : Callback<String> {
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