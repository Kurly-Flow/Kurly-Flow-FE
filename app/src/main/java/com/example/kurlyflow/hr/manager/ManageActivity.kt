package com.example.kurlyflow.hr.manager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.kurlyflow.R
import com.example.kurlyflow.databinding.ActivityManageBinding

class ManageActivity : AppCompatActivity() {
    lateinit var binding : ActivityManageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        initBottomNavigation()
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.framelayout_manage_fragment, fragment).commit()
    }

    private fun initBottomNavigation() {
        binding.bottomnavigationviewManage.run {
            setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.home -> {
                        replaceFragment(HomeFragment())
                    }
                    R.id.attendance -> {
                        replaceFragment(AttendanceFragment())
                    }
                    R.id.detail_region -> {
                        replaceFragment(DetailRegionFragment())
                    }
                    R.id.to -> {
                        replaceFragment(ToFragment())
                    }
                }
                true
            }
            selectedItemId = R.id.home
        }
    }
}