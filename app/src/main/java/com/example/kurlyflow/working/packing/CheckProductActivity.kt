package com.example.kurlyflow.working.packing

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kurlyflow.R
import com.example.kurlyflow.databinding.ActivityCheckproductBinding
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

class CheckProductActivity : AppCompatActivity() {
    lateinit var binding: ActivityCheckproductBinding
    lateinit var products: ArrayList<PackingProductModel>
    lateinit var packingModel: PackingModel
    lateinit var worker: MyPageModel
    private var invoiceId = "123-434-545"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckproductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadData()
        initListener()
        initToolbar()
    }

    private fun loadData() {
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
                    binding.textviewCheckproductName.text = worker.name
                    binding.buttonCheckproductSubmit.text = "품목 검수 완료 - " + worker.name
                    binding.textviewCheckproductDetailregion.text =
                        worker.region + " " + worker.detailRegion
                } else {
                    Toast.makeText(applicationContext, "작업자 정보 조회를 실패했습니다", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<MyPageModel>, t: Throwable) {
                Log.d("TAGme", t.localizedMessage)
                Toast.makeText(applicationContext, "작업자 정보 조회를 실패했습니다", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun initListener() {
        binding.linearlayoutCheckproductQr.setOnClickListener {
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
                        binding.textviewCheckproductInvoicenumber.text =
                            "송장 번호: " + packingModel.id

                        products = arrayListOf()
                        products = packingModel.products
                        initRecyclerView()
                    } else {
                        Toast.makeText(applicationContext, "목록 조회를 실패했습니다", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<PackingModel>, t: Throwable) {
                    Log.d("TAGple", t.localizedMessage)
                    Toast.makeText(applicationContext, "목록 조회를 실패했습니다", Toast.LENGTH_SHORT).show()
                }

            })
            scanQr()
        }

        binding.buttonCheckproductSubmit.setOnClickListener {
            if (binding.linearlayoutCheckproductInvoice.visibility == View.GONE) {
                Toast.makeText(this, "송장의 QR을 먼저 스캔하세요", Toast.LENGTH_LONG).show()
            } else {
                val intent = Intent(this, CheckPackingActivity::class.java)
                intent.putExtra("invoiceId", invoiceId)
                finish()
                startActivity(intent)
            }
        }
    }

    private fun scanQr() {
        binding.linearlayoutCheckproductQr.visibility = View.GONE
        binding.linearlayoutCheckproductInvoice.visibility = View.VISIBLE
    }

    private fun readyToScan() {
        binding.linearlayoutCheckproductQr.visibility = View.VISIBLE
        binding.linearlayoutCheckproductInvoice.visibility = View.GONE
    }

    private fun initRecyclerView() {
        binding.recyclerviewCheckproduct.apply {
            layoutManager =
                LinearLayoutManager(context)
            adapter = CheckProductRecyclerViewAdapter(
                context,
                products
            )
        }
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbarCheckproduct)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarCheckproduct.title = "KurlyFlow"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.packing_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val builder = AlertDialog.Builder(this)
        when (item?.itemId) {
            R.id.logout -> {
                builder.setMessage("로그아웃 하시겠습니까?")
                    .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, _ ->
                        Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                        WorkingLoginSharedPreference.clearUserAccessToken("packing", this)
                        startActivity(Intent(this, PackingLoginActivity::class.java))
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
            R.id.wrong -> {
                builder.setMessage("품목의 수량이나 이름이 불일치 합니까?")
                    .setPositiveButton("네", DialogInterface.OnClickListener { dialog, _ ->
                        PackingService.saveWrongProduct(
                            WorkingLoginSharedPreference.getUserAccessToken(
                                "packing",
                                this
                            ), invoiceId
                        ).enqueue(object : Callback<String> {
                            override fun onResponse(
                                call: Call<String>,
                                response: Response<String>
                            ) {
                                Log.d("TAGw", response.body() + response.code())
                                if (response.code() == 200) {
                                    Toast.makeText(
                                        applicationContext,
                                        "메인 데스크로 옮겨주세요.",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    readyToScan()
                                }
                                dialog.cancel()
                            }

                            override fun onFailure(call: Call<String>, t: Throwable) {
                                Log.d("TAGwe", t.localizedMessage)
                                dialog.cancel()
                            }

                        })
                    })
                    .setNegativeButton("아니오", DialogInterface.OnClickListener { dialog, _ ->
                        dialog.cancel()
                    })
                builder.show()
                return super.onOptionsItemSelected(item)
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}