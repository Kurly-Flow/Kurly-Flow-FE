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
import com.example.kurlyflow.databinding.FragmentHomeBinding
import com.example.kurlyflow.hr.manager.model.HomeWorkerModel
import com.example.kurlyflow.hr.manager.service.ManagerService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment(R.layout.fragment_home) {
    lateinit var binding: FragmentHomeBinding
    lateinit var workers: ArrayList<HomeWorkerModel>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadData()
        initListener()
    }

    private fun loadData() {
        binding.textviewHomeName.text = ManagerLoginSharedPreference.getUserName(requireContext())
        binding.textviewHomeRegion.text =
            ManagerLoginSharedPreference.getUserRegion(requireContext()) + " 관리자"

        ManagerService.getHomeWorkers(ManagerLoginSharedPreference.getUserAccessToken(requireContext()))
            .enqueue(object : Callback<List<HomeWorkerModel>> {
                override fun onResponse(
                    call: Call<List<HomeWorkerModel>>,
                    response: Response<List<HomeWorkerModel>>
                ) {
                    Log.d("TAGw", response.body().toString() + response.code())
                    if (response.code() == 200) {
                        val jsonArray = response.body()!!
                        workers = arrayListOf()
                        for (data in jsonArray) {
                            var detailRegion = data.detailRegion
                            var name = data.name
                            var employeeNumber = data.employeeNumber
                            if (detailRegion.isNullOrBlank())
                                detailRegion = ""
                            if (employeeNumber.isNullOrBlank())
                                employeeNumber = ""
                            val worker =
                                HomeWorkerModel(detailRegion, name, employeeNumber)
                            workers.add(worker)
                        }
                        initRecyclerView()
                    }
                    else{
                        Toast.makeText(requireContext(), "목록을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<HomeWorkerModel>>, t: Throwable) {
                    Log.d("TAGwe", t.localizedMessage)
                    Toast.makeText(requireContext(), "목록을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun initRecyclerView() {
        binding.recyclerviewHome.apply {
            layoutManager =
                LinearLayoutManager(requireContext())
            adapter = ManageRecyclerViewAdapter(
                requireContext(),
                workers
            )
        }
    }

    private fun initListener() {
        binding.toolbarHome.setOnMenuItemClickListener {
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
    }

    private fun refresh() {
        val fragmentManager = parentFragmentManager.beginTransaction()
        fragmentManager.detach(this).attach(this).commit()
    }
}