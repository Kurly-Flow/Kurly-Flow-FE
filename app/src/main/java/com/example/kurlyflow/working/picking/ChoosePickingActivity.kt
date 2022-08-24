package com.example.kurlyflow.working.picking

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
import com.example.kurlyflow.hr.worker.WorkerLoginSharedPreference
import com.example.kurlyflow.working.WorkingLoginSharedPreference
import com.example.kurlyflow.databinding.ActivityChoosepickingBinding
import com.example.kurlyflow.hr.worker.model.MyPageModel
import com.example.kurlyflow.hr.worker.service.WorkerCallService
import com.example.kurlyflow.hr.worker.service.WorkerMyPageService
import com.example.kurlyflow.working.end.EndLoginActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChoosePickingActivity : AppCompatActivity() {
    lateinit var binding: ActivityChoosepickingBinding
    lateinit var worker: MyPageModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChoosepickingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadData()
        initListener()
        initToolbar()
    }

    private fun loadData() {
        WorkerMyPageService.getWorkerMyPage(
            WorkingLoginSharedPreference.getUserAccessToken(
                "picking",
                this
            )
        ).enqueue(object :
            Callback<MyPageModel> {

            override fun onResponse(call: Call<MyPageModel>, response: Response<MyPageModel>) {
                Log.d("TAGm", response.body().toString() + response.code())
                if (response.code() == 200) {
                    worker = response.body()!!
                    binding.textviewChoosepickingName.text = worker.name
                    binding.textviewChoosepickingDetailregion.text =
                        worker.region + " " + worker.detailRegion
                }
            }

            override fun onFailure(call: Call<MyPageModel>, t: Throwable) {
                Log.d("TAGme", t.localizedMessage)
            }

        })
    }

    private fun initListener() {
        binding.buttonChoosepickingMulti.setOnClickListener {
            startActivity(Intent(this, MultiPickingActivity::class.java))
        }
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbarChoosepicking)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarChoosepicking.title = "KurlyFlow"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.picking_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val builder = AlertDialog.Builder(this)
        when (item?.itemId) {
            R.id.home -> {
                //startActivity(Intent(this, ChoosePickingActivity::class.java))
                return super.onOptionsItemSelected(item)
            }
            R.id.logout -> {
                builder.setMessage("로그아웃 하시겠습니까?")
                    .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, _ ->
                        Toast.makeText(applicationContext, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                        WorkerLoginSharedPreference.clearUserAccessToken(applicationContext)
                        startActivity(Intent(applicationContext, PickingLoginActivity::class.java))
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
                                            "picking",
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