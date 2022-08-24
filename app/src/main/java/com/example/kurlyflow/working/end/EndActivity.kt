package com.example.kurlyflow.working.end

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kurlyflow.R
import com.example.kurlyflow.working.WorkingLoginSharedPreference
import com.example.kurlyflow.databinding.ActivityEndBinding
import com.example.kurlyflow.hr.worker.model.MyPageModel
import com.example.kurlyflow.hr.worker.service.WorkerCallService
import com.example.kurlyflow.hr.worker.service.WorkerMyPageService
import com.example.kurlyflow.working.end.model.BasketModel
import com.example.kurlyflow.working.end.model.EndProductModel
import com.example.kurlyflow.working.end.service.EndService
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EndActivity : AppCompatActivity() {
    lateinit var binding: ActivityEndBinding
    lateinit var baskets: ArrayList<BasketModel>
    lateinit var products: ArrayList<EndProductModel>
    lateinit var worker: MyPageModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEndBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadData()
        initListener()
        initToolbar()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadData() {
        WorkerMyPageService.getWorkerMyPage(
            WorkingLoginSharedPreference.getUserAccessToken(
                "end",
                this
            )
        ).enqueue(object :
            Callback<MyPageModel> {

            override fun onResponse(call: Call<MyPageModel>, response: Response<MyPageModel>) {
                Log.d("TAGm", response.body().toString() + response.code())
                if (response.code() == 200) {
                    worker = response.body()!!
                    binding.textviewEndName.text = worker.name
                    binding.textviewEndRegion.text =
                        worker.region + " " + worker.detailRegion
                }
            }

            override fun onFailure(call: Call<MyPageModel>, t: Throwable) {
                Log.d("TAGme", t.localizedMessage)
            }

        })

        EndService.getBasketList(WorkingLoginSharedPreference.getUserAccessToken("end", this))
            .enqueue(object : Callback<List<BasketModel>> {
                override fun onResponse(
                    call: Call<List<BasketModel>>,
                    response: Response<List<BasketModel>>
                ) {
                    Log.d("TAGb", response.body()!!.toString() + response.code())
                    if (response.code() == 200) {
                        val jsonArray = response.body()!!
                        baskets = arrayListOf()
                        for (data in jsonArray) {
                            var endAt = data.endAt.toString().replace("T", " ")
                            val basket =
                                BasketModel(data.basketId.toLong(), data.invoiceId, endAt)
                            baskets.add(basket)
                        }
                        initBasketRecyclerView()
                    } else {
                        Toast.makeText(applicationContext, "목록 조회를 실패했습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<List<BasketModel>>, t: Throwable) {
                    Log.d("Tagbe", t.localizedMessage)
                    Toast.makeText(applicationContext, "목록 조회를 실패했습니다.", Toast.LENGTH_SHORT)
                        .show()
                }

            })
    }

    private fun initBasketRecyclerView() {
        binding.recyclerviewEndPrinting.apply {
            layoutManager =
                LinearLayoutManager(context)
            adapter = BasketRecyclerViewAdapter(
                context,
                baskets
            ).apply {
                setOnItemClickListener(object :
                    BasketRecyclerViewAdapter.OnItemClickListener {
                    override fun onItemClick(v: View, item: BasketModel) {
                        binding.textviewEndSelected.text =
                            "선택된 호수 - 송장 번호: " + item.basketId + " - " + item.invoiceId

                        EndService.getProductList(
                            WorkingLoginSharedPreference.getUserAccessToken(
                                "end",
                                applicationContext,
                            ),
                            item.invoiceId.toString()
                        ).enqueue(object : Callback<ArrayList<EndProductModel>> {
                            override fun onResponse(
                                call: Call<ArrayList<EndProductModel>>,
                                response: Response<ArrayList<EndProductModel>>
                            ) {
                                Log.d("TAGp", response.body().toString() + response.code())
                                if (response.code() == 200) {
                                    products = arrayListOf()
                                    products = response.body()!!
                                    initEndProductRecyclerView()
                                    binding.linearlayoutEndProducts.visibility = View.VISIBLE
                                } else {
                                    Toast.makeText(
                                        applicationContext,
                                        "목록 조회를 실패했습니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onFailure(
                                call: Call<ArrayList<EndProductModel>>,
                                t: Throwable
                            ) {
                                Log.d("TAGpe", t.localizedMessage)
                                Toast.makeText(
                                    applicationContext,
                                    "목록 조회를 실패했습니다.",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }


                        })
                    }
                })
            }
        }
    }

    private fun initEndProductRecyclerView() {
        binding.recyclerviewEndProductlist.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = EndProductRecyclerViewAdapter(context, products)
        }
    }

    private fun initListener() {
        val builder = AlertDialog.Builder(this)
        binding.buttonEndPrintinvoice.setOnClickListener {
            builder.setMessage("선택하신 송장을 재출력 하시겠습니까?")
                .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, _ ->
                    Toast.makeText(applicationContext, "재출력을 시작합니다.", Toast.LENGTH_SHORT).show()
                    dialog.cancel()
                })
                .setNegativeButton("취소", DialogInterface.OnClickListener { dialog, _ ->
                    dialog.cancel()
                })
            builder.show()
        }
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbarEnd)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarEnd.title = "KurlyFlow"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.end_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val builder = AlertDialog.Builder(this)
        when (item?.itemId) {
            R.id.logout -> {
                builder.setMessage("로그아웃 하시겠습니까?")
                    .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, _ ->
                        Toast.makeText(applicationContext, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                        WorkingLoginSharedPreference.clearUserAccessToken("end", applicationContext)
                        startActivity(Intent(applicationContext, EndLoginActivity::class.java))
                        finish()
                    })
                    .setNegativeButton("취소", DialogInterface.OnClickListener { dialog, _ ->
                        dialog.cancel()
                    })
                builder.show()
                return super.onOptionsItemSelected(item)
            }
            R.id.request -> {
                builder.setMessage("상자 적재를 요청하시겠습니까?")
                    .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, _ ->
                        Toast.makeText(applicationContext, "요청 되었습니다.", Toast.LENGTH_SHORT).show()
                        dialog.cancel()
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
                                            "end",
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