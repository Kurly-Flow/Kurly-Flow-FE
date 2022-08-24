package com.example.kurlyflow.working.packing

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kurlyflow.R
import com.example.kurlyflow.databinding.ActivityCheckpackingBinding
import com.example.kurlyflow.hr.worker.model.MyPageModel
import com.example.kurlyflow.hr.worker.service.WorkerCallService
import com.example.kurlyflow.hr.worker.service.WorkerMyPageService
import com.example.kurlyflow.working.WorkingLoginSharedPreference
import com.example.kurlyflow.working.end.EndLoginActivity
import com.example.kurlyflow.working.packing.model.PackingModel
import com.example.kurlyflow.working.packing.model.PackingProductModel
import com.example.kurlyflow.working.packing.service.PackingService
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckPackingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckpackingBinding
    private lateinit var worker: MyPageModel
    private var invoiceId = ""
    private lateinit var packingModel: PackingModel
    private lateinit var products: ArrayList<PackingProductModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckpackingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadData()
        initListener()
        initToolbar()
    }

    private fun loadData() {
        if (intent.hasExtra("invoiceId"))
            invoiceId = intent.getStringExtra("invoiceId").toString()
        binding.textviewCheckpackingInvoicenumber.text = "송장 번호: $invoiceId"

        WorkerMyPageService.getWorkerMyPage(
            WorkingLoginSharedPreference.getUserAccessToken(
                "packing",
                this
            )
        ).enqueue(object :
            Callback<MyPageModel> {

            override fun onResponse(call: Call<MyPageModel>, response: Response<MyPageModel>) {
                Log.d("TAGm", response.body().toString() + response.code())
                if (response.code() == 200) {
                    worker = response.body()!!
                    binding.textviewCheckpackingName.text = worker.name
                    binding.buttonCheckpackingSubmit.text = "포장 검수 완료 - " + worker.name
                    binding.textviewCheckpackingDetailregion.text =
                        worker.region + " " + worker.detailRegion
                } else {
                    Toast.makeText(applicationContext, "목록 조회를 실패했습니다", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MyPageModel>, t: Throwable) {
                Log.d("TAGme", t.localizedMessage)
                Toast.makeText(applicationContext, "목록 조회를 실패했습니다", Toast.LENGTH_SHORT).show()
            }

        })
        PackingService.getPackingList(
            WorkingLoginSharedPreference.getUserAccessToken(
                "packing",
                this
            ), invoiceId
        ).enqueue(object : Callback<PackingModel> {
            override fun onResponse(
                call: Call<PackingModel>,
                response: Response<PackingModel>
            ) {
                Log.d("TAGpl", response.body().toString() + response.code())
                if (response.code() == 200) {
                    packingModel = response.body()!!
                    products = packingModel.products
                    initRecyclerView()
                }
            }

            override fun onFailure(call: Call<PackingModel>, t: Throwable) {
                Log.d("TAGple", t.localizedMessage)
            }

        })
    }

    private fun initListener() {
        val builder = AlertDialog.Builder(this)
        binding.buttonCheckpackingSubmit.setOnClickListener {
            builder.setMessage("모든 검수가 완료 되었나요?")
                .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, _ ->
                    finish()
                    startActivity(Intent(this, CheckProductActivity::class.java))
                })
                .setNegativeButton("취소", DialogInterface.OnClickListener { dialog, _ ->
                    dialog.cancel()
                })
            builder.show()
        }
    }

    private fun initRecyclerView() {
        binding.recyclerviewCheckpacking.apply {
            layoutManager =
                LinearLayoutManager(context)
            adapter = CheckPackingRecyclerViewAdapter(
                context,
                products
            )
        }
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbarCheckpacking)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarCheckpacking.title = "KurlyFlow"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.packing_toolbar, menu)
        menu?.findItem(R.id.wrong)?.isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val builder = AlertDialog.Builder(this)
        when (item?.itemId) {
            R.id.logout -> {
                builder.setMessage("로그아웃 하시겠습니까?")
                    .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, _ ->
                        Toast.makeText(applicationContext, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                        WorkingLoginSharedPreference.clearUserAccessToken(
                            "packing",
                            applicationContext
                        )
                        startActivity(Intent(applicationContext, PackingLoginActivity::class.java))
                        finish()
                    })
                    .setNegativeButton("취소", DialogInterface.OnClickListener { dialog, _ ->
                        dialog.cancel()
                    })
                builder.show()
                return super.onOptionsItemSelected(item)
            }
            R.id.call -> {
                builder.setMessage("관리자를 호출 하시겠습니까?")
                    .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, _ ->
                        FirebaseApp.initializeApp(this)
                        FirebaseMessaging.getInstance().token.addOnCompleteListener(
                            OnCompleteListener { task ->
                                if (!task.isSuccessful) {
                                    Log.d(
                                        "TAGfcm",
                                        "Fetching FCM registration token failed",
                                        task.exception
                                    )
                                    return@OnCompleteListener
                                }

                                // Get new FCM registration token
                                val token = task.result
                                if (token != null) {
                                    WorkerCallService.requestFcmCall(
                                        WorkingLoginSharedPreference.getUserAccessToken(
                                            "packing",
                                            this
                                        ), token
                                    ).enqueue(object : Callback<String> {
                                        override fun onResponse(
                                            call: Call<String>,
                                            response: Response<String>
                                        ) {
                                            Log.d("TAGc", response.code().toString())
                                            if (response.code() == 200) {
                                                Toast.makeText(
                                                    applicationContext,
                                                    "관리자를 호출 했습니다",
                                                    Toast.LENGTH_SHORT
                                                )
                                                    .show()

                                            } else {
                                                Toast.makeText(
                                                    applicationContext,
                                                    "관리자 호출을 실패했습니다",
                                                    Toast.LENGTH_SHORT
                                                )
                                                    .show()
                                            }
                                        }

                                        override fun onFailure(call: Call<String>, t: Throwable) {
                                            Log.d("TAGc", t.localizedMessage)
                                            Toast.makeText(
                                                applicationContext,
                                                "관리자 호출을 실패했습니다",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                        }

                                    })
                                } else {
                                    Toast.makeText(this, "토큰 발급을 실패했습니다", Toast.LENGTH_SHORT)
                                        .show()
                                }
//                                // Log and toast
//                                val msg = getString(R.string.msg_token_fmt, token)
//                                Log.d("TAGfcm", msg)

                            })
                        dialog.cancel()
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
}