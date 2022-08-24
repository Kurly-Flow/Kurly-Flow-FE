package com.example.kurlyflow.hr.manager

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
import com.example.kurlyflow.databinding.ActivityChooseregionBinding
import com.example.kurlyflow.hr.manager.model.RegionModel
import com.example.kurlyflow.hr.manager.service.ManagerService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChooseRegionActivity : AppCompatActivity() {
    lateinit var binding: ActivityChooseregionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseregionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initToolbar()
        binding.textviewChoosepickingName.text = ManagerLoginSharedPreference.getUserName(this)
        binding.buttonChooseregionPicking.setOnClickListener {
            postRegion("PICKING")
        }
        binding.buttonChooseregionQps.setOnClickListener {
            postRegion("QPS")
        }
        binding.buttonChooseregionEnd.setOnClickListener {
            postRegion("END")
        }
        binding.buttonChooseregionPacking.setOnClickListener {
            postRegion("PACKING")
        }
    }

    private fun postRegion(region: String) {
        val regionModel = RegionModel(region)
        ManagerService.requestChooseRegion(
            ManagerLoginSharedPreference.getUserAccessToken(this),
            regionModel
        ).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.d("TAGrrr", response.body() + response.code())
                if (response.code() == 200) {
                    ManagerLoginSharedPreference.setUserRegion(applicationContext, region)
                    startActivity(Intent(applicationContext, ManageActivity::class.java))
                } else {
                    Toast.makeText(applicationContext, "근무지 지정에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("TAGre", t.localizedMessage)
                Toast.makeText(applicationContext, "근무지 지정에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbarChooseregion)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbarChooseregion.title = "KurlyFlow"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.manage_toolbar, menu)
        menu?.findItem(R.id.refresh)?.isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val builder = AlertDialog.Builder(this)
        when (item?.itemId) {
            R.id.logout -> {
                builder.setMessage("로그아웃 하시겠습니까?")
                    .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, _ ->
                        Toast.makeText(applicationContext, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                        ManagerLoginSharedPreference.clearUserInfo(applicationContext)
                        startActivity(Intent(applicationContext, ManagerLoginActivity::class.java))
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
}