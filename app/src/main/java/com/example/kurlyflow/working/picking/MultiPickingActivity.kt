package com.example.kurlyflow.working.picking

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kurlyflow.R
import com.example.kurlyflow.databinding.ActivityMultipickingBinding
import com.example.kurlyflow.hr.worker.WorkerLoginSharedPreference
import com.example.kurlyflow.hr.worker.model.MyPageModel
import com.example.kurlyflow.hr.worker.service.WorkerCallService
import com.example.kurlyflow.hr.worker.service.WorkerMyPageService
import com.example.kurlyflow.working.WorkingLoginSharedPreference
import com.example.kurlyflow.working.end.EndLoginActivity
import com.example.kurlyflow.working.picking.model.PickingModel
import com.example.kurlyflow.working.picking.model.PickingProductModel
import com.example.kurlyflow.working.picking.request.MoveProductsToNewToteRequest
import com.example.kurlyflow.working.picking.service.PickingService
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MultiPickingActivity : AppCompatActivity() {
    lateinit var binding: ActivityMultipickingBinding
    lateinit var worker: MyPageModel
    lateinit var batches: ArrayList<PickingProductModel>
    lateinit var pickingModel: PickingModel
    private var inToteNameList: ArrayList<String> = arrayListOf()
    private var inToteIdList: ArrayList<String> = arrayListOf()
    private var moveIdList: ArrayList<String> = arrayListOf()
    private var nowCount: Int = 0
    private var invoiceCount = 0
    private var totalCount = 0
    private var totalWeight: Double = 0.0
    private var toteId = ""
    private var weightFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMultipickingBinding.inflate(layoutInflater)
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
                    binding.textviewMultipickingName.text = worker.name
                    binding.textviewMultipickingDetailregion.text =
                        worker.region + " " + worker.detailRegion
                }
            }

            override fun onFailure(call: Call<MyPageModel>, t: Throwable) {
                Log.d("TAGme", t.localizedMessage)
            }

        })

        PickingService.getMultiPickingList(
            WorkingLoginSharedPreference.getUserAccessToken(
                "picking",
                this
            )
        ).enqueue(object : Callback<PickingModel> {
            override fun onResponse(call: Call<PickingModel>, response: Response<PickingModel>) {
                Log.d("TAGmp", response.body().toString() + response.code())
                if (response.code() == 200) {
                    pickingModel = response.body()!!
                    batches = arrayListOf()
                    if (pickingModel.invoiceProductResponses.isNotEmpty()) {
                        batches = pickingModel.invoiceProductResponses
                        initRecyclerView()
                        setView(0)
                    }
                    binding.textviewMultipickingRecommendtotecount.text =
                        "?????? ?????? ??????: " + pickingModel.recommendToteCount

                }
            }

            override fun onFailure(call: Call<PickingModel>, t: Throwable) {
                Log.d("TAGmpe", t.localizedMessage)
            }

        })
    }

    private fun setView(position: Int) {
        binding.textviewMultipickingLocate.text = "??????: " + batches[position].region
        binding.textviewMultipickingDetaillocate.text = "??????: " + batches[position].location
        Glide.with(applicationContext).load(batches[position].imageUrl)
            .into(binding.imageviewMultipickingImage)
        binding.textviewMultipickingTitle.text = "??????: " + batches[position].name
        binding.textviewMultipickingCount.text =
            "??????: $nowCount/" + batches[position].quantity + " EA"
        if (position < batches.size - 1)
            binding.textviewMultipickingNext.text = "?????? ??????: " + batches[position + 1].name
        else
            binding.textviewMultipickingNext.text = "?????? ??????: ??????"
    }

    private fun initListener() {
        binding.buttonMultipickingNewtote.setOnClickListener {
            val dialog = CustomDialog(this)
            dialog.showDialog()
            dialog.setOnClickListener(object : CustomDialog.OnDialogClickListener {
                override fun onClicked(toteId: String) {
                    this@MultiPickingActivity.toteId = toteId
                    binding.textviewMultipickingTote.text = "?????? ??????: $toteId"
                    totalWeight = 0.0
                    weightFlag = false
                    inToteIdList.clear()
                    inToteNameList.clear()
                }

            })
        }

        binding.buttonMultipickingStop.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("????????? ?????????????????????????")
                .setPositiveButton("??????", DialogInterface.OnClickListener { _, _ ->
                    startActivity(
                        Intent(
                            applicationContext,
                            ChoosePickingActivity::class.java
                        )
                    )
                    Toast.makeText(applicationContext, "????????? ??????????????????.", Toast.LENGTH_SHORT).show()
                    finish()
                })
                .setNegativeButton("??????", DialogInterface.OnClickListener { dialog, _ ->
                    dialog.dismiss()
                })
            builder.show()
        }

        binding.buttonMultipickingBarcode.setOnClickListener {
            if (toteId == "") {
                Toast.makeText(this, "????????? ?????? ??????????????????", Toast.LENGTH_SHORT).show()
            } else {
                if (nowCount == 0 && totalCount <= 30) {
                    val invoiceProductId = batches[invoiceCount].invoiceProductId
                    PickingService.saveBarcode(
                        WorkingLoginSharedPreference.getUserAccessToken(
                            "picking",
                            this
                        ), invoiceProductId, toteId
                    ).enqueue(object : Callback<String> {
                        override fun onResponse(call: Call<String>, response: Response<String>) {
                            Log.d("TAGb", response.body() + response.code())
                            if (response.code() == 200) {
                                barcodeProcess()
                            }
                        }

                        override fun onFailure(call: Call<String>, t: Throwable) {
                            Log.d("TAGb", t.localizedMessage)
                            Toast.makeText(
                                this@MultiPickingActivity,
                                "?????? ???????????? ???????????????.",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }

                    })
                } else {
                    barcodeProcess()
                }
            }
        }

        binding.buttonMultipickingMovetote.setOnClickListener {
            if (inToteNameList.isEmpty()) {
                Toast.makeText(this, "?????? ????????? ?????? ??? ????????????", Toast.LENGTH_SHORT).show()
            } else {
                var data = inToteNameList.toArray(arrayOfNulls<String>(inToteNameList.size))
                val selectedItemIndex = ArrayList<Int>()
                val builder = AlertDialog.Builder(this)
                builder.setTitle("?????? ????????? ?????? ?????? ??? ????????? ???????????????")
                    .setMultiChoiceItems(
                        data,
                        null,
                    ) { dialogInterface: DialogInterface, i: Int, b: Boolean ->
                        if (b) {
                            selectedItemIndex.add(i)
                        } else if (selectedItemIndex.contains(i)) {
                            selectedItemIndex.remove(i)
                        }
                    }
                    .setPositiveButton("??????") { dialogInterface: DialogInterface, i: Int ->
                        for (j in selectedItemIndex) {
                            moveIdList.add(inToteIdList[j])
                        }
                        dialogInterface.dismiss()
                        var oldToteId = toteId
                        val dialog = CustomDialog2(this)
                        dialog.showDialog()
                        dialog.setOnClickListener(object : CustomDialog2.OnDialogClickListener {
                            override fun onClicked(toteId: String) {
                                this@MultiPickingActivity.toteId = toteId
                                val request =
                                    MoveProductsToNewToteRequest(pickingModel.batchId, oldToteId, toteId, moveIdList)
                                Log.d("TAGG", request.toString())
                                PickingService.moveProductsToNewToteRequest(
                                    WorkingLoginSharedPreference.getUserAccessToken(
                                        "picking",
                                        applicationContext
                                    ),
                                    request
                                ).enqueue(object : Callback<String> {
                                    override fun onResponse(
                                        call: Call<String>,
                                        response: Response<String>
                                    ) {
                                        Log.d("TAGmt", response.code().toString())
                                        if (response.code() == 200) {
                                            Toast.makeText(
                                                applicationContext,
                                                "?????? ??????????????? ??????????????????.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            binding.textviewMultipickingTote.text = "?????? ??????: $toteId"
                                        } else {
                                            Toast.makeText(
                                                applicationContext,
                                                "?????? ??????????????? ??????????????????.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }

                                    override fun onFailure(call: Call<String>, t: Throwable) {
                                        Log.d("TAGmt", t.localizedMessage)
                                        Toast.makeText(
                                            applicationContext,
                                            "?????? ??????????????? ??????????????????.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                })
                            }
                        })
                    }
                    .setNegativeButton("??????") { dialogInterface: DialogInterface, i: Int ->
                        dialogInterface.dismiss()
                    }
                    .show()

            }
        }
    }

    private fun barcodeProcess() {
        val quantity: Int = batches[invoiceCount].quantity.toInt()
        nowCount += 1
        totalCount += 1
        binding.textviewMultipickingCount.text = "$nowCount/$quantity EA"
        if (nowCount == quantity && totalCount < 30) {
            invoiceCount += 1
            Handler(Looper.getMainLooper()).postDelayed({
                nowCount = 0
                setView(invoiceCount)
            }, 300)
        }
        if (totalCount == 30) {
            val builder = AlertDialog.Builder(this@MultiPickingActivity)
            builder.setMessage("????????? ?????? ??????????????????.\n????????????????????? ????????? ????????? ???, ?????? ????????? ????????????.")
                .setPositiveButton(
                    "?????? ??????",
                    DialogInterface.OnClickListener { _, _ ->
                        startActivity(
                            Intent(
                                applicationContext,
                                ChoosePickingActivity::class.java
                            )
                        )
                        finish()
                    })
            builder.show()
        }
        val weight = batches[invoiceCount].weight
        totalWeight += weight
        if (totalWeight >= 8000 && !weightFlag) {
            val builder = AlertDialog.Builder(this@MultiPickingActivity)
            builder.setMessage("????????? 8kg??? ??????????????????.\n??? ????????? ?????????????????????????")
                .setPositiveButton(
                    "???",
                    DialogInterface.OnClickListener { dialogInterface, _ ->
                        weightFlag = false
                        dialogInterface.cancel()
                        val dialog = CustomDialog(this)
                        dialog.showDialog()
                        dialog.setOnClickListener(object : CustomDialog.OnDialogClickListener {
                            override fun onClicked(toteId: String) {
                                this@MultiPickingActivity.toteId = toteId
                                binding.textviewMultipickingTote.text = "?????? ??????: $toteId"
                                totalWeight = 0.0
                                inToteIdList.clear()
                                inToteNameList.clear()
                            }
                        })
                    })
                .setNegativeButton(
                    "?????????",
                    DialogInterface.OnClickListener { dialogInterface, _ ->
                        weightFlag = true
                        dialogInterface.cancel()
                    })
            builder.show()
        }
        inToteNameList.add(batches[invoiceCount].name)
        inToteIdList.add(batches[invoiceCount].invoiceProductId)
        binding.progressbarMultipicking.progress = totalCount
        binding.textviewMultipickingProgress.text = "$totalCount/30"
    }

    private fun initRecyclerView() {
        binding.recyclerviewMultipicking.apply {
            layoutManager =
                LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = MultiPickingRecyclerViewAdapter(
                context,
                batches
            )
        }
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbarMultipicking)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarMultipicking.title = "KurlyFlow"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.picking_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val builder = AlertDialog.Builder(this)
        when (item?.itemId) {
            R.id.home -> {
                startActivity(Intent(this, ChoosePickingActivity::class.java))
                finish()
                return super.onOptionsItemSelected(item)
            }
            R.id.logout -> {
                builder.setMessage("???????????? ???????????????????")
                    .setPositiveButton("??????", DialogInterface.OnClickListener { dialog, _ ->
                        Toast.makeText(applicationContext, "???????????? ???????????????.", Toast.LENGTH_SHORT).show()
                        WorkerLoginSharedPreference.clearUserAccessToken(applicationContext)
                        startActivity(Intent(applicationContext, PickingLoginActivity::class.java))
                        finish()
                    })
                    .setNegativeButton("??????", DialogInterface.OnClickListener { dialog, _ ->
                        dialog.cancel()
                    })
                builder.show()
                return super.onOptionsItemSelected(item)
            }
            R.id.call -> {
                builder.setMessage("???????????? ?????? ???????????????????")
                    .setPositiveButton("??????", DialogInterface.OnClickListener { dialog, _ ->
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
                                                    "???????????? ?????? ????????????",
                                                    Toast.LENGTH_SHORT
                                                )
                                                    .show()

                                            } else {
                                                Toast.makeText(
                                                    applicationContext,
                                                    "????????? ????????? ??????????????????",
                                                    Toast.LENGTH_SHORT
                                                )
                                                    .show()
                                            }
                                        }

                                        override fun onFailure(call: Call<String>, t: Throwable) {
                                            Log.d("TAGc", t.localizedMessage)
                                            Toast.makeText(
                                                applicationContext,
                                                "????????? ????????? ??????????????????",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                        }

                                    })
                                } else {
                                    Toast.makeText(this, "?????? ????????? ??????????????????", Toast.LENGTH_SHORT)
                                        .show()
                                }
//                                // Log and toast
//                                val msg = getString(R.string.msg_token_fmt, token)
//                                Log.d("TAGfcm", msg)

                            })
                        dialog.cancel()
                    })
                    .setNegativeButton("??????", DialogInterface.OnClickListener { dialog, _ ->
                        dialog.cancel()
                    })
                builder.show()
                return super.onOptionsItemSelected(item)
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}