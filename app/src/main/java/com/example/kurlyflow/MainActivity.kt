package com.example.kurlyflow

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kurlyflow.databinding.ActivityMainBinding
import com.example.kurlyflow.hr.manager.ChooseRegionActivity
import com.example.kurlyflow.hr.manager.ManagerLoginActivity
import com.example.kurlyflow.hr.manager.ManagerLoginSharedPreference
import com.example.kurlyflow.hr.worker.WorkerLoginActivity
import com.example.kurlyflow.hr.worker.WorkerLoginSharedPreference
import com.example.kurlyflow.hr.worker.WorkerMyPageActivity
import com.example.kurlyflow.working.WorkingLoginSharedPreference
import com.example.kurlyflow.working.end.EndActivity
import com.example.kurlyflow.working.end.EndLoginActivity
import com.example.kurlyflow.working.packing.CheckProductActivity
import com.example.kurlyflow.working.packing.PackingLoginActivity
import com.example.kurlyflow.working.picking.ChoosePickingActivity
import com.example.kurlyflow.working.picking.PickingLoginActivity

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonMainManager.setOnClickListener {
            if (ManagerLoginSharedPreference.getUserAccessToken(applicationContext).isNotEmpty()) {
                startActivity(Intent(this, ChooseRegionActivity::class.java))
            } else {
                startActivity(Intent(this, ManagerLoginActivity::class.java))
            }
        }
        binding.buttonMainWorker.setOnClickListener {
            if (WorkerLoginSharedPreference.getUserAccessToken(applicationContext).isNotEmpty()) {
                startActivity(Intent(this, WorkerMyPageActivity::class.java))
            } else {
                startActivity(Intent(this, WorkerLoginActivity::class.java))
            }
        }
        binding.buttonMainPicking.setOnClickListener {
            if (WorkingLoginSharedPreference.getUserAccessToken("picking", applicationContext)
                    .isNotEmpty()
            ) {
                startActivity(Intent(this, ChoosePickingActivity::class.java))
            } else {
                startActivity(Intent(this, PickingLoginActivity::class.java))
            }

        }
        binding.buttonMainEnd.setOnClickListener {
            if (WorkingLoginSharedPreference.getUserAccessToken("end", applicationContext)
                    .isNotEmpty()
            ) {
                startActivity(Intent(this, EndActivity::class.java))
            } else {
                startActivity(Intent(this, EndLoginActivity::class.java))
            }
        }
        binding.buttonMainPacking.setOnClickListener {
            if (WorkingLoginSharedPreference.getUserAccessToken("packing", applicationContext)
                    .isNotEmpty()
            ) {
                startActivity(Intent(this, CheckProductActivity::class.java))
            } else {
                startActivity(Intent(this, PackingLoginActivity::class.java))
            }

        }
    }
}