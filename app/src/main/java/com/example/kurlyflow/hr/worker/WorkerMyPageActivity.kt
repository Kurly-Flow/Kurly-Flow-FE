package com.example.kurlyflow.hr.worker

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.kurlyflow.R
import com.example.kurlyflow.databinding.ActivityMypageBinding
import com.example.kurlyflow.hr.worker.model.MyPageModel
import com.example.kurlyflow.hr.worker.request.WorkerMyPageRequest
import com.example.kurlyflow.hr.worker.service.WorkerMyPageService
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WorkerMyPageActivity : AppCompatActivity() {
    lateinit var binding: ActivityMypageBinding
    lateinit var data: MyPageModel
    var selectedSpinner = "선택"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadData()
        initSpinner()
        initListener()
        initToolbar()
    }

    private fun loadData() {
        WorkerMyPageService.getWorkerMyPage(WorkerLoginSharedPreference.getUserAccessToken(this))
            .enqueue(object : Callback<MyPageModel> {
                override fun onResponse(call: Call<MyPageModel>, response: Response<MyPageModel>) {
                    Log.d("TAGm", response.body().toString() + response.code())
                    if (response.code() == 200) {
                        data = response.body()!!
                        binding.textviewToName.text = data.name
                        binding.textviewToPhone.text = data.phone
                        if (!data.employeeNumber.isNullOrEmpty())
                            binding.textviewToId.text = "사번 - " + data.employeeNumber
                        else
                            binding.textviewToId.text = "사번 - 미입력"
                        if (data.wishRegion != "UNASSIGNED")
                            binding.textviewToRegion.text = "희망 근무지 - " + data.wishRegion
                    } else {
                        Toast.makeText(applicationContext, "내 정보 조회에 실패했습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<MyPageModel>, t: Throwable) {
                    Log.d("TAGe", t.localizedMessage)
                    Toast.makeText(applicationContext, "내 정보 조회에 실패했습니다.", Toast.LENGTH_SHORT)
                        .show()
                }

            })
    }

    private fun initSpinner() {
        val spinnerList = listOf(
            "선택",
            "PICKING",
            "QPS",
            "END",
            "PACKING"
        )
        val adapter = ArrayAdapter(
            this,
            com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
            spinnerList
        )
        binding.spinnerMypageRegion.adapter = adapter
        binding.spinnerMypageRegion.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedSpinner =
                        binding.spinnerMypageRegion.getItemAtPosition(position).toString()

                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

            }
    }

    private fun initListener() {
        binding.buttonToSubmit.setOnClickListener {
            if (selectedSpinner != "선택" && binding.edittextToId.text.isNotBlank()) {
                var employeeNumber = binding.edittextToId.text.toString()
                var region = selectedSpinner
                Log.d(
                    "TAGi",
                    WorkerLoginSharedPreference.getUserAccessToken(applicationContext) + employeeNumber + region
                )
                var request = WorkerMyPageRequest(employeeNumber, region)
                WorkerMyPageService.requestWorkerMyPage(
                    WorkerLoginSharedPreference.getUserAccessToken(
                        applicationContext
                    ),
                    request
                ).enqueue(object :
                    Callback<String> {
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        if (response.code() == 200) {
                            val builder = AlertDialog.Builder(this@WorkerMyPageActivity)
                            builder.setMessage("저장되었습니다.")
                                .setPositiveButton(
                                    "확인",
                                    DialogInterface.OnClickListener { dialog, _ ->
                                        refresh()
                                    })
                            builder.show()
                        }
                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Log.d("TAG", t.localizedMessage)
                        val builder = AlertDialog.Builder(this@WorkerMyPageActivity)
                        builder.setMessage("저장에 실패했습니다.")
                            .setPositiveButton(
                                "확인",
                                DialogInterface.OnClickListener { dialog, _ ->
                                    dialog.cancel()
                                })
                        builder.show()
                    }

                })
            } else {
                if (binding.edittextToId.text.isBlank()) {
                    Toast.makeText(applicationContext, "사번을 정확히 입력해주세요", Toast.LENGTH_SHORT).show()
                } else if (selectedSpinner == "선택") {
                    Toast.makeText(applicationContext, "희망 근무지를 선택해주세요", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.buttonToCheck.setOnClickListener {
            WorkerMyPageService.getWorkerRegion(WorkerLoginSharedPreference.getUserAccessToken(this))
                .enqueue(object : Callback<JsonObject> {
                    override fun onResponse(
                        call: Call<JsonObject>,
                        response: Response<JsonObject>
                    ) {
                        Log.d("TAG", response.body().toString())
                        if (response.code() == 200) {
                            var region = response.body()?.get("region").toString()
                            region = region.substring(1, region.length - 1)
                            if (region == "UNASSIGNED") {
                                val builder = AlertDialog.Builder(this@WorkerMyPageActivity)
                                builder.setMessage("아직 확정되기 전입니다.\n잠시 후에 다시 시도해주세요.")
                                    .setPositiveButton(
                                        "확인",
                                        DialogInterface.OnClickListener { dialog, _ ->
                                            dialog.cancel()
                                            startActivity(
                                                Intent(
                                                    applicationContext,
                                                    WorkerAttendanceActivity::class.java
                                                )
                                            )
                                        })
                                builder.show()
                            } else {
                                startActivity(
                                    Intent(
                                        applicationContext,
                                        WorkerAttendanceActivity::class.java
                                    )
                                )
                            }
                        } else {
                            val builder = AlertDialog.Builder(this@WorkerMyPageActivity)
                            builder.setMessage("사번과 희망 근무지를 입력한 후 확인해주세요.")
                                .setPositiveButton(
                                    "확인",
                                    DialogInterface.OnClickListener { dialog, _ ->
                                        dialog.cancel()
                                    })
                            builder.show()
                        }
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        Log.d("TAGe", t.localizedMessage)
                    }
                })
        }
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbarMypage)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarMypage.title = "KurlyFlow"
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
