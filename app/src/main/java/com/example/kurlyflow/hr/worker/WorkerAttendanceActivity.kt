package com.example.kurlyflow.hr.worker

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.kurlyflow.R
import com.example.kurlyflow.databinding.ActivityWorkerattendanceBinding
import com.example.kurlyflow.hr.worker.model.MyPageModel
import com.example.kurlyflow.hr.worker.service.WorkerMyPageService
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WorkerAttendanceActivity : AppCompatActivity() {
    lateinit var binding: ActivityWorkerattendanceBinding
    lateinit var data: MyPageModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkerattendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadData()
        initListener()
        initToolbar()
    }

    private fun loadData() {
        WorkerMyPageService.getWorkerMyPage(WorkerLoginSharedPreference.getUserAccessToken(this))
            .enqueue(object : Callback<MyPageModel> {
                override fun onResponse(call: Call<MyPageModel>, response: Response<MyPageModel>) {
                    Log.d("TAGd", response.body().toString() + response.code())
                    if (response.code() == 200) {
                        data = response.body()!!
                        binding.textviewWorkerattendanceName.text = data.name
                        binding.textviewWorkerattendancePhone.text = data.phone
                        binding.textviewWorkerattendanceRegion.text = "확정 근무지 - " + data.region
                        when {
                            data.detailRegion.isNullOrEmpty() -> {
                                binding.textviewWorkerattendanceDetailregion.text = "세부 권역 - 미확정"
                            }
                            else -> {
                                binding.textviewWorkerattendanceDetailregion.text =
                                    "세부 권역 - " + data.detailRegion
                            }
                        }
                        if (data.isAttended)
                            binding.textviewWorkerattendanceAttendance.text = "출결"
                        else
                            binding.textviewWorkerattendanceAttendance.text = "미출결"
                    }
                }

                override fun onFailure(call: Call<MyPageModel>, t: Throwable) {
                    Log.d("TAGde", t.localizedMessage)
                }

            })
    }

    private fun initListener() {
        binding.imageviewWorkerattendanceQrcode.setOnClickListener {
            Log.d("TAGqr", "qr")
            WorkerMyPageService.requestWorkerAttendance(
                WorkerLoginSharedPreference.getUserAccessToken(
                    this
                )
            ).enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    Log.d("TAGqr", response.body() + response.code())
                    if (response.code() == 200) {
                        val builder = AlertDialog.Builder(this@WorkerAttendanceActivity)
                        builder.setMessage("출결하셨습니다.\n오늘 근무도 안전하게 화이팅하세요!")
                            .setPositiveButton(
                                "확인",
                                DialogInterface.OnClickListener { dialog, _ ->
                                    refresh()
                                })
                        builder.show()
                    }
                    else{
                        Toast.makeText(applicationContext, "이미 출결하셨습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d("TAGqr", t.localizedMessage)
                    Toast.makeText(applicationContext, "출결에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            })
        }

        binding.buttonWorkerattendanceCheck.setOnClickListener {
            WorkerMyPageService.getWorkerDetailRegion(WorkerLoginSharedPreference.getUserAccessToken(this))
                .enqueue(object : Callback<JsonObject> {
                    override fun onResponse(
                        call: Call<JsonObject>,
                        response: Response<JsonObject>
                    ) {
                        Log.d("TAGc", response.body().toString() + response.code())
                        if (response.code() == 200) {
                            var detailRegion = response.body()?.get("detailRegion").toString()
                            Log.d("TAGc", detailRegion)
                            if (detailRegion == "null") {
                                val builder = AlertDialog.Builder(this@WorkerAttendanceActivity)
                                builder.setMessage("아직 확정되기 전입니다.\n잠시 후에 다시 시도해주세요.")
                                    .setPositiveButton(
                                        "확인",
                                        DialogInterface.OnClickListener { dialog, _ ->
                                            dialog.cancel()
                                        })
                                builder.show()
                            } else {
                                detailRegion = detailRegion.substring(1, detailRegion.length - 1)
                                val builder = AlertDialog.Builder(this@WorkerAttendanceActivity)
                                builder.setMessage("세부권역이 [$detailRegion](으)로 확정되었습니다.")
                                    .setPositiveButton(
                                        "확인",
                                        DialogInterface.OnClickListener { dialog, _ ->
                                            refresh()
                                        })
                                builder.show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        Log.d("TAGce", t.localizedMessage)
                    }


                })
        }
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbarWorkerattendance)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarWorkerattendance.title = "KurlyFlow"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.worker_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.logout -> {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("로그아웃 하시겠습니까?")
                    .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, _ ->
                        Toast.makeText(applicationContext, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                        WorkerLoginSharedPreference.clearUserAccessToken(applicationContext)
                        startActivity(Intent(applicationContext, WorkerLoginActivity::class.java))
                        finish()
                    })
                    .setNegativeButton("취소", DialogInterface.OnClickListener { dialog, _ ->
                        dialog.cancel()
                    })
                builder.show()
                return super.onOptionsItemSelected(item)
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun refresh() {
        val intent = intent
        finish() //현재 액티비티 종료 실시
        overridePendingTransition(0, 0) //인텐트 애니메이션 없애기
        startActivity(intent) //현재 액티비티 재실행 실시
        overridePendingTransition(0, 0)
    }

}
