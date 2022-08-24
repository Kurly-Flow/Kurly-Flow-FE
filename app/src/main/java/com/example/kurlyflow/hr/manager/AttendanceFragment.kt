package com.example.kurlyflow.hr.manager

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kurlyflow.R
import com.example.kurlyflow.databinding.FragmentAttendanceBinding
import com.example.kurlyflow.hr.manager.model.AttendanceModel
import com.example.kurlyflow.hr.manager.service.ManagerService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AttendanceFragment : Fragment(R.layout.fragment_attendance) {
    lateinit var binding: FragmentAttendanceBinding
    lateinit var workers: ArrayList<AttendanceModel>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAttendanceBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
        initListener()
    }

    private fun loadData() {
        binding.textviewAttendanceName.text =
            ManagerLoginSharedPreference.getUserName(requireContext())
        binding.textviewAttendanceRegion.text =
            ManagerLoginSharedPreference.getUserRegion(requireContext()) + " 관리자"
        ManagerService.getWorkerAttendance(
            ManagerLoginSharedPreference.getUserAccessToken(
                requireContext()
            )
        ).enqueue(object :
            Callback<ArrayList<AttendanceModel>> {
            override fun onResponse(
                call: Call<ArrayList<AttendanceModel>>,
                response: Response<ArrayList<AttendanceModel>>
            ) {
                Log.d("TAGa", response.body().toString() + response.code())
                if (response.code() == 200) {
                    workers = arrayListOf()
                    workers = response.body()!!
                    initRecyclerView()
                } else {
                    Toast.makeText(requireContext(), "목록 조회에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ArrayList<AttendanceModel>>, t: Throwable) {
                Log.d("TAGa", t.localizedMessage)
                Toast.makeText(requireContext(), "목록 조회에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun initRecyclerView() {
        binding.recyclerviewAttendance.apply {
            layoutManager =
                LinearLayoutManager(requireContext())
            adapter = AttendanceRecyclerViewAdapter(
                requireContext(),
                workers
            )
        }
    }

    private fun initListener() {
        binding.toolbarAttendance.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.logout -> {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setMessage("로그아웃 하시겠습니까?")
                        .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, _ ->
                            Toast.makeText(requireContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT)
                                .show()
                            ManagerLoginSharedPreference.clearUserInfo(requireContext())
                            startActivity(
                                Intent(
                                    requireContext(),
                                    ManagerLoginActivity::class.java
                                )
                            )
                            requireActivity().finish()
                        })
                        .setNegativeButton("취소", DialogInterface.OnClickListener { dialog, _ ->
                            dialog.cancel()
                        })
                    builder.show()
                    true
                }
                R.id.refresh -> {
                    refresh()
                    true
                }
                else -> false
            }
        }

        binding.buttonAttendanceSubmit.setOnClickListener {
            ManagerService.requestDetailRegionAssignment(
                ManagerLoginSharedPreference.getUserAccessToken(
                    requireContext()
                )
            ).enqueue(object : Callback<String>{
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    Log.d("TAGdra", response.code().toString())
                    if(response.code() == 200){
                        Toast.makeText(requireContext(), "세부권역 배정이 완료 되었습니다", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(requireContext(), "세부권역 배정에 실패했습니다", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d("TAGdra", t.localizedMessage)
                    Toast.makeText(requireContext(), "세부권역 배정에 실패했습니다", Toast.LENGTH_SHORT).show()
                }

            })
        }
    }

    private fun refresh() {
        val fragmentManager = parentFragmentManager.beginTransaction()
        fragmentManager.detach(this).attach(this).commit()
    }
}